using System;
using System.Globalization;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace desktop_client.Models
{
    public class SoapServiceInvoker
    {
        private const string _sessionToken = "TU9OU1RFUjoxNzc4Njc3MDM0ODMy";
        private const string _serviceNamespace = "http://ws.grupo3.edu.ec/";

        private readonly HttpClient _httpClient;

        public SoapServiceInvoker()
        {
            _httpClient = new HttpClient { Timeout = TimeSpan.FromSeconds(30) };
        }

        public async Task<double> InvokeConversionAsync(
            string category, double value, string fromUnit, string toUnit)
        {
            string methodName = category switch
            {
                "Mass"        => "convertMass",
                "Length"      => "convertLength",
                "Temperature" => "convertTemperature",
                _             => throw new ArgumentException($"Categoría desconocida: {category}")
            };

            string endpoint    = AppConfig.ConversionEndpoint;
            string soapAction  = $"{_serviceNamespace}{methodName}";
            string valueStr = value.ToString(CultureInfo.InvariantCulture);

            string xmlRequest = $@"<?xml version=""1.0"" encoding=""UTF-8""?>
<soap:Envelope xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/""
               xmlns:con=""{_serviceNamespace}"">
  <soap:Body>
    <con:{methodName}>
      <token>{_sessionToken}</token>
      <value>{valueStr}</value>
      <fromUnit>{fromUnit}</fromUnit>
      <toUnit>{toUnit}</toUnit>
    </con:{methodName}>
  </soap:Body>
</soap:Envelope>";

            using var content = new StringContent(xmlRequest, Encoding.UTF8, "text/xml");
            content.Headers.Remove("Content-Type");
            content.Headers.Add("Content-Type", "text/xml; charset=UTF-8");
            content.Headers.Add("SOAPAction", $"\"{soapAction}\"");

            var response = await _httpClient.PostAsync(endpoint, content);
            string xmlResponse = await response.Content.ReadAsStringAsync();

            if (!response.IsSuccessStatusCode)
            {
                throw new InvalidOperationException(
                    $"Error HTTP del VPS: {(int)response.StatusCode} {response.ReasonPhrase}");
            }

            return ParseSoapResponse(xmlResponse);
        }

        private double ParseSoapResponse(string xmlResponse)
        {
            var xmlDoc = new XmlDocument();
            xmlDoc.LoadXml(xmlResponse);

            var nsManager = new XmlNamespaceManager(xmlDoc.NameTable);
            nsManager.AddNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
            nsManager.AddNamespace("S",    "http://schemas.xmlsoap.org/soap/envelope/");

            var bodyNode = xmlDoc.SelectSingleNode("//*[local-name()='Body']");
            if (bodyNode == null)
                throw new InvalidOperationException("Respuesta SOAP inválida: no se encontró el Body.");

            var responseNode = bodyNode.FirstChild;
            if (responseNode == null)
                throw new InvalidOperationException("Respuesta SOAP inválida: Body vacío.");

            var returnNode = responseNode.FirstChild;
            if (returnNode == null)
                throw new InvalidOperationException("Respuesta SOAP inválida: nodo <return> ausente.");

            foreach (XmlNode child in returnNode.ChildNodes)
            {
                if (child.LocalName.Equals("resultValue", StringComparison.OrdinalIgnoreCase))
                {
                    if (double.TryParse(
                            child.InnerText,
                            NumberStyles.Float,
                            CultureInfo.InvariantCulture,
                            out double result))
                    {
                        return result;
                    }
                    throw new InvalidOperationException(
                        $"El valor resultValue '{child.InnerText}' no es un número válido.");
                }
            }

            throw new InvalidOperationException(
                "Respuesta SOAP inesperada: no se encontró <resultValue> en el XML devuelto.");
        }
    }
}