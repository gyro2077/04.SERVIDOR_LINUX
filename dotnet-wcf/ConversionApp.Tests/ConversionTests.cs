using ConversionApp.Models;
using Xunit;

namespace ConversionApp.Tests;

public class ConversionTests
{
    private readonly MassConverterModel _massConverter = new MassConverterModel();
    private readonly LengthConverterModel _lengthConverter = new LengthConverterModel();
    private readonly TemperatureConverterModel _temperatureConverter = new TemperatureConverterModel();

    [Fact]
    public void Mass_KilogramToPound_ReturnsCorrectValue()
    {
        double result = _massConverter.Convert(1.0, MassUnit.KILOGRAM, MassUnit.POUND);
        Assert.Equal(2.20462262184878, result, 10);
    }

    [Fact]
    public void Mass_GramToKilogram_ReturnsCorrectValue()
    {
        double result = _massConverter.Convert(1000.0, MassUnit.GRAM, MassUnit.KILOGRAM);
        Assert.Equal(1.0, result, 10);
    }

    [Fact]
    public void Length_MeterToFoot_ReturnsCorrectValue()
    {
        double result = _lengthConverter.Convert(1.0, LengthUnit.METER, LengthUnit.FOOT);
        Assert.Equal(3.28084, result, 4);
    }

    [Fact]
    public void Length_KilometerToMile_ReturnsCorrectValue()
    {
        double result = _lengthConverter.Convert(1.0, LengthUnit.KILOMETER, LengthUnit.MILE);
        Assert.Equal(0.621371, result, 4);
    }

    [Fact]
    public void Temperature_CelsiusToFahrenheit_ReturnsCorrectValue()
    {
        double result = _temperatureConverter.Convert(100.0, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT);
        Assert.Equal(212.0, result, 10);
    }

    [Fact]
    public void Temperature_CelsiusToKelvin_ReturnsCorrectValue()
    {
        double result = _temperatureConverter.Convert(0.0, TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN);
        Assert.Equal(273.15, result, 10);
    }

    [Fact]
    public void Temperature_FahrenheitToCelsius_ReturnsCorrectValue()
    {
        double result = _temperatureConverter.Convert(32.0, TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS);
        Assert.Equal(0.0, result, 10);
    }

    [Fact]
    public void Length_InchToCentimeter_ReturnsCorrectValue()
    {
        double result = _lengthConverter.Convert(1.0, LengthUnit.INCH, LengthUnit.CENTIMETER);
        Assert.Equal(2.54, result, 4);
    }

    [Fact]
    public void Mass_OunceToGram_ReturnsCorrectValue()
    {
        double result = _massConverter.Convert(1.0, MassUnit.OUNCE, MassUnit.GRAM);
        Assert.Equal(28.3495, result, 3);
    }

    [Fact]
    public void Temperature_KelvinToCelsius_ReturnsCorrectValue()
    {
        double result = _temperatureConverter.Convert(273.15, TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS);
        Assert.Equal(0.0, result, 10);
    }
}
