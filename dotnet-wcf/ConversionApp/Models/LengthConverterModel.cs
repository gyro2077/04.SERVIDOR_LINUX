using System;

namespace ConversionApp.Models
{
    public class LengthConverterModel
    {
        public double Convert(double value, LengthUnit from, LengthUnit to)
        {
            double valueInMeters = from switch
            {
                LengthUnit.METER => value,
                LengthUnit.KILOMETER => value * 1000.0,
                LengthUnit.CENTIMETER => value / 100.0,
                LengthUnit.MILE => value * 1609.344,
                LengthUnit.YARD => value * 0.9144,
                LengthUnit.FOOT => value * 0.3048,
                LengthUnit.INCH => value * 0.0254,
                _ => throw new ArgumentException("Unidad de longitud no soportada")
            };

            return to switch
            {
                LengthUnit.METER => valueInMeters,
                LengthUnit.KILOMETER => valueInMeters / 1000.0,
                LengthUnit.CENTIMETER => valueInMeters * 100.0,
                LengthUnit.MILE => valueInMeters / 1609.344,
                LengthUnit.YARD => valueInMeters / 0.9144,
                LengthUnit.FOOT => valueInMeters / 0.3048,
                LengthUnit.INCH => valueInMeters / 0.0254,
                _ => throw new ArgumentException("Unidad de longitud no soportada")
            };
        }
    }
}
