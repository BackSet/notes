import * as React from "react"
import { cn } from "../../lib/utils"
import { Skeleton } from "./Skeleton"

interface LoadingStateProps extends React.HTMLAttributes<HTMLDivElement> {
  message?: string
  variant?: "spinner" | "page" | "table"
}

export function LoadingState({
  className,
  message = "Cargando...",
  variant = "spinner",
  ...props
}: LoadingStateProps) {
  if (variant === "page") {
    return (
      <div className={cn("space-y-4", className)} aria-busy="true" {...props}>
        <Skeleton className="h-8 w-2/3 max-w-sm" />
        <Skeleton className="h-4 w-full max-w-xl" />
        <div className="surface space-y-4 p-5 sm:p-6">
          <Skeleton className="h-5 w-1/3" />
          <Skeleton className="h-20 w-full" />
          <Skeleton className="h-10 w-28" />
        </div>
        <span className="sr-only">{message}</span>
      </div>
    )
  }

  if (variant === "table") {
    return (
      <div className={cn("surface space-y-3 p-4 sm:p-6", className)} aria-busy="true" {...props}>
        <Skeleton className="h-6 w-40" />
        {Array.from({ length: 5 }).map((_, index) => (
          <div key={index} className="grid grid-cols-2 gap-3 sm:grid-cols-5">
            <Skeleton className="h-9" />
            <Skeleton className="h-9" />
            <Skeleton className="hidden h-9 sm:block" />
            <Skeleton className="hidden h-9 sm:block" />
            <Skeleton className="hidden h-9 sm:block" />
          </div>
        ))}
        <span className="sr-only">{message}</span>
      </div>
    )
  }

  return (
    <div
      className={cn("flex flex-col items-center justify-center space-y-4 p-8", className)}
      aria-busy="true"
      {...props}
    >
      <div className="h-8 w-8 rounded-full border-4 border-primary border-t-transparent motion-safe:animate-spin"></div>
      <p className="text-sm font-medium text-muted-foreground motion-safe:animate-pulse">{message}</p>
    </div>
  )
}
