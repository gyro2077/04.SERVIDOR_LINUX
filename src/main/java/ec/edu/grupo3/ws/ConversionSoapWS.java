package ec.edu.grupo3.ws;

import ec.edu.grupo3.model.LengthConverterModel;
import ec.edu.grupo3.model.LengthUnit;
import ec.edu.grupo3.model.MassConverterModel;
import ec.edu.grupo3.model.MassUnit;
import ec.edu.grupo3.model.TemperatureConverterModel;
import ec.edu.grupo3.model.TemperatureUnit;
import ec.edu.grupo3.view.ConversionResponseView;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(serviceName = "ConversionService")
public class ConversionSoapWS {

    private final MassConverterModel massConverterModel = new MassConverterModel();
    private final LengthConverterModel lengthConverterModel = new LengthConverterModel();
    private final TemperatureConverterModel temperatureConverterModel = new TemperatureConverterModel();

    @WebMethod(operationName = "convertMass")
    public ConversionResponseView convertMass(
        @WebParam(name = "value") double value,
        @WebParam(name = "fromUnit") String fromUnit,
        @WebParam(name = "toUnit") String toUnit
    ) {
        MassUnit from = MassUnit.valueOf(fromUnit.toUpperCase());
        MassUnit to = MassUnit.valueOf(toUnit.toUpperCase());
        double result = massConverterModel.convert(value, from, to);
        return new ConversionResponseView("MASS", from.name(), to.name(), value, result);
    }

    @WebMethod(operationName = "convertLength")
    public ConversionResponseView convertLength(
        @WebParam(name = "value") double value,
        @WebParam(name = "fromUnit") String fromUnit,
        @WebParam(name = "toUnit") String toUnit
    ) {
        LengthUnit from = LengthUnit.valueOf(fromUnit.toUpperCase());
        LengthUnit to = LengthUnit.valueOf(toUnit.toUpperCase());
        double result = lengthConverterModel.convert(value, from, to);
        return new ConversionResponseView("LENGTH", from.name(), to.name(), value, result);
    }

    @WebMethod(operationName = "convertTemperature")
    public ConversionResponseView convertTemperature(
        @WebParam(name = "value") double value,
        @WebParam(name = "fromUnit") String fromUnit,
        @WebParam(name = "toUnit") String toUnit
    ) {
        TemperatureUnit from = TemperatureUnit.valueOf(fromUnit.toUpperCase());
        TemperatureUnit to = TemperatureUnit.valueOf(toUnit.toUpperCase());
        double result = temperatureConverterModel.convert(value, from, to);
        return new ConversionResponseView("TEMPERATURE", from.name(), to.name(), value, result);
    }
}
