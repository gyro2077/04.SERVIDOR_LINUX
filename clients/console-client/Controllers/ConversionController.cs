using System;
using System.Threading.Tasks;
using ConsoleClient.Models;
using ConsoleClient.Views;

namespace ConsoleClient.Controllers
{
    public class ConversionController
    {
        private readonly UserSession _session;
        private readonly MenuView _menuView = new MenuView();
        private readonly ConversionView _conversionView = new ConversionView();
        
        private readonly string[] _massUnits = { "KILOGRAM", "GRAM", "POUND", "OUNCE" };
        private readonly string[] _lengthUnits = { "METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH" };
        private readonly string[] _tempUnits = { "CELSIUS", "FAHRENHEIT", "KELVIN" };

        public ConversionController(UserSession session)
        {
            _session = session;
        }

        public async Task RunAsync()
        {
            while (true)
            {
                int choice = _menuView.RenderMainMenu(_session.Username);

                switch (choice)
                {
                    case 0:
                        await ProcessConversionAsync("Mass", "CONVERSIÓN DE MASA", _massUnits);
                        break;
                    case 1:
                        await ProcessConversionAsync("Length", "CONVERSIÓN DE LONGITUD", _lengthUnits);
                        break;
                    case 2:
                        await ProcessConversionAsync("Temperature", "CONVERSIÓN DE TEMPERATURA", _tempUnits);
                        break;
                    case 3:
                    case -1:
                        ConsoleUIHelper.PrintHeader("CERRANDO SESIÓN");
                        Console.WriteLine("  ¡Gracias por usar el sistema de conversión SOAP!");
                        return;
                }
            }
        }

        private async Task ProcessConversionAsync(string category, string title, string[] units)
        {
            var data = _conversionView.RenderConversionPrompt(title, units);
            _conversionView.RenderLoading();

            using var invoker = new ServiceInvoker();
            try
            {
                var response = await invoker.InvokeConversionAsync(category, data.value, data.fromUnit, data.toUnit);
                _conversionView.RenderResult(response);
            }
            catch (Exception ex)
            {
                _conversionView.RenderError(ex.Message);
            }
        }
    }
}