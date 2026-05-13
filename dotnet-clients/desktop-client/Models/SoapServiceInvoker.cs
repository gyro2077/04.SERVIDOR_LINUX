using System;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace desktop_client.Models
{
    public class SoapServiceInvoker
    {
        private readonly HttpClient _httpClient;

        public SoapServiceInvoker()
        {
            _httpClient = new HttpClient { Timeout = TimeSpan.FromSeconds(30) };
        }

        public async Task<double> InvokeConversionAsync(string category, double value, string fromUnit, string toUnit)
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

            using var httpContent = new StringContent(xmlRequest, Encoding.UTF8, "text/xml");
            httpContent.Headers.Remove("Content-Type");
            httpContent.Headers.Add("Content-Type", "text/xml; charset=utf-8");
            httpContent.Headers.Add("SOAPAction", $"\"{soapAction}\"");

            var response = await _httpClient.PostAsync(endpointAddress, httpContent);
            string xmlResponse = await response.Content.ReadAsStringAsync();

            if (!response.IsSuccessStatusCode)
            {
                throw new InvalidOperationException($"Error HTTP del Servidor: {response.StatusCode}");
            }

            return ParseSoapResponse(xmlResponse);
        }

        private double ParseSoapResponse(string xmlResponse)
        {
            var xmlDoc = new XmlDocument();
            xmlDoc.LoadXml(xmlResponse);

            var nsManager = new XmlNamespaceManager(xmlDoc.NameTable);
            nsManager.AddNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");

            var responseNode = xmlDoc.SelectSingleNode("//soap:Body/*", nsManager);
            if (responseNode == null) throw new InvalidOperationException("Respuesta SOAP inválida: Cuerpo no encontrado.");

            var conversionNode = responseNode.FirstChild;
            if (conversionNode == null) throw new InvalidOperationException("Respuesta SOAP inválida: Nodo de resultado ausente.");

            foreach (XmlNode child in conversionNode.ChildNodes)
            {
                if (child.LocalName.Equals("resultValue", StringComparison.OrdinalIgnoreCase))
                {
                    if (double.TryParse(child.InnerText, out double result))
                    {
                        return result;
                    }
                }
            }

            throw new InvalidOperationException("No se encontró el valor de resultado en el XML devuelto.");
        }
    }
}