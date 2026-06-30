import { useEffect } from "react"
import { useNavigate } from "@tanstack/react-router"
import { useAuthStore } from "../../stores/authStore"
import { LoadingState } from "../ui/LoadingState"

/** Renders children only for an authenticated session; otherwise redirects to login. */
export function RequireAuth({ children }: { children: React.ReactNode }) {
  const status = useAuthStore((state) => state.status)
  const navigate = useNavigate()

  useEffect(() => {
    if (status === "unauthenticated") {
      void navigate({ to: "/login" })
    }
  }, [status, navigate])

  if (status === "loading") {
    return <LoadingState message="Cargando sesión..." />
  }
  if (status === "unauthenticated") {
    return null
  }
  return <>{children}</>
}
