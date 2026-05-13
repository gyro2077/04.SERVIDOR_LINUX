namespace ConsoleClient.Controllers
{
    public static class AuthController
    {
        private const string ValidUser = "MONSTER";
        private const string ValidPass = "MONSTER9";

        public static bool Authenticate(string username, string password)
            => username == ValidUser && password == ValidPass;
    }
}