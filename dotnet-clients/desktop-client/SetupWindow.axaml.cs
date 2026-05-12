using System;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Media;

namespace desktop_client
{
    public partial class SetupWindow : Window
    {
        public SetupWindow()
        {
            InitializeComponent();

            TxtServerAddress.Text = AppConfig.ServerAddress;
            TxtPort.Text = AppConfig.ServerPort;

            BtnConnect.Click += (s, e) =>
            {
                string address = TxtServerAddress.Text?.Trim() ?? "";
                string port = TxtPort.Text?.Trim() ?? "8080";

                if (string.IsNullOrEmpty(address))
                {
                    ShowError("La direccion del servidor no puede estar vacia.");
                    return;
                }

                if (!IsValidPort(port))
                {
                    ShowError("El puerto debe ser un numero entre 1 y 65535.");
                    return;
                }

                AppConfig.Configure(address, port);

                var mainWindow = new MainWindow();
                mainWindow.Show();

                this.Close();
            };
        }

        private bool IsValidPort(string port)
        {
            if (int.TryParse(port, out int portNum))
            {
                return portNum > 0 && portNum <= 65535;
            }
            return false;
        }

        private void ShowError(string message)
        {
            TxtError.Text = message;
            TxtError.IsVisible = true;
            TxtError.Foreground = new SolidColorBrush(Color.Parse("#D63031"));
        }
    }
}
