using System.Threading.Tasks;
using ConsoleClient.Models;

namespace ConsoleClient.Controllers
{
    public class ConversionController
    {
        private readonly SoapServiceModel _model;

        public ConversionController(SoapServiceModel model)
        {
            _model = model;
        }

        public Task<ConversionResult> ConvertMassAsync(double value, string from, string to)
            => _model.ConvertMassAsync(value, from, to);

        public Task<ConversionResult> ConvertLengthAsync(double value, string from, string to)
            => _model.ConvertLengthAsync(value, from, to);

        public Task<ConversionResult> ConvertTemperatureAsync(double value, string from, string to)
            => _model.ConvertTemperatureAsync(value, from, to);
    }
}