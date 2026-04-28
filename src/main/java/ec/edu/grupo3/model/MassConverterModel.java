package ec.edu.grupo3.model;

public class MassConverterModel {

    public double convert(double value, MassUnit from, MassUnit to) {
        double valueInKg = switch (from) {
            case KILOGRAM -> value;
            case GRAM -> value / 1000.0;
            case POUND -> value * 0.45359237;
            case OUNCE -> value * 0.028349523125;
        };

        return switch (to) {
            case KILOGRAM -> valueInKg;
            case GRAM -> valueInKg * 1000.0;
            case POUND -> valueInKg / 0.45359237;
            case OUNCE -> valueInKg / 0.028349523125;
        };
    }
}
