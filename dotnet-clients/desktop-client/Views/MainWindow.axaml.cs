using System.Threading.Tasks;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Media;
using desktop_client.Controllers;
using desktop_client.Models;

namespace desktop_client.Views
{
    public partial class MainWindow : Window
    {
        private readonly UserSession _session;
        private readonly ConversionController _controller = new ConversionController();

        private readonly string[] MassUnits = { "KILOGRAM", "GRAM", "POUND", "OUNCE" };
        private readonly string[] LengthUnits = { "METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH" };
        private readonly string[] TempUnits = { "CELSIUS", "FAHRENHEIT", "KELVIN" };

        public MainWindow() : this(new UserSession { Username = "Operador Local", IsAuthenticated = true }) { }

        public MainWindow(UserSession session)
        {
            _session = session;
            InitializeComponent();

            TxtUserGreeting.Text = $"👤 {_session.Username}";

            CmbCategory.SelectionChanged += (s, e) =>
            {
                if (CmbCategory.SelectedItem is ComboBoxItem item && item.Tag != null)
                {
                    LoadUnitsForCategory(item.Tag.ToString()!);
                    TxtResult.Text = "";
                }
            };

            BtnConvert.Click += async (s, e) => await ProcessConversionAsync();

            LoadUnitsForCategory("Mass");
        }

        private void LoadUnitsForCategory(string category)
        {
            var source = category switch
            {
                "Mass" => MassUnits,
                "Length" => LengthUnits,
                "Temperature" => TempUnits,
                _ => MassUnits
            };

            CmbFromUnit.ItemsSource = source;
            CmbToUnit.ItemsSource = source;
            CmbFromUnit.SelectedIndex = 0;
            CmbToUnit.SelectedIndex = source.Length > 1 ? 1 : 0;
        }

        private async Task ProcessConversionAsync()
        {
            if (CmbCategory.SelectedItem is not ComboBoxItem categoryItem ||
                CmbFromUnit.SelectedItem == null || CmbToUnit.SelectedItem == null) return;

            string category = categoryItem.Tag?.ToString() ?? "";
            string fromUnit = CmbFromUnit.SelectedItem.ToString() ?? "";
            string toUnit = CmbToUnit.SelectedItem.ToString() ?? "";
            string valueText = TxtValue.Text ?? "";

            BtnConvert.IsEnabled = false;
            TxtStatus.Text = "Conectando al servidor SOAP y procesando...";
            TxtStatus.Foreground = new SolidColorBrush(Color.Parse("#38BDF8"));

            var response = await _controller.ConvertAsync(category, valueText, fromUnit, toUnit);

            BtnConvert.IsEnabled = true;

            if (response.success)
            {
                TxtResult.Text = response.result.ToString("0.######");
                TxtStatus.Text = "Conversión SOAP realizada con éxito.";
                TxtStatus.Foreground = new SolidColorBrush(Color.Parse("#22C55E"));
            }
            else
            {
                TxtStatus.Text = $"Error: {response.errorMessage}";
                TxtStatus.Foreground = new SolidColorBrush(Color.Parse("#EF4444"));
            }
        }
    }
}