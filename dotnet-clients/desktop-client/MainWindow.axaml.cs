using System;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Media;

namespace desktop_client
{
    public partial class MainWindow : Window
    {
        private readonly string[] MassUnits = { "KILOGRAM", "GRAM", "POUND", "OUNCE" };
        private readonly string[] LengthUnits = { "METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH" };
        private readonly string[] TempUnits = { "CELSIUS", "FAHRENHEIT", "KELVIN" };
        private readonly HttpClient _httpClient;

        public MainWindow()
        {
            InitializeComponent();

            _httpClient = new HttpClient { Timeout = TimeSpan.FromSeconds(30) };

            CmbCategory.SelectionChanged += (s, e) =>
            {
                if (CmbCategory.SelectedItem is ComboBoxItem selectedItem)
                {
                    string? category = selectedItem.Tag?.ToString();
                    if (!string.IsNullOrEmpty(category))
                    {
                        LoadUnitsForCategory(category);
                        ClearResults();
                    }
                }
            };

            BtnConvert.Click += async (s, e) => await BtnConvert_Click();

            LoadUnitsForCategory("Mass");
        }

        private void LoadUnitsForCategory(string category)
        {
            string[] source = category switch
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

        private void ClearResults()
        {
            TxtResult.Text = "";
            TxtValue.Text = "";
            TxtStatus.Text = "Listo.";
            TxtStatus.Foreground = Brushes.Gray;
        }

        private async Task BtnConvert_Click()
        {
            if (string.IsNullOrWhiteSpace(TxtValue.Text))
            {
                ShowMessage("Por favor, ingrese un valor para convertir.", "Validacion");
                return;
            }

            string normalizedText = TxtValue.Text.Replace(',', '.');
            if (!double.TryParse(normalizedText, out double value))
            {
                ShowMessage("El valor ingresado no es un numero valido.", "Error de Formato");
                return;
            }

            if (value < 0)
            {
                ShowMessage("Solo se permiten valores positivos.", "Validacion");
                return;
            }

            if (CmbCategory.SelectedItem is not ComboBoxItem categoryItem ||
                CmbFromUnit.SelectedItem == null ||
                CmbToUnit.SelectedItem == null)
            {
                ShowMessage("Seleccione las unidades de origen y destino.", "Validacion");
                return;
            }

            string? category = categoryItem.Tag?.ToString();
            string? fromUnit = CmbFromUnit.SelectedItem?.ToString();
            string? toUnit = CmbToUnit.SelectedItem?.ToString();

            if (string.IsNullOrEmpty(category) || string.IsNullOrEmpty(fromUnit) || string.IsNullOrEmpty(toUnit))
            {
                ShowMessage("Error interno: datos incompletos.", "Error");
                return;
            }

            await PerformConversionAsync(category, fromUnit, toUnit, value);
        }

        private async Task PerformConversionAsync(string category, string fromUnit, string toUnit, double value)
        {
            BtnConvert.IsEnabled = false;
            TxtStatus.Text = "Conectando al servidor SOAP...";
            TxtStatus.Foreground = new SolidColorBrush(Color.Parse("#0984E3"));

            try
            {
                string endpointAddress = AppConfig.EndpointAddress;
                string soapAction = $"http://ws.grupo3.edu.ec/ConversionService/convert{category}";
                string methodName = $"convert{category}";

                string xmlRequest = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <{methodName} xmlns=""http://ws.grupo3.edu.ec/"">
      <value>{value}</value>
      <fromUnit>{fromUnit}</fromUnit>
      <toUnit>{toUnit}</toUnit>
    </{methodName}>
  </soap:Body>
</soap:Envelope>";

                var httpContent = new StringContent(xmlRequest, Encoding.UTF8, "text/xml");
                httpContent.Headers.Remove("Content-Type");
                httpContent.Headers.Add("Content-Type", "text/xml; charset=utf-8");
                httpContent.Headers.Add("SOAPAction", $"\"{soapAction}\"");

                var response = await _httpClient.PostAsync(endpointAddress, httpContent);
                string xmlResponse = await response.Content.ReadAsStringAsync();

                if (!response.IsSuccessStatusCode)
                {
                    throw new InvalidOperationException($"Error HTTP: {response.StatusCode}");
                }

                double resultValue = ParseSoapResponse(xmlResponse);
                TxtResult.Text = resultValue.ToString("0.######");
                TxtStatus.Text = "Conversion exitosa.";
                TxtStatus.Foreground = new SolidColorBrush(Color.Parse("#00B894"));
            }
            catch (HttpRequestException)
            {
                ShowMessage("No se pudo conectar al servidor SOAP.\nVerifique que el servicio este activo en el puerto 8080.", "Error de Comunicacion");
                TxtStatus.Text = "Error en la conexion.";
                TxtStatus.Foreground = new SolidColorBrush(Color.Parse("#D63031"));
            }
            catch (TaskCanceledException)
            {
                ShowMessage("Tiempo de espera agotado. El servidor no responde.", "Timeout");
                TxtStatus.Text = "Tiempo de espera agotado.";
                TxtStatus.Foreground = new SolidColorBrush(Color.Parse("#E17055"));
            }
            catch (Exception ex)
            {
                ShowMessage($"Error al comunicarse con el servidor SOAP:\n\n{ex.Message}", "Error");
                TxtStatus.Text = "Error en la conversion.";
                TxtStatus.Foreground = new SolidColorBrush(Color.Parse("#D63031"));
            }
            finally
            {
                BtnConvert.IsEnabled = true;
            }
        }

        private double ParseSoapResponse(string xmlResponse)
        {
            try
            {
                var xmlDoc = new System.Xml.XmlDocument();
                xmlDoc.LoadXml(xmlResponse);

                var nsManager = new System.Xml.XmlNamespaceManager(xmlDoc.NameTable);
                nsManager.AddNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");

                var responseNode = xmlDoc.SelectSingleNode("//soap:Body/*", nsManager);

                if (responseNode == null)
                {
                    throw new InvalidOperationException("Respuesta SOAP invalida.");
                }

                var conversionNode = responseNode.FirstChild;

                if (conversionNode == null)
                {
                    throw new InvalidOperationException("Respuesta SOAP invalida.");
                }

                foreach (System.Xml.XmlNode child in conversionNode.ChildNodes)
                {
                    if (child.LocalName == "resultValue" || child.LocalName == "ResultValue")
                    {
                        if (double.TryParse(child.InnerText, out double result))
                        {
                            return result;
                        }
                    }
                }

                throw new InvalidOperationException("No se encontro el resultado.");
            }
            catch (Exception ex) when (ex is not InvalidOperationException)
            {
                throw new InvalidOperationException($"Error al procesar respuesta: {ex.Message}", ex);
            }
        }

        private void ShowMessage(string message, string title)
        {
            var dialog = new Window
            {
                Title = title,
                Width = 350,
                Height = 150,
                WindowStartupLocation = WindowStartupLocation.CenterScreen,
                Content = new StackPanel
                {
                    Margin = new Thickness(20),
                    Children =
                    {
                        new TextBlock { Text = message, TextWrapping = Avalonia.Media.TextWrapping.Wrap },
                        new Button
                        {
                            Content = "OK",
                            HorizontalAlignment = Avalonia.Layout.HorizontalAlignment.Center,
                            Margin = new Thickness(0, 15, 0, 0)
                        }
                    }
                }
            };

            if (dialog.Content is StackPanel panel && panel.Children.Count > 1 && panel.Children[1] is Button okButton)
            {
                okButton.Click += (s, args) => dialog.Close();
            }

            dialog.ShowDialog(this);
        }
    }
}
