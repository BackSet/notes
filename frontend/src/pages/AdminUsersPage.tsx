import { useCallback, useEffect, useState } from "react"
import { toast } from "sonner"
import {
  SurfaceCard,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "../components/ui/SurfaceCard"
import { PageHeader } from "../components/ui/PageHeader"
import { LoadingState } from "../components/ui/LoadingState"
import { ErrorState } from "../components/ui/ErrorState"
import { ConfirmDialog } from "../components/ui/ConfirmDialog"
import { UsersTable } from "../components/admin/UsersTable"
import { RequirePermission } from "../components/auth/RequirePermission"
import { client } from "../lib/api/client"
import { Permission } from "../lib/auth/permissions"
import { useAuthStore } from "../stores/authStore"
import type { UserSummaryResponse } from "../lib/api/types"

function messageFrom(error: unknown, fallback: string): string {
  if (error && typeof error === "object" && "detail" in error) {
    const detail = (error as { detail?: unknown }).detail
    if (typeof detail === "string" && detail.length > 0) {
      return detail
    }
  }
  return fallback
}

function AdminUsersContent() {
  const hasPermission = useAuthStore((state) => state.hasPermission)
  const canManage =
    hasPermission(Permission.USER_DISABLE) && hasPermission(Permission.USER_UPDATE)

  const [users, setUsers] = useState<UserSummaryResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [busyUserId, setBusyUserId] = useState<number | null>(null)
  const [pendingToggleUser, setPendingToggleUser] = useState<UserSummaryResponse | null>(null)

  const loadUsers = useCallback(async () => {
    setLoading(true)
    setError(null)
    const { data, error: apiError } = await client.GET("/api/admin/users")
    if (apiError || !data) {
      setError(messageFrom(apiError, "No se pudieron cargar los usuarios"))
      setUsers([])
    } else {
      setUsers(data)
    }
    setLoading(false)
  }, [])

  useEffect(() => {
    void loadUsers()
  }, [loadUsers])

  const handleToggleEnabled = useCallback((user: UserSummaryResponse) => {
    setPendingToggleUser(user)
  }, [])

  const confirmToggleEnabled = useCallback(async () => {
    if (!pendingToggleUser) {
      return
    }

    const user = pendingToggleUser
    setBusyUserId(user.id)

    let updated: UserSummaryResponse | undefined
    let apiError: unknown
    if (user.enabled) {
      const result = await client.POST("/api/admin/users/{id}/disable", {
        params: { path: { id: user.id } },
      })
      updated = result.data
      apiError = result.error
    } else {
      const result = await client.POST("/api/admin/users/{id}/enable", {
        params: { path: { id: user.id } },
      })
      updated = result.data
      apiError = result.error
    }

    if (!updated) {
      toast.error(messageFrom(apiError, "No se pudo actualizar el usuario"))
    } else {
      const next = updated
      setUsers((current) => current.map((item) => (item.id === next.id ? next : item)))
      toast.success(next.enabled ? "Usuario habilitado" : "Usuario deshabilitado")
    }
    setBusyUserId(null)
    setPendingToggleUser(null)
  }, [pendingToggleUser])

  if (loading) {
    return <LoadingState variant="table" message="Cargando usuarios..." />
  }
  if (error) {
    return (
      <ErrorState
        title="No se pudieron cargar los usuarios"
        description={error}
        actionLabel="Reintentar"
        onAction={() => void loadUsers()}
      />
    )
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Usuarios"
        description="Gestiona las cuentas registradas y su estado de acceso."
      />
      <SurfaceCard>
        <CardHeader>
          <CardTitle>Listado de usuarios</CardTitle>
          <CardDescription>Los cambios de estado se aplican inmediatamente.</CardDescription>
        </CardHeader>
        <CardContent>
          <UsersTable
            users={users}
            canManage={canManage}
            busyUserId={busyUserId}
            onToggleEnabled={handleToggleEnabled}
          />
        </CardContent>
      </SurfaceCard>
      <ConfirmDialog
        open={pendingToggleUser !== null}
        title={pendingToggleUser?.enabled ? "Deshabilitar usuario" : "Habilitar usuario"}
        description={
          pendingToggleUser
            ? `Vas a ${pendingToggleUser.enabled ? "deshabilitar" : "habilitar"} a ${pendingToggleUser.username}.`
            : ""
        }
        confirmLabel={pendingToggleUser?.enabled ? "Deshabilitar" : "Habilitar"}
        destructive={pendingToggleUser?.enabled}
        busy={busyUserId !== null}
        onCancel={() => setPendingToggleUser(null)}
        onConfirm={() => void confirmToggleEnabled()}
      />
    </div>
  )
}

export function AdminUsersPage() {
  return (
    <RequirePermission permission={Permission.USER_READ}>
      <AdminUsersContent />
    </RequirePermission>
  )
}
