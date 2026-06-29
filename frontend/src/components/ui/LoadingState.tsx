import * as React from "react"
import { cn } from "../../lib/utils"

interface LoadingStateProps extends React.HTMLAttributes<HTMLDivElement> {
  message?: string
}

export function LoadingState({ className, message = "Cargando...", ...props }: LoadingStateProps) {
  return (
    <div
      className={cn("flex flex-col items-center justify-center p-8 space-y-4", className)}
      {...props}
    >
      <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
      <p className="text-sm font-medium text-muted-foreground animate-pulse">{message}</p>
    </div>
  )
}
