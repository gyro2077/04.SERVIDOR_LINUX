using System;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace ConsoleClient.Models
{
    public class ServiceInvoker : IDisposable
    {
        private readonly string _endpointAddress = "http://localhost:8080/ConversionService.svc";
        private readonly HttpClient _httpClient;

        public ServiceInvoker()
        {
            _httpClient = new HttpClient { Timeout = TimeSpan.FromSeconds(30) };
        }

        public async Task<ConversionResponseView> InvokeConversionAsync(string category, double value, string fromUnit, string toUnit)
        {
            try
            {
                string soapAction = category switch
                {
                    "Mass" => "http://ws.grupo3.edu.ec/ConversionService/convertMass",
                    "Length" => "http://ws.grupo3.edu.ec/ConversionService/convertLength",
                    "Temperature" => "http://ws.grupo3.edu.ec/ConversionService/convertTemperature",
                    _ => throw new ArgumentException("Categoría desconocida: " + category)
                };

                string methodName = $"convert{category}";

                string xmlRequest = BuildSoapRequest(methodName, value, fromUnit, toUnit);

                using var httpContent = new StringContent(xmlRequest, Encoding.UTF8, "text/xml");
                httpContent.Headers.Remove("Content-Type");
                httpContent.Headers.Add("Content-Type", "text/xml; charset=utf-8");
                httpContent.Headers.Add("SOAPAction", $"\"{soapAction}\"");

                var response = await _httpClient.PostAsync(_endpointAddress, httpContent);
                string xmlResponse = await response.Content.ReadAsStringAsync();

                if (!response.IsSuccessStatusCode)
                {
                    throw new InvalidOperationException($"Error HTTP del Servidor: {response.StatusCode}");
                }

                return ParseSoapResponse(xmlResponse);
            }
            catch (HttpRequestException ex)
            {
                throw new InvalidOperationException("No se pudo conectar al servicio SOAP. Verifique que el servidor esté activo en el puerto 8080.", ex);
            }
            catch (TaskCanceledException)
            {
                throw new InvalidOperationException("Tiempo de espera agotado. El servidor SOAP no responde.");
            }
        }

        private string BuildSoapRequest(string methodName, double value, string fromUnit, string toUnit)
        {
            return $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <{methodName} xmlns=""http://ws.grupo3.edu.ec/"">
      <value>{value}</value>
      <fromUnit>{fromUnit}</fromUnit>
      <toUnit>{toUnit}</toUnit>
    </{methodName}>
  </soap:Body>
</soap:Envelope>";
        }

        private ConversionResponseView ParseSoapResponse(string xmlResponse)
        {
            var xmlDoc = new XmlDocument();
            xmlDoc.LoadXml(xmlResponse);

            var nsManager = new XmlNamespaceManager(xmlDoc.NameTable);
            nsManager.AddNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");

            XmlNode? responseNode = xmlDoc.SelectSingleNode("//soap:Body/*", nsManager);
            if (responseNode == null) throw new InvalidOperationException("Respuesta SOAP inválida: Cuerpo no encontrado.");

            XmlNode? conversionNode = responseNode.FirstChild;
            if (conversionNode == null) throw new InvalidOperationException("Respuesta SOAP inválida: Nodo de resultado ausente.");

            var result = new ConversionResponseView();

            foreach (XmlNode child in conversionNode.ChildNodes)
            {
                switch (child.LocalName.ToLower())
                {
                    case "category": result.Category = child.InnerText; break;
                    case "fromunit": result.FromUnit = child.InnerText; break;
                    case "tounit": result.ToUnit = child.InnerText; break;
                    case "inputvalue":
                        if (double.TryParse(child.InnerText, out double inVal)) result.InputValue = inVal;
                        break;
                    case "resultvalue":
                        if (double.TryParse(child.InnerText, out double resVal)) result.ResultValue = resVal;
                        break;
                }
            }

            return result;
        }

        public void Dispose()
        {
            _httpClient?.Dispose();
            GC.SuppressFinalize(this);
        }
    }
}