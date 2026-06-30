import * as React from "react"
import { AlertCircle } from "lucide-react"
import { cn } from "../../lib/utils"
import { Button } from "./Button"

interface ErrorStateProps extends React.HTMLAttributes<HTMLDivElement> {
  title?: string
  description?: string
  actionLabel?: string
  onAction?: () => void
}

export function ErrorState({
  className,
  title = "Algo salio mal",
  description = "No se pudo completar la operacion.",
  actionLabel,
  onAction,
  ...props
}: ErrorStateProps) {
  return (
    <div
      role="alert"
      className={cn(
        "surface-muted flex flex-col items-center justify-center gap-3 p-6 text-center sm:p-8",
        className
      )}
      {...props}
    >
      <div className="alert-destructive flex h-10 w-10 items-center justify-center rounded-full border">
        <AlertCircle className="h-5 w-5" aria-hidden="true" />
      </div>
      <div className="max-w-sm space-y-1">
        <h3 className="text-base font-semibold sm:text-lg">{title}</h3>
        <p className="text-sm text-muted-foreground">{description}</p>
      </div>
      {actionLabel && onAction && (
        <Button type="button" variant="outline" size="sm" onClick={onAction}>
          {actionLabel}
        </Button>
      )}
    </div>
  )
}
