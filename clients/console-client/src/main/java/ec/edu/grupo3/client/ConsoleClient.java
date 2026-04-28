package ec.edu.grupo3.client;

import ec.edu.grupo3.client.generated.ConversionService;
import ec.edu.grupo3.client.generated.ConversionSoapWS;
import ec.edu.grupo3.client.generated.ConversionResponse;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceFeature;
import java.net.URL;
import javax.xml.namespace.QName;

/**
 * Cliente de Consola para el Servicio de Conversión de Unidades SOAP
 *
 * Este cliente demuestra dos enfoques para consumir servicios web SOAP:
 * 1. Enfoque con Stubs Generados (usando wsimport)
 * 2. Enfoque Dinámico (sin generar stubs)
 *
 * @author Grupo 3 - Arquitectura de Software
 * @version 1.0
 */
public class ConsoleClient {

    // URL del WSDL del servicio
    private static final String WSDL_URL = "http://localhost:8080/04.SERVIDOR/conversion?wsdl";

    // Namespace del servicio (del WSDL)
    private static final String NAMESPACE_URI = "http://ws.grupo3.edu.ec/";

    // Nombre del servicio (del WSDL)
    private static final String SERVICE_NAME = "ConversionService";

    /**
     * Método principal que ejecuta el cliente de consola
     *
     * Flujo de ejecución:
     * 1. Verifica que el servidor esté disponible
     * 2. Muestra un menú interactivo
     * 3. Procesa la conversión seleccionada
     * 4. Muestra el resultado
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  Cliente de Conversión de Unidades - SOAP Service          ║");
        System.out.println("║  Arquitectura de Software - Grupo 3                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Verificar que el servidor esté disponible
        if (!isServerAvailable()) {
            System.err.println("❌ Error: El servidor SOAP no está disponible en: " + WSDL_URL);
            System.err.println("   Asegúrate de que Payara esté corriendo:");
            System.err.println("   sudo systemctl start payara.service");
            System.exit(1);
        }

        System.out.println("✅ Servidor SOAP disponible en: " + WSDL_URL);
        System.out.println();

        // Menú principal
        while (true) {
            showMainMenu();
            int choice = getUserChoice(1, 4);

            switch (choice) {
                case 1:
                    handleMassConversion();
                    break;
                case 2:
                    handleLengthConversion();
                    break;
                case 3:
                    handleTemperatureConversion();
                    break;
                case 4:
                    System.out.println("👋 ¡Hasta luego!");
                    System.exit(0);
            }

            System.out.println();
            System.out.println("Presiona Enter para continuar...");
            readLine();
        }
    }

    /**
     * Verifica si el servidor SOAP está disponible
     *
     * @return true si el servidor responde, false en caso contrario
     */
    private static boolean isServerAvailable() {
        try {
            URL url = new URL(WSDL_URL);
            url.openConnection().connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Muestra el menú principal de opciones
     */
    private static void showMainMenu() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  Seleccione el tipo de conversión:                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Masa (Kilogramos, Gramos, Libras, Onzas)               ║");
        System.out.println("║  2. Longitud (Metros, Kilómetros, Centímetros, etc.)      ║");
        System.out.println("║  3. Temperatura (Celsius, Fahrenheit, Kelvin)            ║");
        System.out.println("║  4. Salir                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("👉 Opción: ");
    }

    /**
     * Obtiene la elección del usuario con validación
     *
     * @param min valor mínimo aceptado
     * @param max valor máximo aceptado
     * @return elección validada del usuario
     */
    private static int getUserChoice(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(readLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.print("❌ Opción inválida. Intente nuevamente: ");
            } catch (NumberFormatException e) {
                System.out.print("❌ Ingrese un número válido: ");
            }
        }
    }

    /**
     * Maneja la conversión de masa usando el enfoque con stubs generados
     *
     * Flujo:
     * 1. Solicita el valor y unidades al usuario
     * 2. Crea el servicio usando stubs generados
     * 3. Llama al método remoto
     * 4. Muestra el resultado
     */
    private static void handleMassConversion() {
        System.out.println();
        System.out.println("📊 Conversión de Masa");
        System.out.println("─────────────────────");

        // Solicitar datos al usuario
        double value = getValueFromUser("Ingrese el valor a convertir: ");
        String fromUnit = getUnitFromUser("Unidad de origen (KILOGRAM, GRAM, POUND, OUNCE): ");
        String toUnit = getUnitFromUser("Unidad de destino (KILOGRAM, GRAM, POUND, OUNCE): ");

        try {
            System.out.println();
            System.out.println("🔄 Conectando al servicio SOAP...");

            // ENFOQUE CON STUBS GENERADOS
            // Los stubs fueron generados automáticamente por wsimport
            // basándose en el WSDL del servicio

            // 1. Crear instancia del servicio (clase generada)
            ConversionService service = new ConversionService();

            // 2. Obtener el puerto (endpoint) del servicio
            // El puerto es la interfaz que contiene los métodos del servicio
            ConversionSoapWS port = service.getConversionSoapWSPort();

            // 3. Llamar al método remoto como si fuera un método local
            // JAX-WS maneja automáticamente:
            // - Serialización de parámetros a XML
            // - Envío de la petición HTTP
            // - Deserialización de la respuesta XML
            ConversionResponse response = port.convertMass(value, fromUnit, toUnit);

            // 4. Mostrar el resultado
            displayConversionResult(response);

        } catch (Exception e) {
            System.err.println("❌ Error al realizar la conversión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja la conversión de longitud usando el enfoque dinámico
     *
     * Flujo:
     * 1. Solicita el valor y unidades al usuario
     * 2. Crea el servicio dinámicamente sin stubs
     * 3. Llama al método remoto usando reflexión
     * 4. Muestra el resultado
     */
    private static void handleLengthConversion() {
        System.out.println();
        System.out.println("📏 Conversión de Longitud");
        System.out.println("────────────────────────");

        // Solicitar datos al usuario
        double value = getValueFromUser("Ingrese el valor a convertir: ");
        String fromUnit = getUnitFromUser("Unidad de origen (METER, KILOMETER, CENTIMETER, MILE, YARD, FOOT, INCH): ");
        String toUnit = getUnitFromUser("Unidad de destino (METER, KILOMETER, CENTIMETER, MILE, YARD, FOOT, INCH): ");

        try {
            System.out.println();
            System.out.println("🔄 Conectando al servicio SOAP...");

            // ENFOQUE DINÁMICO (SIN STUBS)
            // Este enfoque no requiere generar stubs con wsimport
            // Es más flexible pero menos type-safe

            // 1. Crear URL del WSDL
            URL wsdlUrl = new URL(WSDL_URL);

            // 2. Crear QName (Qualified Name) del servicio
            // El QName identifica el servicio en el WSDL
            QName serviceName = new QName(NAMESPACE_URI, SERVICE_NAME);

            // 3. Crear el servicio dinámicamente
            Service service = Service.create(wsdlUrl, serviceName);

            // 4. Obtener el puerto del servicio
            // El puerto es la interfaz que contiene los métodos
            ConversionSoapWS port = service.getPort(ConversionSoapWS.class);

            // 5. Llamar al método remoto
            // Aunque no tenemos stubs, JAX-WS genera proxies dinámicos
            ConversionResponse response = port.convertLength(value, fromUnit, toUnit);

            // 6. Mostrar el resultado
            displayConversionResult(response);

        } catch (Exception e) {
            System.err.println("❌ Error al realizar la conversión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja la conversión de temperatura
     *
     * Flujo similar a las otras conversiones
     */
    private static void handleTemperatureConversion() {
        System.out.println();
        System.out.println("🌡️  Conversión de Temperatura");
        System.out.println("────────────────────────────");

        // Solicitar datos al usuario
        double value = getValueFromUser("Ingrese el valor a convertir: ");
        String fromUnit = getUnitFromUser("Unidad de origen (CELSIUS, FAHRENHEIT, KELVIN): ");
        String toUnit = getUnitFromUser("Unidad de destino (CELSIUS, FAHRENHEIT, KELVIN): ");

        try {
            System.out.println();
            System.out.println("🔄 Conectando al servicio SOAP...");

            // Usar el enfoque con stubs generados
            ConversionService service = new ConversionService();
            ConversionSoapWS port = service.getConversionSoapWSPort();

            // Llamar al método remoto
            ConversionResponse response = port.convertTemperature(value, fromUnit, toUnit);

            // Mostrar el resultado
            displayConversionResult(response);

        } catch (Exception e) {
            System.err.println("❌ Error al realizar la conversión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene un valor numérico del usuario con validación
     *
     * @param prompt mensaje para mostrar al usuario
     * @return valor validado
     */
    private static double getValueFromUser(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(readLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("❌ Ingrese un número válido: ");
            }
        }
    }

    /**
     * Obtiene una unidad del usuario
     *
     * @param prompt mensaje para mostrar al usuario
     * @return unidad ingresada por el usuario
     */
    private static String getUnitFromUser(String prompt) {
        System.out.print(prompt);
        return readLine().trim().toUpperCase();
    }

    /**
     * Muestra el resultado de la conversión de forma formateada
     *
     * @param response respuesta del servicio SOAP
     */
    private static void displayConversionResult(ConversionResponse response) {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ Resultado de la Conversión                             ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  Tipo:        " + padRight(response.getCategory(), 40) + "║");
        System.out.println("║  De:          " + padRight(response.getFromUnit(), 40) + "║");
        System.out.println("║  A:           " + padRight(response.getToUnit(), 40) + "║");
        System.out.println("║  Valor:       " + padRight(String.format("%.4f", response.getInputValue()), 40) + "║");
        System.out.println("║  Resultado:   " + padRight(String.format("%.4f", response.getResultValue()), 40) + "║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    /**
     * Utilidad para rellenar texto a la derecha
     *
     * @param text texto a rellenar
     * @param length longitud total deseada
     * @return texto rellenado con espacios
     */
    private static String padRight(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }
        return String.format("%-" + length + "s", text);
    }

    /**
     * Lee una línea de la entrada estándar
     *
     * @return línea leída
     */
    private static String readLine() {
        try {
            return new java.util.Scanner(System.in).nextLine();
        } catch (Exception e) {
            return "";
        }
    }
}