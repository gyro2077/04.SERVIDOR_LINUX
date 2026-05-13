using System.Globalization;
using System.Net.Http;
using System.Text;
using System.Xml;

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
        private const string VPS_ENDPOINT = "http://209.145.48.25:8081/ROOT/Conversion";
        private const string SOAP_NAMESPACE = "http://ws.grupo3.edu.ec/";
        private const string SESSION_TOKEN = "TU9OU1RFUjoxNzc4Njc3MDM0ODMy";

        private readonly HttpClient _httpClient;

        public SoapService(HttpClient httpClient)
        {
            _httpClient = httpClient;
            _httpClient.Timeout = TimeSpan.FromSeconds(30);
        }

        public async Task<SoapResponse> InvokeConversionAsync(string category, double value, string fromUnit, string toUnit)
        {
            try
            {
                string operation = $"convert{category}";
                string valueStr = value.ToString(CultureInfo.InvariantCulture);

                string soapBody = $@"<?xml version=""1.0"" encoding=""UTF-8""?>
<soap:Envelope xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"" xmlns:con=""{SOAP_NAMESPACE}"">
   <soap:Body>
      <con:{operation}>
         <token>{SESSION_TOKEN}</token>
         <value>{valueStr}</value>
         <fromUnit>{fromUnit}</fromUnit>
         <toUnit>{toUnit}</toUnit>
      </con:{operation}>
   </soap:Body>
</soap:Envelope>";

                var content = new StringContent(soapBody, Encoding.UTF8, "text/xml");
                content.Headers.Remove("Content-Type");
                content.Headers.TryAddWithoutValidation("Content-Type", "text/xml; charset=UTF-8");

                var response = await _httpClient.PostAsync(VPS_ENDPOINT, content);
                string xmlResponse = await response.Content.ReadAsStringAsync();

                if (!response.IsSuccessStatusCode)
                {
                    return new SoapResponse
                    {
                        Success = false,
                        ErrorMessage = $"El servidor devolvió HTTP {(int)response.StatusCode}. Respuesta: {xmlResponse}"
                    };
                }

                double result = ParseResultValue(xmlResponse);
                return new SoapResponse { Success = true, ResultValue = result };
            }
            catch (HttpRequestException ex)
            {
                return new SoapResponse { Success = false, ErrorMessage = $"Sin conexión al servidor VPS. Verifique la red. ({ex.Message})" };
            }
            catch (TaskCanceledException)
            {
                return new SoapResponse { Success = false, ErrorMessage = "Tiempo de espera agotado (30 s). El servidor no responde." };
            }
            catch (InvalidOperationException ex)
            {
                return new SoapResponse { Success = false, ErrorMessage = $"Respuesta SOAP inesperada: {ex.Message}" };
            }
            catch (Exception ex)
            {
                return new SoapResponse { Success = false, ErrorMessage = $"Error interno: {ex.Message}" };
            }
        }

        private static double ParseResultValue(string xmlResponse)
        {
            var doc = new XmlDocument();
            doc.LoadXml(xmlResponse);

            XmlNode? body = FindNodeByLocalName(doc.DocumentElement!, "Body");
            if (body == null)
                throw new InvalidOperationException("No se encontró <Body> en la respuesta.");

            XmlNode? responseNode = body.FirstChild;
            if (responseNode == null)
                throw new InvalidOperationException("La respuesta SOAP está vacía.");

            if (responseNode.LocalName == "Fault")
            {
                string faultMsg = responseNode.InnerText;
                throw new InvalidOperationException($"SOAP Fault: {faultMsg}");
            }

            XmlNode? returnNode = FindNodeByLocalName(responseNode, "return");
            if (returnNode == null)
                throw new InvalidOperationException("No se encontró <return> en la respuesta.");

            XmlNode? resultNode = FindNodeByLocalName(returnNode, "resultValue");
            if (resultNode == null)
                throw new InvalidOperationException("No se encontró <resultValue> en la respuesta.");

            if (!double.TryParse(resultNode.InnerText, NumberStyles.Any, CultureInfo.InvariantCulture, out double result))
            {
                throw new InvalidOperationException($"El valor '{resultNode.InnerText}' no es un número válido.");
            }

            return result;
        }

        private static XmlNode? FindNodeByLocalName(XmlNode parent, string localName)
        {
            foreach (XmlNode child in parent.ChildNodes)
            {
                if (child.LocalName == localName)
                    return child;
            }
            return null;
        }
    }
}