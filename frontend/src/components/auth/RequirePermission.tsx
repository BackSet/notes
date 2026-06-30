import { useEffect } from "react"
import { useNavigate } from "@tanstack/react-router"
import { useAuthStore } from "../../stores/authStore"
import { userHasPermission } from "../../lib/auth/permissions"
import { LoadingState } from "../ui/LoadingState"
import { EmptyState } from "../ui/EmptyState"

interface RequirePermissionProps {
  permission: string
  children: React.ReactNode
}

/**
 * Gates a route by an explicit permission. Unauthenticated users are redirected
 * to login; authenticated users without the permission see a forbidden notice.
 * This is a UX guard only — the backend still authorizes every request.
 */
export function RequirePermission({ permission, children }: RequirePermissionProps) {
  const status = useAuthStore((state) => state.status)
  const user = useAuthStore((state) => state.user)
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
  if (!userHasPermission(user, permission)) {
    return (
      <EmptyState
        title="Acceso denegado"
        description="No tienes permisos para ver esta sección."
      />
    )
  }
  return <>{children}</>
}
