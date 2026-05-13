using System;

namespace ConsoleClient.Views
{
    public static class AuthView
    {
        public static (string user, string pass) PromptCredentials()
        {
            Console.Clear();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("╔════════════════════════════════════════╗");
            Console.WriteLine("║      SISTEMA DE CONVERSION SOAP       ║");
            Console.WriteLine("║         Acceso al Sistema             ║");
            Console.WriteLine("╚════════════════════════════════════════╝");
            Console.ResetColor();
            Console.WriteLine();

            Console.Write("  Usuario: ");
            string user = Console.ReadLine()?.Trim() ?? "";

            Console.Write("  Contraseña: ");
            string pass = ReadMaskedPassword();

            return (user, pass);
        }

        public static void ShowError(string message)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine($"\n  ✗ {message}");
            Console.ResetColor();
            Console.WriteLine("  Presione ENTER para intentar de nuevo...");
            Console.ReadLine();
        }

        public static void ShowSuccess()
        {
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("\n  ✓ Acceso concedido. Iniciando sistema...");
            Console.ResetColor();
            System.Threading.Thread.Sleep(800);
        }

        private static string ReadMaskedPassword()
        {
            var pass = new System.Text.StringBuilder();
            ConsoleKeyInfo key;
            while ((key = Console.ReadKey(true)).Key != ConsoleKey.Enter)
            {
                if (key.Key == ConsoleKey.Backspace && pass.Length > 0)
                {
                    pass.Remove(pass.Length - 1, 1);
                    Console.Write("\b \b");
                }
                else if (key.KeyChar >= ' ')
                {
                    pass.Append(key.KeyChar);
                    Console.Write('*');
                }
            }
            Console.WriteLine();
            return pass.ToString();
        }
    }
}