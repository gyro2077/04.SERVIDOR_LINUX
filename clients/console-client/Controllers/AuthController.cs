using System.Threading.Tasks;
using ConsoleClient.Models;
using ConsoleClient.Views;

namespace ConsoleClient.Controllers
{
    public class AuthController
    {
        private readonly AuthView _authView = new AuthView();
        
        private const string VALID_USER = "MONSTER";
        private const string VALID_PASS = "MONSTER9";

        public async Task StartAsync()
        {
            while (true)
            {
                var creds = _authView.RenderLoginPrompt();

                if (creds.username == VALID_USER && creds.password == VALID_PASS)
                {
                    var session = new UserSession { Username = creds.username, IsAuthenticated = true };
                    _authView.RenderAuthSuccess(session.Username);

                    var conversionController = new ConversionController(session);
                    await conversionController.RunAsync();
                    return;
                }
                else
                {
                    _authView.RenderAuthError();
                }
            }
        }
    }
}