# Conversión SOAP de Java a .NET (CoreWCF)

## Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
3. [Estructura de Archivos](#estructura-de-archivos)
4. [Explicación de Componentes](#explicación-de-componentes)
5. [Configuración y Ejecución](#configuración-y-ejecución)
6. [Uso del Servicio SOAP](#uso-del-servicio-soap)
7. [Referencia de Unidades](#referencia-de-unidades)
8. [Ejemplos de Solicitudes XML](#ejemplos-de-solicitudes-xml)
9. [Solución de Problemas](#solución-de-problemas)

---

## Descripción General

Este proyecto es una **migración** del servicio SOAP original escrito en Java (Jakarta EE) hacia **.NET 10** utilizando **CoreWCF** (una implementación moderna y multiplataforma de WCF para .NET Core/.NET 5+).

El servicio proporciona un endpoint SOAP que permite realizar conversiones de unidades en tres categorías:
- **Masa** (kilogramos, gramos, libras, onzas)
- **Longitud** (metros, kilómetros, centímetros, millas, yardas, pies, pulgadas)
- **Temperatura** (Celsius, Fahrenheit, Kelvin)

### ¿Qué es SOAP?

SOAP (Simple Object Access Protocol) es un protocolo estándar para intercambiar datos entre sistemas a través de HTTP usando XML. A diferencia de las APIs REST modernas que usan JSON, SOAP usa exclusivamente XML.

### ¿Qué es CoreWCF?

CoreWCF es una biblioteca que permite crear servicios SOAP compatibles con WCF (Windows Communication Foundation) en .NET moderno (Core, 5, 6, 7, 8, 9, 10). Originalmente WCF solo funcionaba en Windows con .NET Framework, pero CoreWCF trae esa funcionalidad a cualquier sistema operativo.

---

## Arquitectura del Proyecto

El proyecto sigue una arquitectura inspirada en el patrón **MVC (Modelo-Vista-Controlador)**, adaptada para servicios SOAP:

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENTE SOAP                             │
│              (Envía peticiones XML via HTTP)                     │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     CONTROLLERS                                  │
│  IConversionService.cs (Interfaz/Contrato)                        │
│  ConversionService.svc.cs (Implementación del servicio)          │
│                                                                 │
│  Responsabilidad: Recibir peticiones SOAP y devolver respuestas  │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        MODELS                                   │
│  • Enums (LengthUnit, MassUnit, TemperatureUnit)                 │
│  • ConverterModels (lógica de conversión pura)                   │
│                                                                 │
│  Responsabilidad: Toda la lógica de negocio/matemática          │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         VIEWS                                   │
│  ConversionResponseView.cs                                       │
│                                                                 │
│  Responsabilidad: Definir cómo se estructura la respuesta XML     │
└─────────────────────────────────────────────────────────────────┘
```

### Analogía con MVC tradicional

| Componente MVC Tradicional | Equivalente en este proyecto |
|---------------------------|----------------------------|
| **Model** | `Models/*.cs` - Lógica pura de conversión |
| **View** | `Views/ConversionResponseView.cs` - Estructura de respuesta |
| **Controller** | `Controllers/ConversionService.svc.cs` - Punto de entrada del servicio |

---

## Estructura de Archivos

```
dotnet-wcf/
├── ConversionApp/                          # Proyecto principal
│   ├── Controllers/                        # Controladores (Servicio SOAP)
│   │   ├── IConversionService.cs           # Interfaz del servicio (contrato)
│   │   ├── ConversionService.svc           # Archivo descriptor del servicio
│   │   └── ConversionService.svc.cs        # Implementación del servicio
│   ├── Models/                             # Modelos (Lógica de negocio)
│   │   ├── LengthUnit.cs                  # Enum: unidades de longitud
│   │   ├── MassUnit.cs                   # Enum: unidades de masa
│   │   ├── TemperatureUnit.cs             # Enum: unidades de temperatura
│   │   ├── LengthConverterModel.cs        # Lógica de conversión de longitud
│   │   ├── MassConverterModel.cs          # Lógica de conversión de masa
│   │   └── TemperatureConverterModel.cs    # Lógica de conversión de temperatura
│   ├── Views/                             # Vistas (DTOs/Contratos de datos)
│   │   └── ConversionResponseView.cs       # Estructura de respuesta SOAP
│   ├── Program.cs                         # Punto de entrada y configuración
│   └── ConversionApp.csproj               # Archivo de proyecto .NET
├── ConversionApp.Tests/                    # Pruebas unitarias
│   ├── ConversionTests.cs                  # Pruebas de conversión
│   └── ConversionApp.Tests.csproj        # Archivo de proyecto de pruebas
└── README.md                             # Este archivo
```

---

## Explicación de Componentes

### 1. Models (Enumeraciones)

Las enumeraciones definen los tipos de unidades disponibles:

**LengthUnit.cs** - Unidades de longitud:
```csharp
namespace ConversionApp.Models
{
    public enum LengthUnit
    {
        METER,        // Metro
        KILOMETER,    // Kilómetro
        CENTIMETER,   // Centímetro
        MILE,         // Milla
        YARD,         // Yarda
        FOOT,         // Pie
        INCH          // Pulgada
    }
}
```

**MassUnit.cs** - Unidades de masa:
```csharp
namespace ConversionApp.Models
{
    public enum MassUnit
    {
        KILOGRAM,     // Kilogramo
        GRAM,         // Gramo
        POUND,        // Libra
        OUNCE         // Onza
    }
}
```

**TemperatureUnit.cs** - Unidades de temperatura:
```csharp
namespace ConversionApp.Models
{
    public enum TemperatureUnit
    {
        CELSIUS,      // Celsius (Centígrados)
        FAHRENHEIT,   // Fahrenheit
        KELVIN        // Kelvin
    }
}
```

### 2. Models (Lógica de Conversión)

Estos archivos contienen la lógica matemática pura de conversión:

**MassConverterModel.cs** - Convierte entre unidades de masa:
- Convierte todo a kilogramos primero
- Luego convierte de kilogramos a la unidad destino
- Factores de conversión estándar internacionales

**LengthConverterModel.cs** - Convierte entre unidades de longitud:
- Convierte todo a metros primero
- Luego convierte de metros a la unidad destino
- Usa factores de conversión exactos

**TemperatureConverterModel.cs** - Convierte entre unidades de temperatura:
- Las conversiones de temperatura no son lineales (son fórmulas especiales)
- Convierte a Celsius como paso intermedio
- Aplica las fórmulas apropiadas para cada conversión

### 3. Views (Contrato de Respuesta)

**ConversionResponseView.cs** - Define la estructura XML de respuesta:

```csharp
[DataContract(Name = "conversionResponse", Namespace = "")]
public class ConversionResponseView
{
    [DataMember(Order = 1)]
    public string Category { get; set; }      // Categoría: MASS, LENGTH, TEMPERATURE

    [DataMember(Order = 2)]
    public string FromUnit { get; set; }      // Unidad origen

    [DataMember(Order = 3)]
    public string ToUnit { get; set; }        // Unidad destino

    [DataMember(Order = 4)]
    public double InputValue { get; set; }    // Valor de entrada

    [DataMember(Order = 5)]
    public double ResultValue { get; set; }   // Resultado de la conversión
}
```

Los atributos `DataContract` y `DataMember` le dicen a WCF cómo serializar esta clase a XML.

### 4. Controllers (Servicio SOAP)

**IConversionService.cs** - Define el contrato del servicio:

```csharp
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
```

**ConversionService.svc.cs** - Implementa la lógica del servicio:

```csharp
public class ConversionService : IConversionService
{
    private readonly MassConverterModel _massConverter = new MassConverterModel();
    private readonly LengthConverterModel _lengthConverter = new LengthConverterModel();
    private readonly TemperatureConverterModel _temperatureConverter = new TemperatureConverterModel();

    public ConversionResponseView ConvertMass(double value, string fromUnit, string toUnit)
    {
        // Convierte el string de unidad al enum correspondiente
        var from = (MassUnit)Enum.Parse(typeof(MassUnit), fromUnit, true);
        var to = (MassUnit)Enum.Parse(typeof(MassUnit), toUnit, true);

        // Realiza la conversión usando el modelo
        double result = _massConverter.Convert(value, from, to);

        // Devuelve la respuesta formateada
        return new ConversionResponseView("MASS", from.ToString(), to.ToString(), value, result);
    }

    // Los métodos ConvertLength y ConvertTemperature siguen el mismo patrón
}
```

### 5. Program.cs - Configuración del Host

Este archivo configura cómo se ejecuta el servicio:

```csharp
// Configura el host web con Kestrel (servidor HTTP de .NET)
builder.WebHost.UseKestrel(options =>
{
    options.ListenAnyIP(8080);  // Escucha en todas las IPs, puerto 8080
});

// Registra los servicios de CoreWCF
builder.Services.AddServiceModelServices().AddServiceModelMetadata();

// Configura el servicio SOAP
app.UseServiceModel(builder =>
{
    builder.AddService<ConversionApp.Controllers.ConversionService>()
        .AddServiceEndpoint<ConversionApp.Controllers.IConversionService>(
            new BasicHttpBinding(),           // Binding básico SOAP
            "/ConversionService.svc");        // URL del servicio
});

// Habilita el WSDL (metadatos del servicio)
var serviceMetadataBehavior = app.Services.GetRequiredService<ServiceMetadataBehavior>();
serviceMetadataBehavior.HttpGetEnabled = true;

app.Run();
```

---

## Configuración y Ejecución

### Requisitos Previos

- **.NET SDK 10.0** o superior instalado
- Descargable desde: https://dotnet.microsoft.com/download/dotnet

### Compilación

```bash
# Navegar al directorio del proyecto
cd dotnet-wcf/ConversionApp

# Compilar el proyecto
dotnet build
```

### Ejecución

```bash
# Ejecutar el servicio
dotnet run

# El servicio estará disponible en:
# http://localhost:8080/ConversionService.svc
```

### Verificación del WSDL

```bash
# Obtener la descripción del servicio (WSDL)
curl 'http://localhost:8080/ConversionService.svc?wsdl'
```

### Ejecutar Pruebas Unitarias

```bash
# Desde la raíz del proyecto
cd dotnet-wcf

# Compilar y ejecutar pruebas
dotnet test ConversionApp.Tests/ConversionApp.Tests.csproj
```

---

## Uso del Servicio SOAP

### Endpoint

| Elemento | Valor |
|----------|-------|
| **URL** | `http://localhost:8080/ConversionService.svc` |
| **Protocolo** | SOAP 1.1 sobre HTTP |
| **Encoding** | UTF-8 |

### Estructura de una Solicitud SOAP

Toda solicitud SOAP sigue este formato:

```xml
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <!-- Aquí va la operación -->
  </soap:Body>
</soap:Envelope>
```

### Estructura de Respuesta

```xml
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <conversionResponse xmlns="http://ws.grupo3.edu.ec/">
      <Category>MASS</Category>
      <FromUnit>KILOGRAM</FromUnit>
      <ToUnit>POUND</ToUnit>
      <InputValue>1</InputValue>
      <ResultValue>2.20462262184878</ResultValue>
    </conversionResponse>
  </soap:Body>
</soap:Envelope>
```

---

## Referencia de Unidades

### Unidades de Masa

| Unidad | Símbolo | Equivalencia |
|--------|---------|--------------|
| KILOGRAM | kg | Unidad base |
| GRAM | g | 1 kg = 1000 g |
| POUND | lb | 1 kg = 2.20462 lb |
| OUNCE | oz | 1 kg = 35.274 oz |

### Unidades de Longitud

| Unidad | Símbolo | Equivalencia |
|--------|---------|--------------|
| METER | m | Unidad base |
| KILOMETER | km | 1 km = 1000 m |
| CENTIMETER | cm | 1 m = 100 cm |
| MILE | mi | 1 mi = 1609.344 m |
| YARD | yd | 1 m = 1.09361 yd |
| FOOT | ft | 1 m = 3.28084 ft |
| INCH | in | 1 m = 39.3701 in |

### Unidades de Temperatura

| Unidad | Símbolo | Notas |
|--------|---------|-------|
| CELSIUS | °C | Unidad base (punto de congelación del agua = 0°C) |
| FAHRENHEIT | °F | Punto de congelación del agua = 32°F |
| KELVIN | K | Cero absoluto = 0 K (-273.15°C) |

---

## Ejemplos de Solicitudes XML

### 1. Conversión de Masa (Kilogramo a Libra)

**Solicitud:**
```bash
curl -X POST 'http://localhost:8080/ConversionService.svc' \
  -H 'Content-Type: text/xml; charset=utf-8' \
  -H 'SOAPAction: "http://ws.grupo3.edu.ec/ConversionService/convertMass"' \
  -d '<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <convertMass xmlns="http://ws.grupo3.edu.ec/">
      <value>1</value>
      <fromUnit>KILOGRAM</fromUnit>
      <toUnit>POUND</toUnit>
    </convertMass>
  </soap:Body>
</soap:Envelope>'
```

**Respuesta esperada:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <convertMassResponse xmlns="http://ws.grupo3.edu.ec/">
      <ConversionResponseView>
        <Category>MASS</Category>
        <FromUnit>KILOGRAM</FromUnit>
        <ToUnit>POUND</ToUnit>
        <InputValue>1</InputValue>
        <ResultValue>2.20462262184878</ResultValue>
      </ConversionResponseView>
    </convertMassResponse>
  </soap:Body>
</soap:Envelope>
```

### 2. Conversión de Longitud (Metro a Pie)

**Solicitud:**
```bash
curl -X POST 'http://localhost:8080/ConversionService.svc' \
  -H 'Content-Type: text/xml; charset=utf-8' \
  -H 'SOAPAction: "http://ws.grupo3.edu.ec/ConversionService/convertLength"' \
  -d '<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <convertLength xmlns="http://ws.grupo3.edu.ec/">
      <value>1</value>
      <fromUnit>METER</fromUnit>
      <toUnit>FOOT</toUnit>
    </convertLength>
  </soap:Body>
</soap:Envelope>'
```

**Respuesta esperada:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <convertLengthResponse xmlns="http://ws.grupo3.edu.ec/">
      <ConversionResponseView>
        <Category>LENGTH</Category>
        <FromUnit>METER</FromUnit>
        <ToUnit>FOOT</ToUnit>
        <InputValue>1</InputValue>
        <ResultValue>3.28084</ResultValue>
      </ConversionResponseView>
    </convertLengthResponse>
  </soap:Body>
</soap:Envelope>
```

### 3. Conversión de Temperatura (Celsius a Fahrenheit)

**Solicitud:**
```bash
curl -X POST 'http://localhost:8080/ConversionService.svc' \
  -H 'Content-Type: text/xml; charset=utf-8' \
  -H 'SOAPAction: "http://ws.grupo3.edu.ec/ConversionService/convertTemperature"' \
  -d '<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <convertTemperature xmlns="http://ws.grupo3.edu.ec/">
      <value>100</value>
      <fromUnit>CELSIUS</fromUnit>
      <toUnit>FAHRENHEIT</toUnit>
    </convertTemperature>
  </soap:Body>
</soap:Envelope>'
```

**Respuesta esperada:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <convertTemperatureResponse xmlns="http://ws.grupo3.edu.ec/">
      <ConversionResponseView>
        <Category>TEMPERATURE</Category>
        <FromUnit>CELSIUS</FromUnit>
        <ToUnit>FAHRENHEIT</ToUnit>
        <InputValue>100</InputValue>
        <ResultValue>212</ResultValue>
      </ConversionResponseView>
    </convertTemperatureResponse>
  </soap:Body>
</soap:Envelope>
```

---

## Ejemplos Adicionales de Conversiones

### Conversiones de Masa

| Operación | value | fromUnit | toUnit | Resultado |
|-----------|-------|----------|--------|-----------|
| Kilogramo → Libra | 1 | KILOGRAM | POUND | 2.2046 |
| Libra → Kilogramo | 1 | POUND | KILOGRAM | 0.4536 |
| Gramo → Onza | 100 | GRAM | OUNCE | 3.5274 |
| Kilogramo → Gramo | 2 | KILOGRAM | GRAM | 2000 |

### Conversiones de Longitud

| Operación | value | fromUnit | toUnit | Resultado |
|-----------|-------|----------|--------|-----------|
| Metro → Pie | 1 | METER | FOOT | 3.2808 |
| Kilómetro → Milla | 1 | KILOMETER | MILE | 0.6214 |
| Pulgada → Centímetro | 1 | INCH | CENTIMETER | 2.54 |
| Yarda → Metro | 1 | YARD | METER | 0.9144 |

### Conversiones de Temperatura

| Operación | value | fromUnit | toUnit | Resultado |
|-----------|-------|----------|--------|-----------|
| Celsius → Fahrenheit | 0 | CELSIUS | FAHRENHEIT | 32 |
| Celsius → Fahrenheit | 100 | CELSIUS | FAHRENHEIT | 212 |
| Celsius → Kelvin | 0 | CELSIUS | KELVIN | 273.15 |
| Fahrenheit → Celsius | 32 | FAHRENHEIT | CELSIUS | 0 |
| Kelvin → Celsius | 273.15 | KELVIN | CELSIUS | 0 |

---

## Solución de Problemas

### Error: "You must install or update .NET to run this application"

**Problema:** La versión de .NET solicitada no está instalada.

**Solución:**
1. Verifica qué versiones tienes instaladas:
   ```bash
   dotnet --list-runtimes
   ```
2. Si falta .NET 10, instálalo desde: https://dotnet.microsoft.com/download/dotnet

### Error: "Address already in use"

**Problema:** El puerto 8080 está siendo usado por otra aplicación.

**Solución:**
1. Detén el proceso que usa el puerto o cambia el puerto en `Program.cs`:
   ```csharp
   options.ListenAnyIP(8081);  // Cambia a otro puerto
   ```

### Error: "No route matched"

**Problema:** La URL del endpoint es incorrecta.

**Solución:** Verifica que estés usando la URL correcta:
- `http://localhost:8080/ConversionService.svc`

### Error: "Error in line 1 of document"

**Problema:** El XML enviado tiene errores de sintaxis.

**Solución:**
1. Verifica que el XML esté bien formado
2. Usa comillas simples en el comando curl: `curl '...'`
3. Verifica que los namespaces sean correctos

### WSDL no disponible

**Problema:** No se puede acceder al WSDL.

**Solución:** Verifica que `HttpGetEnabled = true` esté configurado en `Program.cs`.

---

## Herramientas Recomendadas para Probar

### SoapUI (Recomendado)

1. Descarga SoapUI desde: https://www.soapui.org/downloads/soapui-open-source/
2. Crea un nuevo proyecto SOAP
3. Ingresa la URL del WSDL: `http://localhost:8080/ConversionService.svc?wsdl`
4. SoapUI generará automáticamente las plantillas de solicitud

### Postman

1. Crea una nueva solicitud POST
2. Establece el header `Content-Type: text/xml`
3. Establece el header `SOAPAction` con la URI de la operación
4. Envía el XML de solicitud

### curl (Línea de comandos)

Útil para pruebas rápidas. Ver ejemplos en la sección [Ejemplos de Solicitudes XML](#ejemplos-de-solicitudes-xml).

---

## Comparación: Java vs .NET

| Aspecto | Java (Jakarta EE) | .NET (CoreWCF) |
|---------|-------------------|----------------|
| Plataforma | Multiplataforma | Multiplataforma |
| Framework | Jakarta SOAP (JAX-WS) | CoreWCF |
| Contrato | `@WebService` + `@WebMethod` | `[ServiceContract]` + `[OperationContract]` |
| DTO | `@XmlType` + `@XmlAccessorType` | `[DataContract]` + `[DataMember]` |
| Endpoint | `.jws` o `.asmx` | `.svc` |
| Configuración | `web.xml` | `Program.cs` |

---

## Licencia

Este proyecto es parte del trabajo académico del Grupo 3 de la materia de Arquitectura de Software.
