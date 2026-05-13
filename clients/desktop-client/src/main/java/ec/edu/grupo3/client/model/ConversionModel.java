package ec.edu.grupo3.client.model;

import ec.edu.grupo3.client.generated.ConversionService;
import ec.edu.grupo3.client.generated.ConversionService_Service;
import ec.edu.grupo3.client.generated.ConversionResponseView;

public class ConversionModel {

    private static final String HARDCODED_TOKEN = "TU9OU1RFUjoxNzc4Njc3MDM0ODMy";
    private ConversionService soapService;

    public ConversionModel() {
        initializeService();
    }

    private void initializeService() {
        try {
            ConversionService_Service service = new ConversionService_Service();
            soapService = service.getConversionServicePort();
            System.out.println("Servicio SOAP inicializado");
        } catch (Exception e) {
            System.err.println("Error al inicializar servicio SOAP: " + e.getMessage());
        }
    }

    public ConversionResponseView convertMass(double value, String fromUnit, String toUnit) throws Exception {
        if (soapService == null) throw new Exception("Servicio SOAP no disponible");
        return soapService.convertMass(HARDCODED_TOKEN, value, fromUnit, toUnit);
    }

    public ConversionResponseView convertLength(double value, String fromUnit, String toUnit) throws Exception {
        if (soapService == null) throw new Exception("Servicio SOAP no disponible");
        return soapService.convertLength(HARDCODED_TOKEN, value, fromUnit, toUnit);
    }

    public ConversionResponseView convertTemperature(double value, String fromUnit, String toUnit) throws Exception {
        if (soapService == null) throw new Exception("Servicio SOAP no disponible");
        return soapService.convertTemperature(HARDCODED_TOKEN, value, fromUnit, toUnit);
    }

    public boolean isServiceAvailable() {
        return soapService != null;
    }
}