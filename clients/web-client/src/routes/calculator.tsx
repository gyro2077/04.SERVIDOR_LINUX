import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useEffect } from "react";
import { isAuthed, logout } from "@/lib/auth";
import { useConversionController } from "@/controllers/useConversionController";

export const Route = createFileRoute("/calculator")({
  component: Calculator,
});

const categoryLabels = {
  length: "Longitud",
  mass: "Masa",
  temperature: "Temperatura",
} as const;

const unitLabels: Record<string, string> = {
  METER: "Metro",
  KILOMETER: "Kilómetro",
  CENTIMETER: "Centímetro",
  MILE: "Milla",
  YARD: "Yarda",
  FOOT: "Pie",
  INCH: "Pulgada",
  KILOGRAM: "Kilogramo",
  GRAM: "Gramo",
  POUND: "Libra",
  OUNCE: "Onza",
  CELSIUS: "Celsius",
  FAHRENHEIT: "Fahrenheit",
  KELVIN: "Kelvin",
};

function Calculator() {
  const navigate = useNavigate();
  const {
    category,
    setCategory,
    value,
    setValue,
    from,
    setFrom,
    to,
    setTo,
    result,
    loading,
    error,
    handleConversion,
    availableUnits,
  } = useConversionController();

  useEffect(() => {
    if (!isAuthed()) navigate({ to: "/" });
  }, [navigate]);

  const handleLogout = () => {
    logout();
    navigate({ to: "/" });
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center px-4 py-10"
      style={{ background: "var(--gradient-hero)" }}
    >
      <div
        className="w-full max-w-xl rounded-3xl bg-card p-8 md:p-10 space-y-8"
        style={{ boxShadow: "var(--shadow-elegant)" }}
      >
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-3xl font-bold text-foreground">Calculadora</h1>
            <p className="text-sm text-muted-foreground mt-1">
              Conversión de unidades
            </p>
          </div>
          <button
            onClick={handleLogout}
            className="text-xs font-medium text-muted-foreground hover:text-primary transition"
          >
            Salir
          </button>
        </div>

        <div className="grid grid-cols-3 gap-2 p-1 rounded-xl bg-secondary">
          {(["length", "mass", "temperature"] as const).map((c) => (
            <button
              key={c}
              onClick={() => setCategory(c)}
              className={`rounded-lg py-2 text-sm font-medium transition ${
                category === c
                  ? "bg-primary text-primary-foreground shadow"
                  : "text-secondary-foreground hover:bg-accent"
              }`}
            >
              {categoryLabels[c]}
            </button>
          ))}
        </div>

        <div className="space-y-4">
          <div className="space-y-2">
            <label className="text-sm font-medium text-foreground">Valor</label>
            <input
              type="number"
              value={value}
              onChange={(e) => setValue(e.target.value)}
              className="w-full rounded-lg border border-border bg-background px-4 py-3 text-lg outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground">De</label>
              <select
                value={from}
                onChange={(e) => setFrom(e.target.value)}
                className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
              >
                {availableUnits.map((u) => (
                  <option key={u} value={u}>
                    {unitLabels[u] || u}
                  </option>
                ))}
              </select>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground">A</label>
              <select
                value={to}
                onChange={(e) => setTo(e.target.value)}
                className="w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
              >
                {availableUnits.map((u) => (
                  <option key={u} value={u}>
                    {unitLabels[u] || u}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        <button
          onClick={handleConversion}
          disabled={loading}
          className="w-full rounded-lg bg-primary px-4 py-3 text-sm font-semibold text-primary-foreground transition hover:opacity-90 disabled:opacity-50"
          style={{ boxShadow: "var(--shadow-soft)" }}
        >
          {loading ? "Convirtiendo..." : "Convertir"}
        </button>

        {error && (
          <div className="rounded-lg bg-destructive/10 border border-destructive/20 p-4">
            <p className="text-sm text-destructive">{error}</p>
          </div>
        )}

        {result && (
          <div
            className="rounded-2xl p-6 text-center"
            style={{ background: "var(--gradient-hero)" }}
          >
            <p className="text-xs uppercase tracking-wider text-primary-foreground/80">
              Resultado
            </p>
            <p className="mt-2 text-4xl font-bold text-primary-foreground break-all">
              {result.resultValue.toFixed(4)}
            </p>
            <p className="mt-1 text-sm text-primary-foreground/80">
              {unitLabels[result.toUnit] || result.toUnit}
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
