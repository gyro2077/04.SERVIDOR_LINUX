using System;
using System.Threading.Tasks;

namespace ConsoleClient.Views
{
    public class AuthView
    {
        public (string username, string password) RenderLoginPrompt()
        {
            ConsoleUIHelper.PrintHeader("SISTEMA DE CONVERSIÓN - AUTENTICACIÓN");
            
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine("  Por favor, ingrese sus credenciales de acceso.");
            Console.ResetColor();
            Console.WriteLine();

            Console.Write("  👤 Usuario: ");
            string username = Console.ReadLine()?.Trim() ?? "";

            Console.Write("  🔑 Contraseña: ");
            string password = ConsoleUIHelper.ReadMaskedPassword();

            return (username, password);
        }

        public void RenderAuthError()
        {
            Console.WriteLine();
            ConsoleUIHelper.PrintError("Credenciales incorrectas. Acceso denegado.");
            Console.WriteLine("\n  Presione ENTER para intentar nuevamente...");
            Console.ReadLine();
        }

        public void RenderAuthSuccess(string username)
        {
            Console.WriteLine();
            ConsoleUIHelper.PrintSuccess($"¡Bienvenido, {username}! Autenticación exitosa.");
            Task.Delay(1000).Wait();
        }
    }
}