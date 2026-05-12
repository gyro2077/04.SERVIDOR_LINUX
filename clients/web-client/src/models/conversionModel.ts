const SOAP_ENDPOINT = "/soap/04.SERVIDOR/conversion";

export type MassUnit = "KILOGRAM" | "GRAM" | "POUND" | "OUNCE";
export type LengthUnit =
  | "METER"
  | "KILOMETER"
  | "CENTIMETER"
  | "MILE"
  | "YARD"
  | "FOOT"
  | "INCH";
export type TemperatureUnit = "CELSIUS" | "FAHRENHEIT" | "KELVIN";

export interface ConversionResult {
  category: string;
  fromUnit: string;
  toUnit: string;
  inputValue: number;
  resultValue: number;
}

const buildSoapEnvelope = (
  method: string,
  namespace: string,
  params: Record<string, string | number>
): string => {
  const paramString = Object.entries(params)
    .map(([key, value]) => `<${key}>${value}</${key}>`)
    .join("");

  return `<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:${method} xmlns:ns2="${namespace}">
      ${paramString}
    </ns2:${method}>
  </soap:Body>
</soap:Envelope>`;
};

const parseXmlResponse = (xml: string): ConversionResult => {
  const parser = new DOMParser();
  const xmlDoc = parser.parseFromString(xml, "text/xml");

  if (xmlDoc.getElementsByTagName("parsererror").length) {
    throw new Error("Error parsing XML response");
  }

  const returnElement = xmlDoc.getElementsByTagName("return")[0];
  if (!returnElement) {
    throw new Error("Invalid response structure");
  }

  const getText = (tagName: string): string => {
    const element = returnElement.getElementsByTagName(tagName)[0];
    return element?.textContent || "";
  };

  return {
    category: getText("category"),
    fromUnit: getText("fromUnit"),
    toUnit: getText("toUnit"),
    inputValue: parseFloat(getText("inputValue")),
    resultValue: parseFloat(getText("resultValue")),
  };
};

export const convertMass = async (
  value: number,
  fromUnit: MassUnit,
  toUnit: MassUnit
): Promise<ConversionResult> => {
  const envelope = buildSoapEnvelope("convertMass", "http://ws.grupo3.edu.ec/", {
    value,
    fromUnit,
    toUnit,
  });

  const response = await fetch(SOAP_ENDPOINT, {
    method: "POST",
    headers: {
      "Content-Type": "text/xml",
      SOAPAction: "",
    },
    body: envelope,
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const xmlText = await response.text();
  return parseXmlResponse(xmlText);
};

export const convertLength = async (
  value: number,
  fromUnit: LengthUnit,
  toUnit: LengthUnit
): Promise<ConversionResult> => {
  const envelope = buildSoapEnvelope("convertLength", "http://ws.grupo3.edu.ec/", {
    value,
    fromUnit,
    toUnit,
  });

  const response = await fetch(SOAP_ENDPOINT, {
    method: "POST",
    headers: {
      "Content-Type": "text/xml",
      SOAPAction: "",
    },
    body: envelope,
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const xmlText = await response.text();
  return parseXmlResponse(xmlText);
};

export const convertTemperature = async (
  value: number,
  fromUnit: TemperatureUnit,
  toUnit: TemperatureUnit
): Promise<ConversionResult> => {
  const envelope = buildSoapEnvelope(
    "convertTemperature",
    "http://ws.grupo3.edu.ec/",
    {
      value,
      fromUnit,
      toUnit,
    }
  );

  const response = await fetch(SOAP_ENDPOINT, {
    method: "POST",
    headers: {
      "Content-Type": "text/xml",
      SOAPAction: "",
    },
    body: envelope,
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const xmlText = await response.text();
  return parseXmlResponse(xmlText);
};
