using System;
using System.Threading.Tasks;
using ConsoleClient.Controllers;

namespace ConsoleClient
{
    class Program
    {
        static async Task Main(string[] args)
        {
            Console.Title = "SOAP Conversion Client - MVC & Auth";
            
            var authController = new AuthController();
            await authController.StartAsync();
        }
    }
}