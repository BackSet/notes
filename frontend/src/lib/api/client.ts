import createClient, { type Middleware } from "openapi-fetch"
import {
  clearSession,
  getAccessToken,
  getRefreshToken,
  setTokens,
} from "../auth/session"
import type {
  LoginRequest,
  LogoutRequest,
  MeResponse,
  PermissionResponse,
  RefreshRequest,
  RegisterRequest,
  RoleResponse,
  TokenResponse,
  UpdateUserRequest,
  UserSummaryResponse,
} from "./types"

type Json<T> = { content: { "application/json": T } }

// Typed subset of the backend OpenAPI contract.
export interface paths {
  "/api/auth/register": {
    post: { requestBody: Json<RegisterRequest>; responses: { 201: Json<MeResponse> } }
  }
  "/api/auth/login": {
    post: { requestBody: Json<LoginRequest>; responses: { 200: Json<TokenResponse> } }
  }
  "/api/auth/refresh": {
    post: { requestBody: Json<RefreshRequest>; responses: { 200: Json<TokenResponse> } }
  }
  "/api/auth/logout": {
    post: { requestBody: Json<LogoutRequest>; responses: { 204: { content?: never } } }
  }
  "/api/auth/logout-all": {
    post: { responses: { 204: { content?: never } } }
  }
  "/api/auth/me": {
    get: { responses: { 200: Json<MeResponse> } }
  }
  "/api/admin/users": {
    get: { responses: { 200: Json<UserSummaryResponse[]> } }
  }
  "/api/admin/users/{id}": {
    get: { parameters: { path: { id: number } }; responses: { 200: Json<UserSummaryResponse> } }
    patch: {
      parameters: { path: { id: number } }
      requestBody: Json<UpdateUserRequest>
      responses: { 200: Json<UserSummaryResponse> }
    }
  }
  "/api/admin/users/{id}/disable": {
    post: { parameters: { path: { id: number } }; responses: { 200: Json<UserSummaryResponse> } }
  }
  "/api/admin/users/{id}/enable": {
    post: { parameters: { path: { id: number } }; responses: { 200: Json<UserSummaryResponse> } }
  }
  "/api/admin/roles": {
    get: { responses: { 200: Json<RoleResponse[]> } }
  }
  "/api/admin/permissions": {
    get: { responses: { 200: Json<PermissionResponse[]> } }
  }
}

export const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"

export const client = createClient<paths>({ baseUrl: baseURL })

// Auth endpoints must not trigger the refresh/retry loop.
const AUTH_PATHS = ["/api/auth/login", "/api/auth/register", "/api/auth/refresh"]

let refreshInFlight: Promise<boolean> | null = null

/** Refreshes the access token once, coalescing concurrent callers. */
function refreshAccessToken(): Promise<boolean> {
  if (refreshInFlight) {
    return refreshInFlight
  }
  const refreshToken = getRefreshToken()
  if (!refreshToken) {
    return Promise.resolve(false)
  }
  refreshInFlight = (async () => {
    try {
      const response = await fetch(`${baseURL}/api/auth/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken } satisfies RefreshRequest),
      })
      if (!response.ok) {
        return false
      }
      const tokens = (await response.json()) as TokenResponse
      setTokens(tokens.accessToken, tokens.refreshToken)
      return true
    } catch {
      return false
    }
  })()
  refreshInFlight.finally(() => {
    refreshInFlight = null
  })
  return refreshInFlight
}

const authMiddleware: Middleware = {
  onRequest({ request }) {
    const token = getAccessToken()
    if (token) {
      request.headers.set("Authorization", `Bearer ${token}`)
    }
    return request
  },
  async onResponse({ request, response }) {
    if (response.status !== 401) {
      return response
    }
    const path = new URL(request.url).pathname
    if (AUTH_PATHS.some((authPath) => path.endsWith(authPath))) {
      return response
    }

    const refreshed = await refreshAccessToken()
    if (!refreshed) {
      clearSession()
      return response
    }

    // Retry once with the new token. Cloning is only safe when the body was not
    // consumed (GET and body-less POST); otherwise surface the original 401.
    if (request.bodyUsed) {
      return response
    }
    const retried = request.clone()
    const token = getAccessToken()
    if (token) {
      retried.headers.set("Authorization", `Bearer ${token}`)
    }
    return fetch(retried)
  },
}

client.use(authMiddleware)
