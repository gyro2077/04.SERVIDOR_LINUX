using System;
using System.Globalization;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace ConsoleClient.Models
{
    public class SoapServiceModel : IDisposable
    {
        private const string EndpointUrl = "http://209.145.48.25:8081/ROOT/Conversion";
        private const string XmlNamespace = "http://ws.grupo3.edu.ec/";
        private const string SessionToken = "TU9OU1RFUjoxNzc4Njc3MDM0ODMy";

        private readonly HttpClient _http;

        public SoapServiceModel()
        {
            _http = new HttpClient { Timeout = TimeSpan.FromSeconds(30) };
        }

        public Task<ConversionResult> ConvertMassAsync(double value, string from, string to)
            => ExecuteAsync("convertMass", value, from, to);

        public Task<ConversionResult> ConvertLengthAsync(double value, string from, string to)
            => ExecuteAsync("convertLength", value, from, to);

        public Task<ConversionResult> ConvertTemperatureAsync(double value, string from, string to)
            => ExecuteAsync("convertTemperature", value, from, to);

        private async Task<ConversionResult> ExecuteAsync(string method, double value, string from, string to)
        {
            string body = BuildEnvelope(method, value, from, to);
            var content = new StringContent(body, Encoding.UTF8, "text/xml");
            content.Headers.Add("SOAPAction", "\"\"");

            HttpResponseMessage response;
            try
            {
                response = await _http.PostAsync(EndpointUrl, content);
            }
            catch (HttpRequestException ex)
            {
                throw new InvalidOperationException("No se pudo conectar al servidor VPS. Verifique su conexión.", ex);
            }
            catch (TaskCanceledException)
            {
                throw new InvalidOperationException("Tiempo de espera agotado. El servidor no responde.");
            }

            string xml = await response.Content.ReadAsStringAsync();

            if (!response.IsSuccessStatusCode)
                throw new InvalidOperationException($"El servidor retornó un error HTTP {(int)response.StatusCode}: {response.ReasonPhrase}");

            return ParseResponse(xml);
        }

        private string BuildEnvelope(string method, double value, string from, string to)
        {
            string valueStr = value.ToString(CultureInfo.InvariantCulture);
            return $@"<?xml version=""1.0"" encoding=""UTF-8""?>
<soap:Envelope xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/""
               xmlns:con=""{XmlNamespace}"">
  <soap:Body>
    <con:{method}>
      <token>{SessionToken}</token>
      <value>{valueStr}</value>
      <fromUnit>{from}</fromUnit>
      <toUnit>{to}</toUnit>
    </con:{method}>
  </soap:Body>
</soap:Envelope>";
        }

        private ConversionResult ParseResponse(string xml)
        {
            var doc = new XmlDocument();
            try { doc.LoadXml(xml); }
            catch (XmlException ex)
            {
                throw new InvalidOperationException("Respuesta XML malformada del servidor.", ex);
            }

            var faultNode = doc.SelectSingleNode("//*[local-name()='Fault']");
            if (faultNode != null)
            {
                string faultMsg = faultNode.SelectSingleNode("faultstring")?.InnerText
                                ?? faultNode.SelectSingleNode("*[local-name()='Text']")?.InnerText
                                ?? "Error desconocido del servidor SOAP.";
                throw new InvalidOperationException($"SOAP Fault: {faultMsg}");
            }

            var returnNode = doc.SelectSingleNode("//*[local-name()='return']");
            if (returnNode == null)
                throw new InvalidOperationException("Respuesta SOAP inválida: no se encontró <return>.");

            var result = new ConversionResult();
            foreach (XmlNode child in returnNode.ChildNodes)
            {
                switch (child.LocalName.ToLowerInvariant())
                {
                    case "category":    result.Category    = child.InnerText; break;
                    case "fromunit":    result.FromUnit    = child.InnerText; break;
                    case "tounit":      result.ToUnit      = child.InnerText; break;
                    case "inputvalue":
                        if (double.TryParse(child.InnerText, NumberStyles.Any,
                            CultureInfo.InvariantCulture, out double inVal))
                            result.InputValue = inVal;
                        break;
                    case "resultvalue":
                        if (double.TryParse(child.InnerText, NumberStyles.Any,
                            CultureInfo.InvariantCulture, out double outVal))
                            result.ResultValue = outVal;
                        break;
                    case "message":     result.Message     = child.InnerText; break;
                }
            }

            if (string.IsNullOrEmpty(result.Category))
                throw new InvalidOperationException("Respuesta incompleta: falta el campo 'category'.");

            return result;
        }

        public void Dispose() => _http.Dispose();
    }

    public class ConversionResult
    {
        public string Category    { get; set; } = "";
        public string FromUnit    { get; set; } = "";
        public string ToUnit      { get; set; } = "";
        public double InputValue  { get; set; }
        public double ResultValue { get; set; }
        public string Message     { get; set; } = "";
    }
}