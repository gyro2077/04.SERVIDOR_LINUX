const SOAP_ENDPOINT = 'http://localhost:8080/ConversionService.svc';

export interface ConversionResult {
  success: boolean;
  resultValue?: number;
  errorMessage?: string;
}

export const invokeSoapConversion = async (
  category: 'Mass' | 'Length' | 'Temperature',
  value: number,
  fromUnit: string,
  toUnit: string
): Promise<ConversionResult> => {
  const methodName = `convert${category}`;

  const soapEnvelope = `<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <${methodName} xmlns="http://ws.grupo3.edu.ec/">
      <value>${value}</value>
      <fromUnit>${fromUnit}</fromUnit>
      <toUnit>${toUnit}</toUnit>
    </${methodName}>
  </soap:Body>
</soap:Envelope>`;

  try {
    const response = await fetch(SOAP_ENDPOINT, {
      method: 'POST',
      headers: {
        'Content-Type': 'text/xml; charset=utf-8',
        'SOAPAction': `"http://ws.grupo3.edu.ec/ConversionService/${methodName}"`,
      },
      body: soapEnvelope,
    });

    const responseText = await response.text();

    if (!response.ok) {
      return {
        success: false,
        errorMessage: `HTTP Error ${response.status}: El servidor rechazo la solicitud SOAP.`,
      };
    }

    const resultMatch = responseText.match(/<ResultValue>(.*?)<\/ResultValue>/);

    if (resultMatch && resultMatch[1]) {
      const parsedResult = parseFloat(resultMatch[1]);
      return { success: true, resultValue: parsedResult };
    } else {
      return {
        success: false,
        errorMessage: 'Respuesta XML inesperada. No se encontro la etiqueta <ResultValue>.',
      };
    }
  } catch (error: any) {
    return {
      success: false,
      errorMessage: `Error de red: No se pudo conectar al servidor SOAP. Verifique que la IP sea correcta y el backend este encendido.\nDetalle: ${error.message}`,
    };
  }
};