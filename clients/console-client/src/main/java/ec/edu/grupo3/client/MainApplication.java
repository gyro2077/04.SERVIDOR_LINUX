package ec.edu.grupo3.client;

import ec.edu.grupo3.client.controllers.AuthController;
import ec.edu.grupo3.client.controllers.ConversionController;
import ec.edu.grupo3.client.models.UserSession;
import ec.edu.grupo3.client.views.ConsoleHelper;

public class MainApplication {

    public static void main(String[] args) {
        AuthController authController = new AuthController();
        UserSession session = authController.authenticate();

        if (session.isAuthenticated()) {
            ConversionController conversionController = new ConversionController(session);
            conversionController.start();
        }

        ConsoleHelper.clearScreen();
        System.out.println(ConsoleHelper.ANSI_BOLD + ConsoleHelper.ANSI_GREEN + "👋 Sesión finalizada correctamente. ¡Hasta pronto!" + ConsoleHelper.ANSI_RESET);
        System.exit(0);
    }
}