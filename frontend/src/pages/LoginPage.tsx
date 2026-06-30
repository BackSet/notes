import { useEffect } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { Link, useNavigate } from "@tanstack/react-router"
import { toast } from "sonner"
import {
  SurfaceCard,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "../components/ui/SurfaceCard"
import { Button } from "../components/ui/Button"
import { Input } from "../components/ui/Input"
import { Label } from "../components/ui/Label"
import { useAuthStore } from "../stores/authStore"

const loginSchema = z.object({
  identifier: z.string().min(1, "Ingresa tu email o nombre de usuario"),
  password: z.string().min(1, "Ingresa tu contraseña"),
})

type LoginForm = z.infer<typeof loginSchema>

export function LoginPage() {
  const login = useAuthStore((state) => state.login)
  const status = useAuthStore((state) => state.status)
  const navigate = useNavigate()

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<LoginForm>({ resolver: zodResolver(loginSchema) })

  useEffect(() => {
    if (status === "authenticated") {
      void navigate({ to: "/" })
    }
  }, [status, navigate])

  async function onSubmit(values: LoginForm) {
    try {
      await login(values.identifier, values.password)
      toast.success("Sesión iniciada")
      void navigate({ to: "/" })
    } catch (error) {
      const message = error instanceof Error ? error.message : "No se pudo iniciar sesión"
      setError("root", { message })
    }
  }

  return (
    <div className="mx-auto max-w-sm">
      <SurfaceCard>
        <CardHeader>
          <CardTitle>Iniciar sesión</CardTitle>
          <CardDescription>Accede con tu email o nombre de usuario.</CardDescription>
        </CardHeader>
        <CardContent>
          <form className="space-y-4" onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="space-y-1.5">
              <Label htmlFor="identifier">Email o usuario</Label>
              <Input
                id="identifier"
                autoComplete="username"
                aria-invalid={errors.identifier ? "true" : undefined}
                {...register("identifier")}
              />
              {errors.identifier && (
                <p role="alert" className="text-sm text-destructive">
                  {errors.identifier.message}
                </p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="password">Contraseña</Label>
              <Input
                id="password"
                type="password"
                autoComplete="current-password"
                aria-invalid={errors.password ? "true" : undefined}
                {...register("password")}
              />
              {errors.password && (
                <p role="alert" className="text-sm text-destructive">
                  {errors.password.message}
                </p>
              )}
            </div>
            {errors.root && (
              <p role="alert" className="text-sm text-destructive">
                {errors.root.message}
              </p>
            )}
            <Button type="submit" className="w-full" disabled={isSubmitting}>
              {isSubmitting ? "Entrando..." : "Entrar"}
            </Button>
          </form>
          <p className="mt-4 text-center text-sm text-muted-foreground">
            ¿No tienes cuenta?{" "}
            <Link to="/register" className="text-primary hover:underline">
              Regístrate
            </Link>
          </p>
        </CardContent>
      </SurfaceCard>
    </div>
  )
}
