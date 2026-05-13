package ec.edu.grupo3.client.models;

import ec.edu.grupo3.client.generated.ConversionService;
import ec.edu.grupo3.client.generated.ConversionService_Service;
import ec.edu.grupo3.client.generated.ConversionResponseView;

public class SoapServiceModel {

    private static final String VALID_TOKEN = "TU9OU1RFUjoxNzc4Njc3MDM0ODMy";

    private ConversionService getPort() {
        ConversionService_Service service = new ConversionService_Service();
        return service.getConversionServicePort();
    }

    public double convertMass(double value, String fromUnit, String toUnit) throws Exception {
        ConversionResponseView response = getPort().convertMass(VALID_TOKEN, value, fromUnit, toUnit);
        return response.getResultValue();
    }

    public double convertLength(double value, String fromUnit, String toUnit) throws Exception {
        ConversionResponseView response = getPort().convertLength(VALID_TOKEN, value, fromUnit, toUnit);
        return response.getResultValue();
    }

    public double convertTemperature(double value, String fromUnit, String toUnit) throws Exception {
        ConversionResponseView response = getPort().convertTemperature(VALID_TOKEN, value, fromUnit, toUnit);
        return response.getResultValue();
    }
}