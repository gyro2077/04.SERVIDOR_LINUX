using System;

namespace ConversionApp.Models
{
    public class TemperatureConverterModel
    {
        public double Convert(double value, TemperatureUnit from, TemperatureUnit to)
        {
            double valueInCelsius = ToCelsius(value, from);
            return FromCelsius(valueInCelsius, to);
        }

        private double ToCelsius(double value, TemperatureUnit unit)
        {
            return unit switch
            {
                TemperatureUnit.CELSIUS => value,
                TemperatureUnit.FAHRENHEIT => (value - 32.0) * 5.0 / 9.0,
                TemperatureUnit.KELVIN => value - 273.15,
                _ => throw new ArgumentException("Unidad de temperatura no soportada")
            };
        }

        private double FromCelsius(double value, TemperatureUnit unit)
        {
            return unit switch
            {
                TemperatureUnit.CELSIUS => value,
                TemperatureUnit.FAHRENHEIT => (value * 9.0 / 5.0) + 32.0,
                TemperatureUnit.KELVIN => value + 273.15,
                _ => throw new ArgumentException("Unidad de temperatura no soportada")
            };
        }
    }
}
