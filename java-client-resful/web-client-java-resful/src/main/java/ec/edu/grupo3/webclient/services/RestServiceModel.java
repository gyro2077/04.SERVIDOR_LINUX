package ec.edu.grupo3.webclient.services;

import ec.edu.grupo3.webclient.models.ConversionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RestServiceModel {

    private static final String BASE_URL = "http://209.145.48.25:8082/ROOT/api";
    private static final String VALID_TOKEN = "TU9OU1RFUjoxNzc4Njc3MDM0ODMy";

    private final RestClient restClient = RestClient.create();

    public ConversionResponse convert(String category, double value, String fromUnit, String toUnit) {
        String endpoint = switch (category) {
            case "Mass" -> "/convert/mass";
            case "Length" -> "/convert/length";
            case "Temperature" -> "/convert/temperature";
            default -> throw new IllegalArgumentException("Categoria invalida");
        };

        ConversionResponse response = restClient.post()
            .uri(BASE_URL + endpoint + "?token={token}&value={value}&fromUnit={from}&toUnit={to}",
                VALID_TOKEN, value, fromUnit, toUnit)
            .retrieve()
            .body(ConversionResponse.class);

        if (response == null) {
            throw new IllegalStateException("Respuesta vacia del servidor RESTful");
        }

        if ("ERROR".equalsIgnoreCase(response.category())) {
            throw new IllegalStateException(response.message());
        }

        return response;
    }
}