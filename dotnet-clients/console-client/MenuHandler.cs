using System;
using System.Threading.Tasks;
using ConsoleClient.Controllers;
using ConsoleClient.Models;

namespace ConsoleClient
{
    public class MenuHandler
    {
        private readonly ConversionController _controller;

        private readonly string[] _massUnits = { "KILOGRAM", "GRAM", "POUND", "OUNCE" };
        private readonly string[] _lengthUnits = { "METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH" };
        private readonly string[] _tempUnits = { "CELSIUS", "FAHRENHEIT", "KELVIN" };

        public MenuHandler(ConversionController controller)
        {
            _controller = controller;
        }

        public async Task RunMainMenuAsync()
        {
            while (true)
            {
                PrintBanner();
                Console.WriteLine();
                Console.WriteLine("  Use ↑↓ para navegar  |  ENTER para seleccionar");

                int option = ConsoleHelper.ShowSelectionMenu(new[]
                {
                    "Conversion de Masa",
                    "Conversion de Longitud",
                    "Conversion de Temperatura",
                    "Salir"
                });

                switch (option)
                {
                    case 0: await ProcessAsync("Mass"); break;
                    case 1: await ProcessAsync("Length"); break;
                    case 2: await ProcessAsync("Temperature"); break;
                    case 3:
                    case -1:
                        PrintExitMessage();
                        return;
                }
            }
        }

        private async Task ProcessAsync(string category)
        {
            string title = category switch
            {
                "Mass" => "CONVERSION DE MASA",
                "Length" => "CONVERSION DE LONGITUD",
                "Temperature" => "CONVERSION DE TEMPERATURA",
                _ => category.ToUpper()
            };

            string[] units = category switch
            {
                "Mass" => _massUnits,
                "Length" => _lengthUnits,
                "Temperature" => _tempUnits,
                _ => Array.Empty<string>()
            };

            PrintSubHeader(title);
            double value = ReadValidatedDouble("Ingrese valor: ");
            string fromUnit = ReadUnitFromMenu(units, "unidad de ORIGEN");
            string toUnit = ReadUnitFromMenu(units, "unidad de DESTINO");

            Console.WriteLine("\n  Conectando con el servidor VPS...");

            try
            {
                ConversionResult result = category switch
                {
                    "Mass" => await _controller.ConvertMassAsync(value, fromUnit, toUnit),
                    "Length" => await _controller.ConvertLengthAsync(value, fromUnit, toUnit),
                    "Temperature" => await _controller.ConvertTemperatureAsync(value, fromUnit, toUnit),
                    _ => throw new ArgumentException("Categoría inválida")
                };

                PrintResult(result);
            }
            catch (Exception ex)
            {
                PrintError($"\n  [Error]: {ex.Message}");
            }

            Console.WriteLine("\n  Presione ENTER para continuar...");
            Console.ReadLine();
        }

        private string ReadUnitFromMenu(string[] units, string label)
        {
            Console.WriteLine($"\n  Seleccione {label} (↑↓ + ENTER):");
            int sel = ConsoleHelper.ShowSelectionMenu(units);
            if (sel >= 0)
            {
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine($"  Seleccionado: {units[sel]}");
                Console.ResetColor();
                return units[sel];
            }
            Console.Write("  Escriba manualmente: ");
            return Console.ReadLine()?.Trim().ToUpper() ?? "";
        }

        private double ReadValidatedDouble(string prompt)
        {
            while (true)
            {
                Console.Write($"  {prompt}");
                string input = Console.ReadLine()?.Trim() ?? "";
                if (string.IsNullOrEmpty(input)) { PrintWarning("No puede estar vacío."); continue; }
                if (!double.TryParse(input, System.Globalization.NumberStyles.Any,
                    System.Globalization.CultureInfo.InvariantCulture, out double v))
                { PrintWarning("Ingrese un número válido."); continue; }
                if (v < 0) { PrintWarning("Solo valores positivos."); continue; }
                return v;
            }
        }

        private void PrintResult(ConversionResult r)
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("\n  ╔══════════════════════════════════════╗");
            Console.WriteLine($"  ║  Categoría : {r.Category,-24}║");
            Console.WriteLine($"  ║  Origen    : {r.InputValue,10:F4} {r.FromUnit,-13}║");
            Console.WriteLine($"  ║  Resultado : {r.ResultValue,10:F6} {r.ToUnit,-13}║");
            Console.WriteLine("  ╚══════════════════════════════════════╝");
            Console.ResetColor();
        }

        private void PrintBanner()
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("  ╔════════════════════════════════════════╗");
            Console.WriteLine("  ║    SISTEMA DE CONVERSION UNIVERSAL    ║");
            Console.WriteLine("  ║          Cliente SOAP .NET            ║");
            Console.WriteLine("  ╚════════════════════════════════════════╝");
            Console.ResetColor();
        }

        private void PrintSubHeader(string t)
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine($"  ── {t} ──");
            Console.ResetColor();
        }

        private void PrintWarning(string m)
        {
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine($"  ⚠  {m}");
            Console.ResetColor();
        }

        private void PrintError(string m)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine(m);
            Console.ResetColor();
        }

        private void PrintExitMessage()
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("  ╔════════════════════════════════════════╗");
            Console.WriteLine("  ║      ¡Hasta luego! Bye, MONSTER.      ║");
            Console.WriteLine("  ╚════════════════════════════════════════╝");
            Console.ResetColor();
        }
    }
}