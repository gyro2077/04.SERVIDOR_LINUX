using System;
using ConsoleClient.Models;

namespace ConsoleClient.Views
{
    public class ConversionView
    {
        public (double value, string fromUnit, string toUnit) RenderConversionPrompt(string title, string[] availableUnits)
        {
            ConsoleUIHelper.PrintHeader(title);

            double value = ReadDouble("  Ingrese el valor a convertir: ");

            Console.WriteLine("\n  Seleccione la unidad de ORIGEN:");
            string fromUnit = SelectUnit(availableUnits);

            Console.WriteLine("\n  Seleccione la unidad de DESTINO:");
            string toUnit = SelectUnit(availableUnits);

            return (value, fromUnit, toUnit);
        }

        public void RenderResult(ConversionResponseView response)
        {
            Console.WriteLine("\n╔════════════════════════════════════════════════════════════╗");
            Console.WriteLine("║                 RESULTADO DE LA CONVERSIÓN                 ║");
            Console.WriteLine("╠════════════════════════════════════════════════════════════╣");
            Console.WriteLine($"║  Categoría: {response.Category.PadRight(46)} ║");
            Console.WriteLine($"║  Origen:    {response.InputValue} {response.FromUnit}".PadRight(59) + "║");
            Console.WriteLine($"║  Destino:   {response.ResultValue:F6} {response.ToUnit}".PadRight(59) + "║");
            Console.WriteLine("╚════════════════════════════════════════════════════════════╝");
            Console.WriteLine("\n  Presione ENTER para continuar...");
            Console.ReadLine();
        }

        public void RenderError(string error)
        {
            Console.WriteLine();
            ConsoleUIHelper.PrintError(error);
            Console.WriteLine("\n  Presione ENTER para continuar...");
            Console.ReadLine();
        }

        public void RenderLoading()
        {
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine("\n  ⏳ Conectando con el servidor SOAP y procesando...");
            Console.ResetColor();
        }

        private double ReadDouble(string prompt)
        {
            while (true)
            {
                Console.Write(prompt);
                if (double.TryParse(Console.ReadLine()?.Trim(), out double val) && val >= 0)
                {
                    return val;
                }
                ConsoleUIHelper.PrintError("Ingrese un número positivo válido.");
            }
        }

        private string SelectUnit(string[] units)
        {
            int idx = ConsoleUIHelper.ShowInteractiveMenu(units);
            if (idx >= 0)
            {
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine($"  ✓ Seleccionado: {units[idx]}");
                Console.ResetColor();
                return units[idx];
            }
            Console.Write("  Escriba la unidad manualmente: ");
            return Console.ReadLine()?.Trim().ToUpper() ?? "";
        }
    }
}