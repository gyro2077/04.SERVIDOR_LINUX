package ec.edu.grupo3.client.views;

public class AuthView {

    public void showBanner() {
        ConsoleHelper.clearScreen();
        System.out.println(ConsoleHelper.ANSI_BOLD + ConsoleHelper.ANSI_CYAN + "╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         SISTEMA UNIVERSAL DE CONVERSIÓN SOAP               ║");
        System.out.println("║               ACCESO CORPORATIVO SEGURO                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝" + ConsoleHelper.ANSI_RESET);
        System.out.println();
    }

    public String getUsername() {
        return ConsoleHelper.readString("👤 Ingrese Usuario: ");
    }

    public String getPassword() {
        return ConsoleHelper.readPassword("🔑 Ingrese Contraseña: ");
    }

    public void showError(String msg) {
        System.out.println();
        System.out.println(ConsoleHelper.ANSI_RED + "❌ " + msg + ConsoleHelper.ANSI_RESET);
        System.out.println("Presione Enter para reintentar...");
        ConsoleHelper.readString("");
    }

    public void showSuccess(String user) {
        System.out.println();
        System.out.println(ConsoleHelper.ANSI_GREEN + "✅ Autenticación exitosa. Bienvenido, " + user + "!" + ConsoleHelper.ANSI_RESET);
        try { Thread.sleep(1000); } catch (InterruptedException e){}
    }
}