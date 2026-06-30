import { Button } from "../ui/Button"
import { EmptyState } from "../ui/EmptyState"
import { cn } from "../../lib/utils"
import type { UserSummaryResponse } from "../../lib/api/types"

interface UsersTableProps {
  users: UserSummaryResponse[]
  /** When false, no enable/disable controls are rendered. */
  canManage: boolean
  busyUserId: number | null
  onToggleEnabled: (user: UserSummaryResponse) => void
}

export function UsersTable({ users, canManage, busyUserId, onToggleEnabled }: UsersTableProps) {
  if (users.length === 0) {
    return (
      <EmptyState
        title="No hay usuarios"
        description="Aún no se han registrado usuarios en el sistema."
      />
    )
  }

  return (
    <div className="w-full overflow-x-auto rounded-md border">
    <table className="w-full min-w-[720px] border-collapse text-sm">
      <caption className="sr-only">Listado de usuarios</caption>
      <thead>
        <tr className="border-b bg-muted/40 text-left text-muted-foreground">
          <th scope="col" className="px-3 py-2 font-medium">Usuario</th>
          <th scope="col" className="px-3 py-2 font-medium">Email</th>
          <th scope="col" className="px-3 py-2 font-medium">Roles</th>
          <th scope="col" className="px-3 py-2 font-medium">Estado</th>
          {canManage && (
            <th scope="col" className="px-3 py-2 font-medium">Acciones</th>
          )}
        </tr>
      </thead>
      <tbody>
        {users.map((user) => (
          <tr key={user.id} className="border-b last:border-0">
            <td className="px-3 py-2 font-medium">{user.username}</td>
            <td className="px-3 py-2 text-muted-foreground">{user.email}</td>
            <td className="px-3 py-2 text-muted-foreground">
              {user.roles.length > 0 ? user.roles.join(", ") : "—"}
            </td>
            <td className="px-3 py-2">
              <span
                className={cn(
                  "inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium",
                  user.enabled
                    ? "bg-success/15 text-success"
                    : "bg-muted text-muted-foreground"
                )}
              >
                {user.enabled ? "Habilitado" : "Deshabilitado"}
              </span>
            </td>
            {canManage && (
              <td className="px-3 py-2">
                <Button
                  type="button"
                  size="sm"
                  variant={user.enabled ? "destructive" : "secondary"}
                  disabled={busyUserId === user.id}
                  onClick={() => onToggleEnabled(user)}
                >
                  {user.enabled ? "Deshabilitar" : "Habilitar"}
                </Button>
              </td>
            )}
          </tr>
        ))}
      </tbody>
    </table>
    </div>
  )
}
