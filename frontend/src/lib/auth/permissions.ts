import type { MeResponse } from "../api/types"

/** Permission names used to gate UI controls. Backend remains the source of truth. */
export const Permission = {
  USER_READ: "USER_READ",
  USER_CREATE: "USER_CREATE",
  USER_UPDATE: "USER_UPDATE",
  USER_DISABLE: "USER_DISABLE",
  ROLE_READ: "ROLE_READ",
  PERMISSION_READ: "PERMISSION_READ",
} as const

export type PermissionName = (typeof Permission)[keyof typeof Permission]

/** Pure check used by guards and components; tolerates a null user. */
export function userHasPermission(user: MeResponse | null, permission: string): boolean {
  return user?.permissions.includes(permission) ?? false
}
