import { beforeEach, describe, expect, test, vi } from "vitest"
import {
  clearSession,
  getAccessToken,
  getRefreshToken,
  hasSession,
  setTokens,
  subscribeToSession,
} from "../session"

describe("session", () => {
  beforeEach(() => {
    localStorage.clear()
  })

  test("stores and reads tokens", () => {
    expect(hasSession()).toBe(false)
    setTokens("access-1", "refresh-1")
    expect(getAccessToken()).toBe("access-1")
    expect(getRefreshToken()).toBe("refresh-1")
    expect(hasSession()).toBe(true)
  })

  test("clearSession removes both tokens", () => {
    setTokens("access-1", "refresh-1")
    clearSession()
    expect(getAccessToken()).toBeNull()
    expect(getRefreshToken()).toBeNull()
    expect(hasSession()).toBe(false)
  })

  test("notifies subscribers on set and clear, and stops after unsubscribe", () => {
    const listener = vi.fn()
    const unsubscribe = subscribeToSession(listener)

    setTokens("a", "b")
    clearSession()
    expect(listener).toHaveBeenCalledTimes(2)

    unsubscribe()
    setTokens("c", "d")
    expect(listener).toHaveBeenCalledTimes(2)
  })
})
