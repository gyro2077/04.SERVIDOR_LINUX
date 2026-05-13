package ec.edu.grupo3.client.models;

import ec.edu.grupo3.client.generated.ConversionService;
import ec.edu.grupo3.client.generated.ConversionSoapWS;
import ec.edu.grupo3.client.generated.ConversionResponse;

public class SoapServiceModel {

    private ConversionSoapWS getPort() {
        ConversionService service = new ConversionService();
        return service.getConversionSoapWSPort();
    }

    public double convertMass(double value, String fromUnit, String toUnit) throws Exception {
        ConversionResponse response = getPort().convertMass(value, fromUnit, toUnit);
        return response.getResultValue();
    }

    public double convertLength(double value, String fromUnit, String toUnit) throws Exception {
        ConversionResponse response = getPort().convertLength(value, fromUnit, toUnit);
        return response.getResultValue();
    }

    public double convertTemperature(double value, String fromUnit, String toUnit) throws Exception {
        ConversionResponse response = getPort().convertTemperature(value, fromUnit, toUnit);
        return response.getResultValue();
    }
}