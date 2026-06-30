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
      <header className="border-b">
        <nav
          aria-label="Navegación principal"
          className="mx-auto flex max-w-4xl items-center justify-between px-4 py-3"
        >
          <Link to="/" className="font-semibold">
            Notes
          </Link>
          <div className="flex items-center gap-3 text-sm">
            {isAuthenticated ? (
              <>
                {hasPermission(Permission.USER_READ) && (
                  <Link to="/admin/users" className="hover:underline">
                    Administración
                  </Link>
                )}
                <span className="text-muted-foreground">{user?.username}</span>
                <Button type="button" size="sm" variant="outline" onClick={handleLogout}>
                  Cerrar sesión
                </Button>
              </>
            ) : (
              <>
                <Link to="/login" className="hover:underline">
                  Iniciar sesión
                </Link>
                <Link to="/register" className="hover:underline">
                  Registrarse
                </Link>
              </>
            )}
          </div>
        </nav>
      </header>
      <main className="mx-auto max-w-4xl px-4 py-8">
        <Outlet />
      </main>
      <Toaster />
    </div>
  )
}
