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

const registerSchema = z.object({
  email: z.string().min(1, "Ingresa tu email").email("Email no válido"),
  username: z
    .string()
    .min(3, "El usuario debe tener al menos 3 caracteres")
    .max(100, "El usuario es demasiado largo"),
  password: z.string().min(8, "La contraseña debe tener al menos 8 caracteres"),
})

type RegisterForm = z.infer<typeof registerSchema>

export function RegisterPage() {
  const registerUser = useAuthStore((state) => state.register)
  const status = useAuthStore((state) => state.status)
  const navigate = useNavigate()

  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<RegisterForm>({ resolver: zodResolver(registerSchema) })

  useEffect(() => {
    if (status === "authenticated") {
      void navigate({ to: "/" })
    }
  }, [status, navigate])

  async function onSubmit(values: RegisterForm) {
    try {
      await registerUser(values.email, values.username, values.password)
      toast.success("Cuenta creada")
      void navigate({ to: "/" })
    } catch (error) {
      const message = error instanceof Error ? error.message : "No se pudo completar el registro"
      setError("root", { message })
    }
  }

  return (
    <div className="mx-auto w-full max-w-md">
      <SurfaceCard>
        <CardHeader>
          <CardTitle>Crear cuenta</CardTitle>
          <CardDescription>Regístrate para empezar a usar Notes.</CardDescription>
        </CardHeader>
        <CardContent>
          <form className="space-y-4" onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="space-y-1.5">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                autoComplete="email"
                aria-invalid={errors.email ? "true" : undefined}
                {...register("email")}
              />
              {errors.email && (
                <p role="alert" className="text-sm text-destructive">
                  {errors.email.message}
                </p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="username">Usuario</Label>
              <Input
                id="username"
                autoComplete="username"
                aria-invalid={errors.username ? "true" : undefined}
                {...register("username")}
              />
              {errors.username && (
                <p role="alert" className="text-sm text-destructive">
                  {errors.username.message}
                </p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="password">Contraseña</Label>
              <Input
                id="password"
                type="password"
                autoComplete="new-password"
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
              {isSubmitting ? "Creando..." : "Crear cuenta"}
            </Button>
          </form>
          <p className="mt-4 text-center text-sm text-muted-foreground">
            ¿Ya tienes cuenta?{" "}
            <Link to="/login" className="focus-ring rounded-sm text-primary hover:underline">
              Inicia sesión
            </Link>
          </p>
        </CardContent>
      </SurfaceCard>
    </div>
  )
}
