import { Link } from "@tanstack/react-router"
import { PageHeader } from "../components/ui/PageHeader"
import {
  SurfaceCard,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "../components/ui/SurfaceCard"
import { RequireAuth } from "../components/auth/RequireAuth"
import { useAuthStore } from "../stores/authStore"
import { Permission } from "../lib/auth/permissions"

function DashboardContent() {
  const user = useAuthStore((state) => state.user)
  const hasPermission = useAuthStore((state) => state.hasPermission)

  return (
    <div className="space-y-6">
      <PageHeader
        title="Panel"
        description="Resumen de tu cuenta y accesos disponibles."
      />
      <SurfaceCard>
        <CardHeader>
          <CardTitle>Hola, {user?.username}</CardTitle>
          <CardDescription>{user?.email}</CardDescription>
        </CardHeader>
        <CardContent className="space-y-2 text-sm">
          <p>
            <span className="font-medium">Roles:</span>{" "}
            {user && user.roles.length > 0 ? user.roles.join(", ") : "Sin roles"}
          </p>
          <p>
            <span className="font-medium">Permisos:</span>{" "}
            {user && user.permissions.length > 0 ? user.permissions.join(", ") : "Sin permisos"}
          </p>
          {hasPermission(Permission.USER_READ) && (
            <p className="pt-2">
              <Link to="/admin/users" className="focus-ring rounded-sm text-primary hover:underline">
                Ir a administración de usuarios →
              </Link>
            </p>
          )}
        </CardContent>
      </SurfaceCard>
    </div>
  )
}

export function DashboardPage() {
  return (
    <RequireAuth>
      <DashboardContent />
    </RequireAuth>
  )
}
