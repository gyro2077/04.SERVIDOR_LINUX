package ec.edu.grupo3.model;

public class TemperatureConverterModel {

    public double convert(double value, TemperatureUnit from, TemperatureUnit to) {
        double valueInCelsius = toCelsius(value, from);
        return fromCelsius(valueInCelsius, to);
    }

    private double toCelsius(double value, TemperatureUnit unit) {
        return switch (unit) {
            case CELSIUS -> value;
            case FAHRENHEIT -> (value - 32.0) * 5.0 / 9.0;
            case KELVIN -> value - 273.15;
        };
    }

    private double fromCelsius(double value, TemperatureUnit unit) {
        return switch (unit) {
            case CELSIUS -> value;
            case FAHRENHEIT -> (value * 9.0 / 5.0) + 32.0;
            case KELVIN -> value + 273.15;
        };
    }
}
