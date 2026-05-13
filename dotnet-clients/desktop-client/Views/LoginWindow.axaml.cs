using Avalonia.Controls;
using Avalonia.Interactivity;
using desktop_client.Models;

namespace desktop_client.Views
{
    public partial class LoginWindow : Window
    {
        private const string VALID_USER = "MONSTER";
        private const string VALID_PASS = "MONSTER9";

        public LoginWindow()
        {
            InitializeComponent();
            BtnLogin.Click += BtnLogin_Click;
        }

        private void BtnLogin_Click(object? sender, RoutedEventArgs e)
        {
            string username = TxtUsername.Text?.Trim() ?? "";
            string password = TxtPassword.Text ?? "";

            if (username == VALID_USER && password == VALID_PASS)
            {
                var session = new UserSession { Username = username, IsAuthenticated = true };
                
                var mainWindow = new MainWindow(session);
                mainWindow.Show();
                this.Close();
            }
            else
            {
                TxtAuthError.Text = "Usuario o contraseña incorrectos.";
                TxtAuthError.IsVisible = true;
            }
        }
    }
}