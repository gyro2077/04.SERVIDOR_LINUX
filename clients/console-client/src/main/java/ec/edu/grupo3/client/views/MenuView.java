package ec.edu.grupo3.client.views;

import java.util.Arrays;
import java.util.List;

public class MenuView {

    public int showMainMenu() {
        List<String> options = Arrays.asList(
            "Conversión de Masa (kg, g, lb, oz)",
            "Conversión de Longitud (m, km, cm, mi, yd, ft, in)",
            "Conversión de Temperatura (C, F, K)",
            "Cerrar Sesión y Salir"
        );
        return ConsoleHelper.showSelectionMenu("SELECCIONE EL SERVICIO A CONSUMIR", options);
    }

    public String selectUnit(String title, List<String> units) {
        int idx = ConsoleHelper.showSelectionMenu(title, units);
        return units.get(idx);
    }

    public double getInputValue(String unit) {
        ConsoleHelper.clearScreen();
        System.out.println(ConsoleHelper.ANSI_BOLD + ConsoleHelper.ANSI_CYAN + "Parámetros de Conversión" + ConsoleHelper.ANSI_RESET);
        System.out.println("────────────────────────");
        return ConsoleHelper.readDouble("Ingrese la cantidad en [" + unit + "]: ");
    }

    public void showResult(String category, double inputVal, String from, double resultVal, String to) {
        ConsoleHelper.clearScreen();
        System.out.println(ConsoleHelper.ANSI_BOLD + ConsoleHelper.ANSI_GREEN + "╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  RESULTADO DE CONVERSIÓN SOAP DEVUELTO                     ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣" + ConsoleHelper.ANSI_RESET);
        System.out.printf("║  Categoría : %-45s ║\n", category);
        System.out.printf("║  Origen    : %.4f %-36s ║\n", inputVal, from);
        System.out.printf("║  Destino   : " + ConsoleHelper.ANSI_BOLD + "%.4f %-36s" + ConsoleHelper.ANSI_RESET + " ║\n", resultVal, to);
        System.out.println(ConsoleHelper.ANSI_BOLD + ConsoleHelper.ANSI_GREEN + "╚════════════════════════════════════════════════════════════╝" + ConsoleHelper.ANSI_RESET);
        System.out.println();
        ConsoleHelper.readString("Presione Enter para regresar al menú principal...");
    }

    public void showError(String err) {
        System.out.println();
        System.out.println(ConsoleHelper.ANSI_RED + "❌ Error del Servicio: " + err + ConsoleHelper.ANSI_RESET);
        ConsoleHelper.readString("Presione Enter para continuar...");
    }
}