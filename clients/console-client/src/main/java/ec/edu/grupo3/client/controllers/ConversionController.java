package ec.edu.grupo3.client.controllers;

import ec.edu.grupo3.client.models.SoapServiceModel;
import ec.edu.grupo3.client.models.UserSession;
import ec.edu.grupo3.client.views.MenuView;
import java.util.Arrays;
import java.util.List;

public class ConversionController {

    private final MenuView menuView = new MenuView();
    private final SoapServiceModel model = new SoapServiceModel();
    private final UserSession session;

    private final List<String> massUnits = Arrays.asList("KILOGRAM", "GRAM", "POUND", "OUNCE");
    private final List<String> lengthUnits = Arrays.asList("METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH");
    private final List<String> tempUnits = Arrays.asList("CELSIUS", "FAHRENHEIT", "KELVIN");

    public ConversionController(UserSession session) {
        this.session = session;
    }

    public void start() {
        while (true) {
            int choice = menuView.showMainMenu();
            switch (choice) {
                case 0:
                    processConversion("Mass", massUnits);
                    break;
                case 1:
                    processConversion("Length", lengthUnits);
                    break;
                case 2:
                    processConversion("Temperature", tempUnits);
                    break;
                case 3:
                    return;
            }
        }
    }

    private void processConversion(String category, List<String> units) {
        String from = menuView.selectUnit("SELECCIONE UNIDAD DE ORIGEN (" + category + ")", units);
        String to = menuView.selectUnit("SELECCIONE UNIDAD DE DESTINO (" + category + ")", units);
        double inputVal = menuView.getInputValue(from);

        try {
            double resultVal = 0;
            if ("Mass".equals(category)) resultVal = model.convertMass(inputVal, from, to);
            else if ("Length".equals(category)) resultVal = model.convertLength(inputVal, from, to);
            else if ("Temperature".equals(category)) resultVal = model.convertTemperature(inputVal, from, to);

            menuView.showResult(category, inputVal, from, resultVal, to);
        } catch (Exception e) {
            menuView.showError(e.getMessage() != null ? e.getMessage() : "Error de conexión al servidor Payara.");
        }
    }
}