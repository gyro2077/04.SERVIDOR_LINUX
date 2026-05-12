using System;
using CoreWCF;
using ConversionApp.Models;
using ConversionApp.Views;

namespace ConversionApp.Controllers
{
    [ServiceBehavior(Namespace = "http://ws.grupo3.edu.ec/")]
    public class ConversionService : IConversionService
    {
        private readonly MassConverterModel _massConverter = new MassConverterModel();
        private readonly LengthConverterModel _lengthConverter = new LengthConverterModel();
        private readonly TemperatureConverterModel _temperatureConverter = new TemperatureConverterModel();

        public ConversionResponseView ConvertMass(double value, string fromUnit, string toUnit)
        {
            var from = (MassUnit)Enum.Parse(typeof(MassUnit), fromUnit, true);
            var to = (MassUnit)Enum.Parse(typeof(MassUnit), toUnit, true);

            double result = _massConverter.Convert(value, from, to);

            return new ConversionResponseView("MASS", from.ToString(), to.ToString(), value, result);
        }

        public ConversionResponseView ConvertLength(double value, string fromUnit, string toUnit)
        {
            var from = (LengthUnit)Enum.Parse(typeof(LengthUnit), fromUnit, true);
            var to = (LengthUnit)Enum.Parse(typeof(LengthUnit), toUnit, true);

            double result = _lengthConverter.Convert(value, from, to);

            return new ConversionResponseView("LENGTH", from.ToString(), to.ToString(), value, result);
        }

        public ConversionResponseView ConvertTemperature(double value, string fromUnit, string toUnit)
        {
            var from = (TemperatureUnit)Enum.Parse(typeof(TemperatureUnit), fromUnit, true);
            var to = (TemperatureUnit)Enum.Parse(typeof(TemperatureUnit), toUnit, true);

            double result = _temperatureConverter.Convert(value, from, to);

            return new ConversionResponseView("TEMPERATURE", from.ToString(), to.ToString(), value, result);
        }
    }
}
