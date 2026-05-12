using System;
using System.IO;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using System.Xml.Serialization;

namespace ConsoleClient
{
    public class ServiceInvoker : IDisposable
    {
        private readonly string _endpointAddress;

        public ServiceInvoker()
        {
            _endpointAddress = "http://localhost:8080/ConversionService.svc";
        }

        public async Task<ConversionResponseView> InvokeConversionAsync(string category, double value, string fromUnit, string toUnit)
        {
            try
            {
                using var client = new HttpClient();
                client.Timeout = TimeSpan.FromSeconds(30);

                string soapAction = category switch
                {
                    "Mass" => "http://ws.grupo3.edu.ec/ConversionService/convertMass",
                    "Length" => "http://ws.grupo3.edu.ec/ConversionService/convertLength",
                    "Temperature" => "http://ws.grupo3.edu.ec/ConversionService/convertTemperature",
                    _ => throw new ArgumentException("Categoria desconocida: " + category)
                };

                string methodName = $"convert{category}" + (category != "Mass" ? category : "");
                methodName = category switch
                {
                    "Mass" => "convertMass",
                    "Length" => "convertLength",
                    "Temperature" => "convertTemperature",
                    _ => methodName
                };

                string xmlRequest = BuildSoapRequest(methodName, value, fromUnit, toUnit);

                var httpContent = new StringContent(xmlRequest, Encoding.UTF8, "text/xml");
                httpContent.Headers.Remove("Content-Type");
                httpContent.Headers.Add("Content-Type", "text/xml; charset=utf-8");
                httpContent.Headers.Add("SOAPAction", $"\"{soapAction}\"");

                var response = await client.PostAsync(_endpointAddress, httpContent);
                string xmlResponse = await response.Content.ReadAsStringAsync();

                if (!response.IsSuccessStatusCode)
                {
                    throw new InvalidOperationException($"Error HTTP: {response.StatusCode}");
                }

                return ParseSoapResponse(xmlResponse);
            }
            catch (HttpRequestException ex)
            {
                throw new InvalidOperationException("No se pudo conectar al servicio. Verifique que el servidor este activo.", ex);
            }
            catch (TaskCanceledException)
            {
                throw new InvalidOperationException("Tiempo de espera agotado. El servidor no responde.");
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
            try
            {
                var xmlDoc = new XmlDocument();
                xmlDoc.LoadXml(xmlResponse);

                XmlNamespaceManager nsManager = new XmlNamespaceManager(xmlDoc.NameTable);
                nsManager.AddNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
                nsManager.AddNamespace("ns", "http://ws.grupo3.edu.ec/");

                XmlNode? responseNode = xmlDoc.SelectSingleNode("//soap:Body/*", nsManager);

                if (responseNode == null)
                {
                    throw new InvalidOperationException("Respuesta SOAP invalida: no se encontro el cuerpo.");
                }

                XmlNode? conversionNode = responseNode.FirstChild;

                if (conversionNode == null)
                {
                    throw new InvalidOperationException("Respuesta SOAP invalida: estructura inesperada.");
                }

                var result = new ConversionResponseView();

                foreach (XmlNode child in conversionNode.ChildNodes)
                {
                    switch (child.LocalName)
                    {
                        case "category":
                        case "Category":
                            result.Category = child.InnerText;
                            break;
                        case "fromUnit":
                        case "FromUnit":
                            result.FromUnit = child.InnerText;
                            break;
                        case "toUnit":
                        case "ToUnit":
                            result.ToUnit = child.InnerText;
                            break;
                        case "inputValue":
                        case "InputValue":
                            if (double.TryParse(child.InnerText, out double inputVal))
                                result.InputValue = inputVal;
                            break;
                        case "resultValue":
                        case "ResultValue":
                            if (double.TryParse(child.InnerText, out double resultVal))
                                result.ResultValue = resultVal;
                            break;
                    }
                }

                if (string.IsNullOrEmpty(result.Category))
                {
                    throw new InvalidOperationException("Respuesta SOAP invalida: datos incompletos.");
                }

                return result;
            }
            catch (InvalidOperationException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException($"Error al procesar respuesta: {ex.Message}", ex);
            }
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
        }
    }

    public class ConversionResponseView
    {
        public string Category { get; set; } = "";
        public string FromUnit { get; set; } = "";
        public string ToUnit { get; set; } = "";
        public double InputValue { get; set; }
        public double ResultValue { get; set; }
    }
}
