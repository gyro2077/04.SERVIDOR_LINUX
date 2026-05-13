package ec.edu.grupo3.client.model;

import ec.edu.grupo3.client.generated.ConversionService;
import ec.edu.grupo3.client.generated.ConversionSoapWS;
import ec.edu.grupo3.client.generated.ConversionResponse;

public class ConversionModel {

    private static final String WSDL_URL = "http://localhost:8080/04.SERVIDOR/conversion?wsdl";
    private ConversionSoapWS soapService;

    public ConversionModel() {
        initializeService();
    }

    private void initializeService() {
        try {
            ConversionService service = new ConversionService();
            soapService = service.getConversionSoapWSPort();
            System.out.println("Servicio SOAP inicializado");
        } catch (Exception e) {
            System.err.println("Error al inicializar servicio SOAP: " + e.getMessage());
        }
    }

    public ConversionResponse convertMass(double value, String fromUnit, String toUnit) throws Exception {
        if (soapService == null) throw new Exception("Servicio SOAP no disponible");
        return soapService.convertMass(value, fromUnit, toUnit);
    }

    public ConversionResponse convertLength(double value, String fromUnit, String toUnit) throws Exception {
        if (soapService == null) throw new Exception("Servicio SOAP no disponible");
        return soapService.convertLength(value, fromUnit, toUnit);
    }

    public ConversionResponse convertTemperature(double value, String fromUnit, String toUnit) throws Exception {
        if (soapService == null) throw new Exception("Servicio SOAP no disponible");
        return soapService.convertTemperature(value, fromUnit, toUnit);
    }

    public boolean isServiceAvailable() {
        return soapService != null;
    }
}