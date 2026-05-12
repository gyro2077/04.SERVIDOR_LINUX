using System;
using System.Threading.Tasks;

namespace ConsoleClient
{
    class Program
    {
        static async Task Main(string[] args)
        {
            Console.Title = "SOAP Conversion Client";
            Console.WriteLine("========================================");
            Console.WriteLine("    Sistema de Conversion Universal");
            Console.WriteLine("========================================");

            var menu = new MenuHandler();
            await menu.RunMainMenuAsync();
        }
    }
}
