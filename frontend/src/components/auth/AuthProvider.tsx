import { useEffect } from "react"
import { useAuthStore } from "../../stores/authStore"
import { subscribeToSession } from "../../lib/auth/session"

/** Hydrates the session on mount and keeps the store in sync with token changes. */
export function AuthProvider({ children }: { children: React.ReactNode }) {
  const hydrate = useAuthStore((state) => state.hydrate)
  const syncFromSession = useAuthStore((state) => state.syncFromSession)

  useEffect(() => {
    void hydrate()
  }, [hydrate])

  useEffect(() => subscribeToSession(syncFromSession), [syncFromSession])

  return <>{children}</>
}
