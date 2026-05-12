using System;

namespace ConversionApp.Models
{
    public class MassConverterModel
    {
        public double Convert(double value, MassUnit from, MassUnit to)
        {
            double valueInKg = from switch
            {
                MassUnit.KILOGRAM => value,
                MassUnit.GRAM => value / 1000.0,
                MassUnit.POUND => value * 0.45359237,
                MassUnit.OUNCE => value * 0.028349523125,
                _ => throw new ArgumentException("Unidad de masa no soportada")
            };

            return to switch
            {
                MassUnit.KILOGRAM => valueInKg,
                MassUnit.GRAM => valueInKg * 1000.0,
                MassUnit.POUND => valueInKg / 0.45359237,
                MassUnit.OUNCE => valueInKg / 0.028349523125,
                _ => throw new ArgumentException("Unidad de masa no soportada")
            };
        }
    }
}
