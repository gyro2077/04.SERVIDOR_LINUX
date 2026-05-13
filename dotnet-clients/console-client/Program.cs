using System;
using System.Threading.Tasks;
using ConsoleClient.Controllers;
using ConsoleClient.Models;
using ConsoleClient.Views;

namespace ConsoleClient
{
    class Program
    {
        static async Task Main(string[] args)
        {
            Console.Title = "SOAP Conversion Client — VPS";

            bool authenticated = false;
            int attempts = 0;
            const int maxAttempts = 3;

            while (!authenticated && attempts < maxAttempts)
            {
                var (user, pass) = AuthView.PromptCredentials();
                attempts++;

                if (AuthController.Authenticate(user, pass))
                {
                    AuthView.ShowSuccess();
                    authenticated = true;
                }
                else
                {
                    int remaining = maxAttempts - attempts;
                    string msg = remaining > 0
                        ? $"Credenciales incorrectas. Intentos restantes: {remaining}"
                        : "Acceso bloqueado. Demasiados intentos fallidos.";
                    AuthView.ShowError(msg);
                }
            }

            if (!authenticated) return;

            using var model = new SoapServiceModel();
            var controller = new ConversionController(model);
            var menu = new MenuHandler(controller);

            await menu.RunMainMenuAsync();
        }
    }
}