using System;

namespace ConsoleClient.Views
{
    public class MenuView
    {
        public int RenderMainMenu(string username)
        {
            ConsoleUIHelper.PrintHeader($"MENÚ PRINCIPAL - Usuario: {username}");
            Console.WriteLine("  Seleccione el servicio que desea consumir:\n");

            return ConsoleUIHelper.ShowInteractiveMenu(new[]
            {
                "Conversión de Masa",
                "Conversión de Longitud",
                "Conversión de Temperatura",
                "Cerrar Sesión / Salir"
            });
        }
    }
}