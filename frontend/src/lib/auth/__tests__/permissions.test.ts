import { describe, expect, test } from "vitest"
import { userHasPermission } from "../permissions"
import type { MeResponse } from "../../api/types"

const adminUser: MeResponse = {
  id: 1,
  username: "admin",
  email: "admin@example.com",
  enabled: true,
  roles: ["ADMIN"],
  permissions: ["USER_READ", "USER_UPDATE"],
}

const publicUser: MeResponse = {
  id: 2,
  username: "pub",
  email: "pub@example.com",
  enabled: true,
  roles: [],
  permissions: [],
}

describe("userHasPermission", () => {
  test("returns true when the user has the permission", () => {
    expect(userHasPermission(adminUser, "USER_READ")).toBe(true)
  })

  test("returns false when the user lacks the permission", () => {
    expect(userHasPermission(publicUser, "USER_READ")).toBe(false)
  })

  test("returns false for a null user", () => {
    expect(userHasPermission(null, "USER_READ")).toBe(false)
  })
})
