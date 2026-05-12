# Cliente Web ASP.NET Core MVC para Servicio SOAP

## Tabla de Contenidos

1. [Descripcion General](#descripcion-general)
2. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
3. [Estructura de Archivos](#estructura-de-archivos)
4. [Explicacion de Componentes](#explicacion-de-componentes)
5. [Configuracion del Servidor](#configuracion-del-servidor)
6. [Configuracion y Ejecucion](#configuracion-y-ejecucion)
7. [Uso del Cliente](#uso-del-cliente)
8. [Validaciones Implementadas](#validaciones-implementadas)
9. [Solucion de Problemas](#solucion-de-problemas)

---

## Descripcion General

Este cliente web permite interactuar con el servicio SOAP de conversion de unidades a traves de un navegador web. Desarrollado en **ASP.NET Core MVC** con **Bootstrap 5**, es responsive y funciona en cualquier dispositivo.

### Caracteristicas Principales

- **Arquitectura MVC**: Separation clara de concerns (Model-View-Controller)
- **Validacion Dual**: Cliente (JavaScript) y servidor (C#)
- **Responsive Design**: Bootstrap 5 para PC, tablets y moviles
- **Inyeccion de Dependencias**: HttpClient inyectado correctamente
- **Proteccion CSRF**: Tokens AntiForgery en todos los formularios
- **Configuracion Dinamica**: IP/puerto configurable

---

## Arquitectura del Proyecto

```
┌─────────────────────────────────────────────────────────────────┐
│                       NAVEGADOR WEB                             │
│                    (HTML + Bootstrap + JS)                     │
└─────────────────────────────┬───────────────────────────────────┘
                              │ HTTP POST / GET
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    CONTROLLER (C#)                              │
│  ConversionController                                          │
│  • Recibe peticion HTTP                                        │
│  • Valida modelo (ModelState)                                │
│  • Llama al servicio SOAP                                     │
│  • Devuelve View con resultado                                │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SERVICE (SoapService)                       │
│  • Construye XML SOAP                                         │
│  • Envia peticion HTTP                                        │
│  • Parsea respuesta XML                                       │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              SERVICIO SOAP (Backend .NET)                       │
│         http://IP:PUERTO/ConversionService.svc                  │
└─────────────────────────────────────────────────────────────────┘
```

### Flujo de Datos

```
1. Usuario llena formulario en el navegador
   |
   v
2. JavaScript valida en cliente (navegador)
   |
   v
3. POST /Conversion/Index (con AntiForgeryToken)
   |
   v
4. Controller valida modelo (C#)
   |
   v
5. SoapService.InvokeConversionAsync()
   |
   v
6. Peticion HTTP al servidor SOAP
   |
   v
7. Respuesta XML parseada
   |
   v
8. View devuelta con modelo actualizado
```

---

## Estructura de Archivos

```
dotnet-clients/web-client/
├── Configuration/
│   └── SoapServiceConfig.cs       # Configuracion del servidor
├── Controllers/
│   └── ConversionController.cs    # Logica del controlador
├── Models/
│   └── ConversionViewModel.cs     # Modelo de datos
├── Services/
│   └── SoapService.cs             # Cliente SOAP (HttpClient)
├── Views/
│   ├── Conversion/
│   │   ├── Index.cshtml           # Pagina principal
│   │   └── Configure.cshtml       # Pagina de configuracion
│   └── Shared/
│       └── _ValidationScriptsPartial.cshtml
├── wwwroot/
│   └── (archivos estaticos CSS, JS, img)
├── Program.cs                      # Punto de entrada
├── appsettings.json                # Configuracion
└── README.md                      # Este archivo
```

---

## Explicacion de Componentes

### 1. Program.cs - Configuracion del Host

```csharp
var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllersWithViews();

builder.Services.AddSoapServiceConfiguration(builder.Configuration);
builder.Services.AddHttpClient<ISoapService, SoapService>();

var app = builder.Build();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Conversion}/{action=Index}/{id?}");
```

**Puntos clave**:
- `AddControllersWithViews()` - Habilita MVC
- `AddHttpClient<ISoapService, SoapService>()` - Inyeccion de HttpClient
- Ruta por defecto: `/Conversion/Index`

### 2. ConversionViewModel.cs - Modelo de Datos

```csharp
public class ConversionViewModel
{
    [Required(ErrorMessage = "Debe seleccionar una categoria.")]
    public string Category { get; set; } = "Mass";

    [Required(ErrorMessage = "Debe ingresar un valor a convertir.")]
    [Range(0.0001, double.MaxValue, ErrorMessage = "El valor debe ser mayor a 0.")]
    public double? InputValue { get; set; }

    [Required(ErrorMessage = "Debe seleccionar la unidad de origen.")]
    public string FromUnit { get; set; }

    [Required(ErrorMessage = "Debe seleccionar la unidad de destino.")]
    public string ToUnit { get; set; }

    public double? ResultValue { get; set; }
    public string? ErrorMessage { get; set; }
    public string? SuccessMessage { get; set; }

    public List<SelectListItem> Categories { get; } = new List<SelectListItem>
    {
        new SelectListItem { Value = "Mass", Text = "Masa" },
        new SelectListItem { Value = "Length", Text = "Longitud" },
        new SelectListItem { Value = "Temperature", Text = "Temperatura" }
    };
}
```

**Data Annotations**:
- `[Required]` - Campo obligatorio
- `[Range]` - Valor numerico valido
- `[Display]` - Etiqueta para el label

### 3. SoapServiceConfig.cs - Configuracion

```csharp
public class SoapServiceConfig
{
    public string ServerAddress { get; set; } = "localhost";
    public string ServerPort { get; set; } = "8080";
    public string EndpointAddress => $"http://{ServerAddress}:{ServerPort}/ConversionService.svc";
    public string SoapNamespace => "http://ws.grupo3.edu.ec/";

    public void Configure(string serverAddress, string serverPort = "8080")
    {
        ServerAddress = serverAddress;
        ServerPort = serverPort;
    }
}
```

### 4. SoapService.cs - Cliente SOAP

```csharp
public class SoapService : ISoapService
{
    private readonly HttpClient _httpClient;
    private readonly SoapServiceConfig _config;

    public async Task<SoapResponse> InvokeConversionAsync(
        string category, double value, string fromUnit, string toUnit)
    {
        // Construir XML SOAP
        string xmlRequest = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <convert{category} xmlns=""http://ws.grupo3.edu.ec/"">
      <value>{value}</value>
      <fromUnit>{fromUnit}</fromUnit>
      <toUnit>{toUnit}</toUnit>
    </convert{category}>
  </soap:Body>
</soap:Envelope>";

        // Enviar peticion
        var httpContent = new StringContent(xmlRequest, Encoding.UTF8, "text/xml");
        httpContent.Headers.Add("SOAPAction", $"\"http://ws.grupo3.edu.ec/ConversionService/convert{category}\"");

        var response = await _httpClient.PostAsync(_config.EndpointAddress, httpContent);
        string xmlResponse = await response.Content.ReadAsStringAsync();

        // Parsear respuesta
        return ParseSoapResponse(xmlResponse);
    }
}
```

### 5. ConversionController.cs - Controlador

```csharp
public class ConversionController : Controller
{
    private readonly ISoapService _soapService;
    private readonly SoapServiceConfig _config;

    [HttpGet]
    public IActionResult Index()
    {
        return View(new ConversionViewModel { Category = "Mass" });
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Index(ConversionViewModel model)
    {
        // Validacion del modelo
        if (!ModelState.IsValid)
        {
            return View(model);
        }

        // Llamar al servicio SOAP
        var soapResponse = await _soapService.InvokeConversionAsync(
            model.Category,
            model.InputValue!.Value,
            model.FromUnit,
            model.ToUnit
        );

        if (soapResponse.Success)
        {
            model.SuccessMessage = $"Conversion exitosa: {model.InputValue} {model.FromUnit} = {soapResponse.ResultValue:F6} {model.ToUnit}";
        }
        else
        {
            model.ErrorMessage = soapResponse.ErrorMessage;
        }

        return View(model);
    }
}
```

### 6. Index.cshtml - Vista Principal

```html
@model ConversionViewModel

<form asp-action="Index" method="post">
    @Html.AntiForgeryToken()

    <select asp-for="Category" asp-items="Model.Categories" id="cmbCategory" onchange="updateUnits()"></select>

    <select asp-for="FromUnit" id="cmbFrom"></select>
    <select asp-for="ToUnit" id="cmbTo"></select>

    <input asp-for="InputValue" type="number" step="any" />

    <button type="submit" class="btn btn-primary">Realizar Conversion</button>
</form>
```

**Tag Helpers usados**:
- `asp-for` - Binding de modelo
- `asp-items` - Items del ComboBox
- `asp-validation-for` - Mensajes de error
- `asp-action` - Accion del form

---

## Configuracion del Servidor

### Archivo appsettings.json

```json
{
  "SoapService": {
    "ServerAddress": "localhost",
    "ServerPort": "8080"
  }
}
```

### Cambiar IP/Puerto desde la web

1. Accede a `/Conversion/Configure`
2. Ingresa la nueva direccion IP
3. Ingresa el nuevo puerto
4. Guarda

### Configuracion por Entorno

| Entorno | ServerAddress | ServerPort |
|---------|---------------|-------------|
| Desarrollo | localhost | 8080 |
| VM | IP de la VM | 8080 |
| Produccion | Dominio/IP | 8080 |

---

## Configuracion y Ejecucion

### Requisitos Previos

- **.NET SDK 10.0** o superior

### Compilacion

```bash
cd dotnet-clients/web-client
dotnet build
```

### Ejecucion

```bash
dotnet run
```

La aplicacion estara disponible en `http://localhost:5000` o `https://localhost:5001`.

### En Rider

1. Abre **File → Open Project**
2. Selecciona `web-client.csproj`
3. Presiona **Shift+F10** (Run)

### En Visual Studio (Windows)

1. Abre la solucion
2. Presiona `F5` (Start Debugging)

---

## Uso del Cliente

### Pagina Principal

```
┌────────────────────────────────────────────────────────────┐
│ [Configurar]    Conversor Universal SOAP                    │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  Categoria: [Masa ▼]                                      │
│                                                            │
│  ┌──────────────┐      ┌──────────────┐                  │
│  │ De (Origen)  │  ➔   │ A (Destino)  │                  │
│  │ [KILOGRAM ▼] │      │ [POUND ▼]    │                  │
│  └──────────────┘      └──────────────┘                  │
│                                                            │
│  Valor a Convertir: [100]                                  │
│                                                            │
│  [Realizar Conversion]                                    │
│                                                            │
├────────────────────────────────────────────────────────────┤
│            Cliente Web ASP.NET Core MVC                     │
└────────────────────────────────────────────────────────────┘
```

### Pagina de Configuracion

```
┌────────────────────────────────────────────────────────────┐
│ Configuracion del Servidor SOAP                           │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  Direccion del Servidor:                                  │
│  [192.168.100.171]                                       │
│                                                            │
│  Puerto:                                                  │
│  [8080]                                                  │
│                                                            │
│  [Guardar Configuracion]                                   │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

---

## Validaciones Implementadas

### 1. Validacion en Cliente (JavaScript)

- `type="number"` - Solo numeros en el input
- `step="any"` - Permitir decimales
- `min="0"` - Solo valores positivos
- jQuery Unobtrusive Validation para errores instantaneos

### 2. Validacion en Servidor (C#)

```csharp
[Required(ErrorMessage = "Debe seleccionar una categoria.")]
[Range(0.0001, double.MaxValue, ErrorMessage = "El valor debe ser mayor a 0.")]
```

### 3. Proteccion CSRF

```html
@Html.AntiForgeryToken()
```

```csharp
[ValidateAntiForgeryToken]
```

### 4. Actualizacion Dinamica de Unidades (JavaScript)

```javascript
function updateUnits() {
    const category = document.getElementById("cmbCategory").value;
    const fromSelect = document.getElementById("cmbFrom");
    const toSelect = document.getElementById("cmbTo");

    fromSelect.innerHTML = "";
    toSelect.innerHTML = "";

    units[category].forEach(u => {
        fromSelect.add(new Option(u, u));
        toSelect.add(new Option(u, u));
    });
}
```

---

## Solucion de Problemas

### Error: "No se pudo conectar al servidor SOAP"

**Causas posibles**:
1. Servidor no esta ejecutandose
2. IP incorrecta en configuracion
3. Puerto bloqueado por firewall

**Solucion**:
1. Verifica que el servidor este corriendo
2. Ve a `/Conversion/Configure` y actualiza la IP
3. Verifica que el puerto 8080 este abierto

### Error de validacion en el formulario

**Causa**: Campo vacio o valor invalido

**Solucion**: Verifica que:
- La categoria este seleccionada
- El valor sea mayor a 0
- Las unidades esten seleccionadas

### La pagina no carga estilos

**Causa**: Archivos estaticos no encontrados

**Solucion**:
```bash
# Verifica que wwwroot exista
ls wwwroot

# Compila nuevamente
dotnet clean
dotnet build
```

---

## Comparacion: Tipos de Clientes

| Tipo | Ventajas | Desventajas |
|------|----------|-------------|
| **Consola** | Rapido, simple | Sin interfaz grafica |
| **Escritorio** | Interfaz rica | Requiere instalacion |
| **Web** | Accesible desde cualquier lugar | Requiere navegador |

### Cuando usar cada uno:

- **Consola**: Automatizacion, scripts, testing
- **Escritorio**: Uso diario, mejor UX
- **Web**: Acceso desde cualquier dispositivo

---

## Licencia

Este proyecto es parte del trabajo academico del Grupo 3 de la materia de Arquitectura de Software.