import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useState, type FormEvent } from "react";
import { useNavigate } from "@tanstack/react-router";
import { login, isAuthed } from "@/lib/auth";

export const Route = createFileRoute("/")({
  component: Index,
});

function Index() {
  const navigate = useNavigate();
  const [user, setUser] = useState("");
  const [pass, setPass] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (isAuthed()) navigate({ to: "/calculator" });
  }, [navigate]);

  const onSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (login(user, pass)) {
      navigate({ to: "/calculator" });
    } else {
      setError("Usuario o contraseña incorrectos");
    }
  };

  return (
    <div className="min-h-screen grid lg:grid-cols-2 bg-background">
      <div className="flex items-center justify-center px-6 py-12">
        <form onSubmit={onSubmit} className="w-full max-w-sm space-y-6">
          <div>
            <h1 className="text-4xl font-bold tracking-tight text-foreground">
              Bienvenido
            </h1>
            <p className="mt-2 text-sm text-muted-foreground">
              Ingresa tus credenciales para acceder a la calculadora.
            </p>
          </div>

          <div className="space-y-4">
            <div className="space-y-2">
              <label className="text-sm font-medium tracking-tight text-foreground">Usuario</label>
              <input
                type="text"
                value={user}
                onChange={(e) => setUser(e.target.value)}
                placeholder="MONSTER"
                className="w-full rounded-lg border border-border bg-card px-4 py-2.5 text-sm outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium tracking-tight text-foreground">Contraseña</label>
              <input
                type="password"
                value={pass}
                onChange={(e) => setPass(e.target.value)}
                placeholder="••••••••"
                className="w-full rounded-lg border border-border bg-card px-4 py-2.5 text-sm outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
              />
            </div>
          </div>

          {error && (
            <p className="text-sm text-destructive" role="alert">
              {error}
            </p>
          )}

          <button
            type="submit"
            className="w-full rounded-lg bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground transition hover:opacity-90"
            style={{ boxShadow: "var(--shadow-soft)" }}
          >
            Iniciar sesión
          </button>

          
        </form>
      </div>

      <div
        className="hidden lg:flex items-center justify-center relative overflow-hidden"
        style={{ background: "var(--gradient-hero)" }}
      >
        <div className="absolute w-72 h-72 rounded-full bg-white/20 blur-3xl" />
        <div className="relative w-56 h-56 rounded-full bg-white/30 backdrop-blur-xl border border-white/40 flex items-center justify-center text-white">
          <span className="text-6xl font-bold">∑</span>
        </div>
      </div>
    </div>
  );
}
