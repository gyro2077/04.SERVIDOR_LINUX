using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using Microsoft.Extensions.Options;
using web_client.Configuration;

namespace web_client.Services
{
    public interface ISoapService
    {
        Task<SoapResponse> InvokeConversionAsync(string category, double value, string fromUnit, string toUnit);
    }

    public class SoapResponse
    {
        public bool Success { get; set; }
        public double ResultValue { get; set; }
        public string? ErrorMessage { get; set; }
    }

    public class SoapService : ISoapService
    {
        private readonly HttpClient _httpClient;
        private readonly SoapServiceConfig _config;

        public SoapService(HttpClient httpClient, IOptions<SoapServiceConfig> config)
        {
            _httpClient = httpClient;
            _config = config.Value;
        }

        public async Task<SoapResponse> InvokeConversionAsync(string category, double value, string fromUnit, string toUnit)
        {
            try
            {
                string endpointAddress = _config.EndpointAddress;
                string soapAction = $"{_config.SoapNamespace}ConversionService/convert{category}";
                string methodName = $"convert{category}";

                string xmlRequest = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <{methodName} xmlns=""{_config.SoapNamespace}"">
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
                    return new SoapResponse
                    {
                        Success = false,
                        ErrorMessage = $"Error HTTP: {response.StatusCode}"
                    };
                }

                double resultValue = ParseSoapResponse(xmlResponse);
                return new SoapResponse
                {
                    Success = true,
                    ResultValue = resultValue
                };
            }
            catch (HttpRequestException ex)
            {
                return new SoapResponse
                {
                    Success = false,
                    ErrorMessage = $"No se pudo conectar al servidor SOAP. Verifique que el servicio este activo. ({ex.Message})"
                };
            }
            catch (TaskCanceledException)
            {
                return new SoapResponse
                {
                    Success = false,
                    ErrorMessage = "Tiempo de espera agotado. El servidor no responde."
                };
            }
            catch (Exception ex)
            {
                return new SoapResponse
                {
                    Success = false,
                    ErrorMessage = $"Error al procesar la respuesta: {ex.Message}"
                };
            }
        }

        private double ParseSoapResponse(string xmlResponse)
        {
            var xmlDoc = new XmlDocument();
            xmlDoc.LoadXml(xmlResponse);

            var nsManager = new XmlNamespaceManager(xmlDoc.NameTable);
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

            foreach (XmlNode child in conversionNode.ChildNodes)
            {
                if (child.LocalName == "resultValue" || child.LocalName == "ResultValue")
                {
                    if (double.TryParse(child.InnerText, out double result))
                    {
                        return result;
                    }
                }
            }

            throw new InvalidOperationException("No se encontro el valor de resultado en la respuesta.");
        }
    }
}
