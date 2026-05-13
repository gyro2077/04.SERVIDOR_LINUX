package ec.edu.grupo3.client.views;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ConsoleHelper {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_REVERSE = "\u001B[7m";

    private static final Scanner scanner = new Scanner(System.in);

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void setTerminalRawMode(boolean enable) {
        try {
            String[] cmd = enable 
                ? new String[]{"/bin/sh", "-c", "stty -icanon min 1 -echo < /dev/tty"}
                : new String[]{"/bin/sh", "-c", "stty icanon echo < /dev/tty"};
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (Exception e) {
        }
    }

    public static int showSelectionMenu(String title, List<String> options) {
        int selectedIndex = 0;
        setTerminalRawMode(true);

        try {
            while (true) {
                clearScreen();
                System.out.println(ANSI_BOLD + ANSI_CYAN + "╔════════════════════════════════════════════════════════════╗" + ANSI_RESET);
                System.out.println(ANSI_BOLD + ANSI_CYAN + "║  " + padRight(title, 56) + "║" + ANSI_RESET);
                System.out.println(ANSI_BOLD + ANSI_CYAN + "╠════════════════════════════════════════════════════════════╣" + ANSI_RESET);
                
                for (int i = 0; i < options.size(); i++) {
                    if (i == selectedIndex) {
                        System.out.println(ANSI_CYAN + "║ " + ANSI_REVERSE + "  ► " + padRight(options.get(i), 53) + ANSI_RESET + ANSI_CYAN + " ║" + ANSI_RESET);
                    } else {
                        System.out.println(ANSI_CYAN + "║    " + padRight(options.get(i), 53) + " ║" + ANSI_RESET);
                    }
                }
                
                System.out.println(ANSI_BOLD + ANSI_CYAN + "╚════════════════════════════════════════════════════════════╝" + ANSI_RESET);
                System.out.println(ANSI_YELLOW + "Use las flechas [↑] y [↓] para moverse. Presione [Enter] para seleccionar." + ANSI_RESET);

                int read = System.in.read();
                if (read == 27) {
                    int next1 = System.in.read();
                    int next2 = System.in.read();
                    if (next1 == 91) {
                        if (next2 == 65) {
                            selectedIndex = (selectedIndex > 0) ? selectedIndex - 1 : options.size() - 1;
                        } else if (next2 == 66) {
                            selectedIndex = (selectedIndex < options.size() - 1) ? selectedIndex + 1 : 0;
                        }
                    }
                } else if (read == 10 || read == 13) {
                    break;
                }
            }
        } catch (IOException e) {
            selectedIndex = 0;
        } finally {
            setTerminalRawMode(false);
        }

        return selectedIndex;
    }

    public static String readString(String prompt) {
        System.out.print(ANSI_BOLD + prompt + ANSI_RESET);
        return scanner.nextLine().trim();
    }

    public static String readPassword(String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] pwd = console.readPassword(ANSI_BOLD + prompt + ANSI_RESET);
            return pwd != null ? new String(pwd) : "";
        } else {
            System.out.print(ANSI_BOLD + prompt + ANSI_RESET);
            return scanner.nextLine().trim();
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(ANSI_BOLD + prompt + ANSI_RESET);
            String input = scanner.nextLine().trim().replace(',', '.');
            try {
                double val = Double.parseDouble(input);
                if (val >= 0) return val;
                System.out.println(ANSI_RED + "❌ Ingrese un valor numérico positivo." + ANSI_RESET);
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "❌ Entrada inválida. Ingrese un número válido sin letras." + ANSI_RESET);
            }
        }
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}