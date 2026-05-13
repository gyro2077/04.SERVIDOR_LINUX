using System;
using System.Threading.Tasks;
using desktop_client.Models;

namespace desktop_client.Controllers
{
    public class ConversionController
    {
        private readonly SoapServiceInvoker _serviceInvoker = new SoapServiceInvoker();

        public async Task<(bool success, double result, string errorMessage)> ConvertAsync(
            string category, string valueText, string fromUnit, string toUnit)
        {
            if (string.IsNullOrWhiteSpace(valueText))
            {
                return (false, 0, "Por favor, ingrese un valor para convertir.");
            }

            string normalizedText = valueText.Replace(',', '.');
            if (!double.TryParse(normalizedText, out double value))
            {
                return (false, 0, "El valor ingresado no es un número válido.");
            }

            if (value < 0)
            {
                return (false, 0, "Solo se permiten valores positivos.");
            }

            if (string.IsNullOrEmpty(category) || string.IsNullOrEmpty(fromUnit) || string.IsNullOrEmpty(toUnit))
            {
                return (false, 0, "Seleccione las unidades de origen y destino.");
            }

            try
            {
                double result = await _serviceInvoker.InvokeConversionAsync(category, value, fromUnit, toUnit);
                return (true, result, "");
            }
            catch (Exception ex)
            {
                return (false, 0, ex.Message);
            }
        }
    }
}