package ec.edu.grupo3.webclient.models;

public record ConversionResponse(
    String category,
    String fromUnit,
    String toUnit,
    double inputValue,
    double resultValue,
    String message
) {}