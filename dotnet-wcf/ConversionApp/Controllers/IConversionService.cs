using CoreWCF;
using ConversionApp.Views;

namespace ConversionApp.Controllers
{
    [ServiceContract(Name = "ConversionService", Namespace = "http://ws.grupo3.edu.ec/")]
    public interface IConversionService
    {
        [OperationContract(Name = "convertMass")]
        ConversionResponseView ConvertMass(double value, string fromUnit, string toUnit);

        [OperationContract(Name = "convertLength")]
        ConversionResponseView ConvertLength(double value, string fromUnit, string toUnit);

        [OperationContract(Name = "convertTemperature")]
        ConversionResponseView ConvertTemperature(double value, string fromUnit, string toUnit);
    }
}
