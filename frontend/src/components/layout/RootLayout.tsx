import { Link, Outlet, useNavigate } from "@tanstack/react-router"
import { toast } from "sonner"
import { Button } from "../ui/Button"
import { Toaster } from "../ui/Toaster"
import { useAuthStore } from "../../stores/authStore"
import { Permission } from "../../lib/auth/permissions"

export function RootLayout() {
  const status = useAuthStore((state) => state.status)
  const user = useAuthStore((state) => state.user)
  const logout = useAuthStore((state) => state.logout)
  const hasPermission = useAuthStore((state) => state.hasPermission)
  const navigate = useNavigate()

  const isAuthenticated = status === "authenticated"

  async function handleLogout() {
    await logout()
    toast.success("Sesión cerrada")
    void navigate({ to: "/login" })
  }

  return (
    <div className="min-h-screen bg-background text-foreground">
      <header className="border-b bg-background/95">
        <nav
          aria-label="Navegación principal"
          className="mx-auto flex max-w-6xl flex-col gap-3 px-4 py-3 sm:flex-row sm:items-center sm:justify-between"
        >
          <Link to="/" className="focus-ring rounded-sm font-semibold">
            Notes
          </Link>
          <div className="flex w-full flex-wrap items-center gap-3 text-sm sm:w-auto sm:justify-end">
            {isAuthenticated ? (
              <>
                {hasPermission(Permission.USER_READ) && (
                  <Link to="/admin/users" className="focus-ring rounded-sm hover:underline">
                    Administración
                  </Link>
                )}
                <span className="min-w-0 max-w-full truncate text-muted-foreground sm:max-w-48">{user?.username}</span>
                <Button type="button" size="sm" variant="outline" onClick={handleLogout}>
                  Cerrar sesión
                </Button>
              </>
            ) : (
              <>
                <Link to="/login" className="focus-ring rounded-sm hover:underline">
                  Iniciar sesión
                </Link>
                <Link to="/register" className="focus-ring rounded-sm hover:underline">
                  Registrarse
                </Link>
              </>
            )}
          </div>
        </nav>
      </header>
      <main className="mx-auto w-full max-w-6xl px-4 py-6 sm:py-8">
        <Outlet />
      </main>
      <Toaster />
    </div>
  )
}
