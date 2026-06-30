import { render, screen } from "@testing-library/react"
import { describe, expect, test, vi } from "vitest"
import { UsersTable } from "../UsersTable"
import type { UserSummaryResponse } from "../../../lib/api/types"

const users: UserSummaryResponse[] = [
  {
    id: 1,
    username: "admin",
    email: "admin@example.com",
    enabled: true,
    roles: ["ADMIN"],
    createdAt: "2026-01-01T00:00:00Z",
    updatedAt: "2026-01-01T00:00:00Z",
  },
]

describe("UsersTable", () => {
  test("renders an empty state when there are no users", () => {
    render(
      <UsersTable users={[]} canManage onToggleEnabled={vi.fn()} busyUserId={null} />
    )
    expect(screen.getByText(/No hay usuarios/i)).toBeInTheDocument()
  })

  test("renders a row and a management control when allowed", () => {
    const { container } = render(
      <UsersTable users={users} canManage onToggleEnabled={vi.fn()} busyUserId={null} />
    )
    expect(screen.getByText("admin")).toBeInTheDocument()
    expect(screen.getByRole("button", { name: /Deshabilitar/i })).toBeInTheDocument()
    expect(container.querySelector(".overflow-x-auto")).toBeInTheDocument()
  })

  test("hides management controls when not allowed", () => {
    render(
      <UsersTable users={users} canManage={false} onToggleEnabled={vi.fn()} busyUserId={null} />
    )
    expect(screen.getByText("admin")).toBeInTheDocument()
    expect(screen.queryByRole("button", { name: /Deshabilitar/i })).not.toBeInTheDocument()
  })
})
