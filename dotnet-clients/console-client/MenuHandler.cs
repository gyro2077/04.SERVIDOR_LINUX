using System;
using System.Threading.Tasks;

namespace ConsoleClient
{
    public class MenuHandler
    {
        private readonly ServiceInvoker _invoker = new ServiceInvoker();
        private readonly string[] _massUnits = { "KILOGRAM", "GRAM", "POUND", "OUNCE" };
        private readonly string[] _lengthUnits = { "METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH" };
        private readonly string[] _tempUnits = { "CELSIUS", "FAHRENHEIT", "KELVIN" };

        public async Task RunMainMenuAsync()
        {
            while (true)
            {
                PrintBanner();
                Console.WriteLine();
                Console.WriteLine("  Presione ENTER para seleccionar");
                Console.WriteLine("  Use ↑↓ para navegar");

                int option = ConsoleHelper.ShowSelectionMenu(new[]
                {
                    "Conversion de Masa",
                    "Conversion de Longitud",
                    "Conversion de Temperatura",
                    "Salir"
                });

                switch (option)
                {
                    case 0:
                        await ProcessMassConversionAsync();
                        break;
                    case 1:
                        await ProcessLengthConversionAsync();
                        break;
                    case 2:
                        await ProcessTemperatureConversionAsync();
                        break;
                    case 3:
                    case -1:
                        PrintExitMessage();
                        return;
                }
            }
        }

        private async Task ProcessMassConversionAsync()
        {
            PrintSubHeader("CONVERSION DE MASA");

            double value = ReadValidatedDouble("Ingrese valor: ");
            string fromUnit = ReadUnitFromMenu(_massUnits, "unidad de ORIGEN");
            string toUnit = ReadUnitFromMenu(_massUnits, "unidad de DESTINO");

            await ExecuteConversionAsync("Mass", value, fromUnit, toUnit);
        }

        private async Task ProcessLengthConversionAsync()
        {
            PrintSubHeader("CONVERSION DE LONGITUD");

            double value = ReadValidatedDouble("Ingrese valor: ");
            string fromUnit = ReadUnitFromMenu(_lengthUnits, "unidad de ORIGEN");
            string toUnit = ReadUnitFromMenu(_lengthUnits, "unidad de DESTINO");

            await ExecuteConversionAsync("Length", value, fromUnit, toUnit);
        }

        private async Task ProcessTemperatureConversionAsync()
        {
            PrintSubHeader("CONVERSION DE TEMPERATURA");

            double value = ReadValidatedDouble("Ingrese valor: ");
            string fromUnit = ReadUnitFromMenu(_tempUnits, "unidad de ORIGEN");
            string toUnit = ReadUnitFromMenu(_tempUnits, "unidad de DESTINO");

            await ExecuteConversionAsync("Temperature", value, fromUnit, toUnit);
        }

        private string ReadUnitFromMenu(string[] units, string label)
        {
            Console.WriteLine($"\nSeleccione {label} (flechas ↑↓ + ENTER):");
            int selection = ConsoleHelper.ShowSelectionMenu(units);

            if (selection >= 0)
            {
                Console.ForegroundColor = ConsoleColor.Green;
                Console.WriteLine($"  Seleccionado: {units[selection]}");
                Console.ResetColor();
                return units[selection];
            }

            Console.Write("  Escriba manualmente: ");
            string input = Console.ReadLine()?.Trim().ToUpper() ?? "";
            return input;
        }

        private double ReadValidatedDouble(string prompt)
        {
            while (true)
            {
                Console.Write(prompt);
                string input = Console.ReadLine()?.Trim() ?? "";

                if (string.IsNullOrEmpty(input))
                {
                    PrintWarning("No puede estar vacio. Intente de nuevo.");
                    continue;
                }

                if (!double.TryParse(input, out double value))
                {
                    PrintWarning("Ingrese un numero valido.");
                    continue;
                }

                if (value < 0)
                {
                    PrintWarning("Solo valores positivos.");
                    continue;
                }

                return value;
            }
        }

        private async Task ExecuteConversionAsync(string category, double value, string fromUnit, string toUnit)
        {
            Console.WriteLine("\nConectando con el servidor SOAP...");

            try
            {
                var response = await _invoker.InvokeConversionAsync(category, value, fromUnit, toUnit);

                PrintSuccess($"\n========== RESULTADO ==========");
                PrintSuccess($"  Categoria: {response.Category}");
                PrintSuccess($"  {response.InputValue} {response.FromUnit}");
                PrintSuccess($"         = {response.ResultValue:F6} {response.ToUnit}");
                PrintSuccess($"==============================");
            }
            catch (Exception ex)
            {
                PrintError($"\n[Error]: {ex.Message}");
            }

            Console.WriteLine("\nPresione ENTER para continuar...");
            Console.ReadLine();
        }

        private void PrintBanner()
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("╔════════════════════════════════════════╗");
            Console.WriteLine("║   SISTEMA DE CONVERSION UNIVERSAL    ║");
            Console.WriteLine("║         Cliente SOAP .NET            ║");
            Console.WriteLine("╚════════════════════════════════════════╝");
            Console.ResetColor();
        }

        private void PrintSubHeader(string title)
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine($"--- {title} ---");
            Console.ResetColor();
        }

        private void PrintWarning(string message)
        {
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine($"  ⚠ {message}");
            Console.ResetColor();
        }

        private void PrintError(string message)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine(message);
            Console.ResetColor();
        }

        private void PrintSuccess(string message)
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine(message);
            Console.ResetColor();
        }

        private void PrintExitMessage()
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("╔════════════════════════════════════════╗");
            Console.WriteLine("║     ¡Gracias por usar el sistema!    ║");
            Console.WriteLine("╚════════════════════════════════════════╝");
            Console.ResetColor();
        }
    }
}
