// Hand-written subset of the backend contract consumed by the frontend.
// Will be replaced by an openapi-typescript-generated file later.

export interface RegisterRequest {
  email: string
  username: string
  password: string
}

export interface LoginRequest {
  identifier: string
  password: string
}

export interface RefreshRequest {
  refreshToken: string
}

export interface LogoutRequest {
  refreshToken: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresInMs: number
}

export interface MeResponse {
  id: number
  username: string
  email: string
  enabled: boolean
  roles: string[]
  permissions: string[]
}

export interface UserSummaryResponse {
  id: number
  username: string
  email: string
  enabled: boolean
  roles: string[]
  createdAt: string
  updatedAt: string
}

export interface UpdateUserRequest {
  email?: string
  username?: string
  enabled?: boolean
}

export interface RoleResponse {
  id: number
  name: string
  description: string | null
  permissions: string[]
}

export interface PermissionResponse {
  id: number
  name: string
  description: string | null
}
