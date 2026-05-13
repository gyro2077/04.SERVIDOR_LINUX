using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Media;
using desktop_client.Models;

namespace desktop_client.Views
{
    public partial class SetupWindow : Window
    {
        public SetupWindow()
        {
            InitializeComponent();
            TxtServerAddress.Text = AppConfig.ServerAddress;
            TxtPort.Text = AppConfig.ServerPort;
            BtnConnect.Click += BtnConnect_Click;
        }

        private void BtnConnect_Click(object? sender, RoutedEventArgs e)
        {
            string address = TxtServerAddress.Text?.Trim() ?? "";
            string port = TxtPort.Text?.Trim() ?? "8080";

            if (string.IsNullOrEmpty(address))
            {
                ShowError("La dirección del servidor es obligatoria.");
                return;
            }

            if (!int.TryParse(port, out int portNum) || portNum <= 0 || portNum > 65535)
            {
                ShowError("Ingrese un puerto válido (1 - 65535).");
                return;
            }

            AppConfig.Configure(address, port);

            var loginWindow = new LoginWindow();
            loginWindow.Show();
            this.Close();
        }

        private void ShowError(string message)
        {
            TxtError.Text = message;
            TxtError.IsVisible = true;
        }
    }
}