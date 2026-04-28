package ec.edu.grupo3.model;

public class LengthConverterModel {

    public double convert(double value, LengthUnit from, LengthUnit to) {
        double valueInMeters = switch (from) {
            case METER -> value;
            case KILOMETER -> value * 1000.0;
            case CENTIMETER -> value / 100.0;
            case MILE -> value * 1609.344;
            case YARD -> value * 0.9144;
            case FOOT -> value * 0.3048;
            case INCH -> value * 0.0254;
        };

        return switch (to) {
            case METER -> valueInMeters;
            case KILOMETER -> valueInMeters / 1000.0;
            case CENTIMETER -> valueInMeters * 100.0;
            case MILE -> valueInMeters / 1609.344;
            case YARD -> valueInMeters / 0.9144;
            case FOOT -> valueInMeters / 0.3048;
            case INCH -> valueInMeters / 0.0254;
        };
    }
}
