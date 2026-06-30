import { create } from "zustand"
import { client } from "../lib/api/client"
import type { MeResponse } from "../lib/api/types"
import {
  clearSession,
  getRefreshToken,
  hasSession,
  setTokens,
} from "../lib/auth/session"
import { userHasPermission } from "../lib/auth/permissions"

export type AuthStatus = "loading" | "authenticated" | "unauthenticated"

interface AuthState {
  user: MeResponse | null
  status: AuthStatus
  hydrate: () => Promise<void>
  login: (identifier: string, password: string) => Promise<void>
  register: (email: string, username: string, password: string) => Promise<void>
  logout: () => Promise<void>
  syncFromSession: () => void
  hasPermission: (permission: string) => boolean
}

/** Turns a backend error body (ProblemDetail) into a user-facing message. */
function messageFrom(error: unknown, fallback: string): string {
  if (error && typeof error === "object" && "detail" in error) {
    const detail = (error as { detail?: unknown }).detail
    if (typeof detail === "string" && detail.length > 0) {
      return detail
    }
  }
  return fallback
}

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  status: "loading",

  async hydrate() {
    if (!hasSession()) {
      set({ user: null, status: "unauthenticated" })
      return
    }
    set({ status: "loading" })
    const { data, error } = await client.GET("/api/auth/me")
    if (error || !data) {
      clearSession()
      set({ user: null, status: "unauthenticated" })
      return
    }
    set({ user: data, status: "authenticated" })
  },

  async login(identifier, password) {
    const { data, error } = await client.POST("/api/auth/login", {
      body: { identifier, password },
    })
    if (error || !data) {
      throw new Error(messageFrom(error, "Credenciales inválidas"))
    }
    setTokens(data.accessToken, data.refreshToken)
    const me = await client.GET("/api/auth/me")
    if (me.error || !me.data) {
      clearSession()
      throw new Error("No se pudo cargar la sesión")
    }
    set({ user: me.data, status: "authenticated" })
  },

  async register(email, username, password) {
    const { data, error } = await client.POST("/api/auth/register", {
      body: { email, username, password },
    })
    if (error || !data) {
      throw new Error(messageFrom(error, "No se pudo completar el registro"))
    }
    await get().login(username, password)
  },

  async logout() {
    const refreshToken = getRefreshToken()
    if (refreshToken) {
      // Best-effort backend revocation; local state is cleared regardless.
      try {
        await client.POST("/api/auth/logout", { body: { refreshToken } })
      } catch {
        // ignore network/abort errors during logout
      }
    }
    clearSession()
    set({ user: null, status: "unauthenticated" })
  },

  syncFromSession() {
    if (!hasSession()) {
      set({ user: null, status: "unauthenticated" })
    }
  },

  hasPermission(permission) {
    return userHasPermission(get().user, permission)
  },
}))
