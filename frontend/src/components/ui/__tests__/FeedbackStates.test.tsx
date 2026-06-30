import { fireEvent, render, screen } from "@testing-library/react"
import { describe, expect, test, vi } from "vitest"
import { ConfirmDialog } from "../ConfirmDialog"
import { ErrorState } from "../ErrorState"
import { LoadingState } from "../LoadingState"
import { PageHeader } from "../PageHeader"
import { Skeleton } from "../Skeleton"

describe("feedback and layout states", () => {
  test("renders a semantic skeleton placeholder", () => {
    const { container } = render(<Skeleton data-testid="skeleton" className="h-4 w-10" />)

    expect(screen.getByTestId("skeleton")).toHaveAttribute("aria-hidden", "true")
    expect(container.firstChild).toHaveClass("motion-safe:animate-pulse")
  })

  test("renders table loading skeletons with busy state", () => {
    render(<LoadingState variant="table" message="Cargando usuarios" />)

    expect(screen.getByText("Cargando usuarios")).toHaveClass("sr-only")
    expect(screen.getByText("Cargando usuarios").closest("[aria-busy='true']")).toBeInTheDocument()
  })

  test("renders error action", () => {
    const retry = vi.fn()
    render(
      <ErrorState
        title="No se pudo cargar"
        description="Intenta de nuevo"
        actionLabel="Reintentar"
        onAction={retry}
      />
    )

    fireEvent.click(screen.getByRole("button", { name: "Reintentar" }))
    expect(retry).toHaveBeenCalledTimes(1)
  })

  test("renders page header actions", () => {
    render(<PageHeader title="Usuarios" actions={<button type="button">Nuevo</button>} />)

    expect(screen.getByRole("heading", { name: "Usuarios" })).toBeInTheDocument()
    expect(screen.getByRole("button", { name: "Nuevo" })).toBeInTheDocument()
  })

  test("confirms and cancels dialog actions", () => {
    const onConfirm = vi.fn()
    const onCancel = vi.fn()
    render(
      <ConfirmDialog
        open
        title="Deshabilitar usuario"
        description="Confirma el cambio"
        confirmLabel="Deshabilitar"
        onConfirm={onConfirm}
        onCancel={onCancel}
      />
    )

    expect(screen.getByRole("alertdialog")).toBeInTheDocument()
    fireEvent.click(screen.getByRole("button", { name: "Cancelar" }))
    fireEvent.click(screen.getByRole("button", { name: "Deshabilitar" }))
    expect(onCancel).toHaveBeenCalledTimes(1)
    expect(onConfirm).toHaveBeenCalledTimes(1)
  })
})
