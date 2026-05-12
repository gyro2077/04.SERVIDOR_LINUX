# Cliente de Escritorio Avalonia para Servicio SOAP

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

Este cliente de escritorio permite interactuar con el servicio SOAP de conversion de unidades a traves de una interfaz grafica moderna. Desarrollado en **Avalonia UI 12** con **.NET 10**, es multiplataforma (Windows, Linux, macOS).

### Caracteristicas Principales

- **Interfaz Grafica Rica**: Diseno moderno con XAML usando FluentTheme
- **Configuracion del Servidor**: Ventana de configuracion inicial para IP/puerto
- **Seleccion Visual**: ComboBox para categorias y unidades
- **Comunicacion Asincrona**: No congela la interfaz mientras espera respuesta
- **Manejo de Errores**: Dialogos elegantes para errores de conexion
- **Multiplataforma**: Funciona en Windows, Linux y macOS

---

## Arquitectura del Proyecto

```
┌─────────────────────────────────────────────────────────────────┐
│                    CLIENTE DE ESCRITORIO                         │
│                      (Avalonia UI)                              │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SetupWindow                                │
│  • Configuracion de IP y puerto del servidor                   │
│  • Validacion de entrada                                        │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      MainWindow                                  │
│  • Interfaz grafica principal                                   │
│  • Seleccion de categoria y unidades                            │
│  • Conversiones y resultados                                    │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      AppConfig                                 │
│  • Configuracion centralizada del servidor                   │
│  • Endpoint dinamico                                            │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              SERVICIO SOAP (Backend .NET)                       │
│         http://IP:PUERTO/ConversionService.svc                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Estructura de Archivos

```
dotnet-clients/desktop-client/
├── App.axaml                    # Configuracion de la aplicacion
├── App.axaml.cs                 # Punto de entrada Avalonia
├── AppConfig.cs                 # Configuracion del servidor (NUEVO)
├── Program.cs                   # Main
├── SetupWindow.axaml           # Ventana de configuracion (NUEVO)
├── SetupWindow.axaml.cs        # Logica de configuracion (NUEVO)
├── MainWindow.axaml            # Interfaz grafica principal
├── MainWindow.axaml.cs         # Logica y eventos
├── desktop-client.csproj      # Archivo de proyecto
├── README.md                    # Este archivo
└── bin/Debug/                  # Archivos compilados
```

---

## Explicacion de Componentes

### 1. App.axaml / App.axaml.cs - Punto de Entrada

```csharp
public partial class App : Application
{
    public override void OnFrameworkInitializationCompleted()
    {
        if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
        {
            desktop.MainWindow = new SetupWindow();  // Inicia con configuracion
        }
    }
}
```

**Funcion**: Inicializa la aplicacion y muestra primero la ventana de configuracion.

### 2. AppConfig.cs - Configuracion Centralizada

```csharp
public static class AppConfig
{
    public static string ServerAddress { get; set; } = "localhost";
    public static string ServerPort { get; set; } = "8080";
    
    public static string EndpointAddress => 
        $"http://{ServerAddress}:{ServerPort}/ConversionService.svc";
    
    public static void Configure(string serverAddress, string serverPort = "8080")
    {
        ServerAddress = serverAddress;
        ServerPort = serverPort;
    }
}
```

**Funcion**: Centraliza la configuracion del servidor para que sea accesible desde cualquier ventana.

### 3. SetupWindow - Ventana de Configuracion

```xml
<Window Title="Configuracion - Cliente SOAP" Width="450" Height="280">
    <StackPanel>
        <TextBox x:Name="TxtServerAddress" Watermark="localhost o IP..."/>
        <TextBox x:Name="TxtPort" Text="8080"/>
        <Button x:Name="BtnConnect" Content="CONECTAR"/>
    </StackPanel>
</Window>
```

**Flujo**:
1. Usuario ingresa direccion IP del servidor
2. Valida que el puerto sea numerico
3. Guarda la configuracion en `AppConfig`
4. Abre la ventana principal

### 4. MainWindow - Ventana Principal

```xml
<Window Title="SOAP Conversion Client" Width="550" Height="400">
    <StackPanel Margin="20">
        <ComboBox x:Name="CmbCategory">
            <ComboBoxItem Content="Masa" Tag="Mass"/>
            <ComboBoxItem Content="Longitud" Tag="Length"/>
            <ComboBoxItem Content="Temperatura" Tag="Temperature"/>
        </ComboBox>
        
        <Grid ColumnDefinitions="*,Auto,*">
            <ComboBox x:Name="CmbFromUnit"/>
            <TextBox x:Name="TxtValue"/>
            <ComboBox x:Name="CmbToUnit"/>
            <TextBox x:Name="TxtResult" IsReadOnly="True"/>
        </Grid>
        
        <Button x:Name="BtnConvert" Content="CONVERTIR"/>
        <TextBlock x:Name="TxtStatus"/>
    </StackPanel>
</Window>
```

---

## Configuracion del Servidor

### Importante: Cambio de IP

Por defecto, el cliente apunta a `localhost`. Si el servidor esta en otra maquina:

**Ejemplo**: Si el servidor esta en `192.168.100.171`:

1. Al iniciar la aplicacion, aparece la ventana de configuracion
2. En "Direccion del Servidor" escribe: `192.168.100.171`
3. El puerto por defecto es `8080`
4. Presiona "CONECTAR"

### Configuracion Segun el Entorno

| Entorno | Servidor | Puerto | Descripcion |
|---------|----------|--------|-------------|
| **Local** | `localhost` | 8080 | Servidor en la misma maquina |
| **VM Windows** | `192.168.x.x` | 8080 | IP de la maquina host |
| **Red Local** | IP de la PC | 8080 | Cualquier PC en la red |
| **Produccion** | Dominio/IP | 8080 | Servidor externo |

---

## Configuracion y Ejecucion

### Requisitos Previos

- **.NET SDK 10.0** o superior
- **JetBrains Rider** (o Visual Studio / dotnet CLI)

### Creacion del Proyecto (desde cero)

Si necesitas recrear el proyecto:

```bash
# 1. Instalar plantillas Avalonia
dotnet new install Avalonia.Templates

# 2. Crear el proyecto
cd dotnet-clients
dotnet new avalonia.app -n desktop-client
cd desktop-client

# 3. Compilar para verificar
dotnet build
```

### Compilacion

```bash
cd dotnet-clients/desktop-client
dotnet build
```

### Ejecucion

```bash
dotnet run
```

### En Rider

1. Abre **File → Open Project**
2. Selecciona `desktop-client.csproj`
3. Presiona **Shift+F10** (Run)
4. O usa el boton verde de "Run" en la barra superior

### En Visual Studio (Windows)

1. Abre la solucion `.sln` o el proyecto `.csproj`
2. Selecciona **dotnet run** o presiona `F5`

---

## Uso del Cliente

### 1. Ventana de Configuracion

Al iniciar la aplicacion, aparece:

```
┌────────────────────────────────────────┐
│   Configuracion del Servidor           │
│                                        │
│   Ingrese la direccion IP o nombre     │
│   del servidor donde se ejecuta el    │
│   servicio SOAP.                      │
│                                        │
│   Direccion del Servidor:              │
│   [localhost                   ]       │
│                                        │
│   Puerto:                              │
│   [8080                         ]      │
│                                        │
│            [CONECTAR]                  │
└────────────────────────────────────────┘
```

### 2. Ventana Principal

```
┌────────────────────────────────────────┐
│   Conversor Universal SOAP            │
│   Cliente de Escritorio               │
│                                        │
│   Categoria: [Masa              ▼]    │
│                                        │
│   ┌──────────┐    ┌──────────┐        │
│   │De (Orig) │    │A (Dest)  │        │
│   │[KILO  ▼] │ -> │[POUND▼]  │        │
│   │          │    │          │        │
│   │Valor:    │    │Resultado:│        │
│   │[1      ] │    │[2.2046  ]│        │
│   └──────────┘    └──────────┘        │
│                                        │
│            [CONVERTIR]                 │
│                                        │
│   Conversion exitosa.                 │
└────────────────────────────────────────┘
```

### Controles

| Elemento | Descripcion |
|----------|-------------|
| ComboBox Categoria | Selecciona: Masa, Longitud, Temperatura |
| ComboBox De (Origen) | Unidad de origen |
| ComboBox A (Destino) | Unidad de destino |
| TextBox Valor | Numero a convertir (solo positivos) |
| TextBox Resultado | Resultado de la conversion (solo lectura) |
| Boton CONVERTIR | Ejecuta la conversion |
| TextBlock Estado | Mensaje de estado (Listo, Conectando, Error...) |

### Unidades Disponibles

#### Masa
- KILOGRAM, GRAM, POUND, OUNCE

#### Longitud
- METER, KILOMETER, CENTIMETER, MILE, YARD, FOOT, INCH

#### Temperatura
- CELSIUS, FAHRENHEIT, KELVIN

---

## Validaciones Implementadas

### 1. Validacion de Entrada Numerica

El valor solo acepta numeros positivos:

```
- Se permiten: 0, 1, 100, 3.14, -5.5 (negativos se rechazan)
- Se rechazan: texto, simbolos especiales
```

### 2. Validacion de Campos Vacios

```
"Ingrese un valor para convertir"
```

### 3. Validacion de Puerto

```
"El puerto debe ser un numero entre 1 y 65535"
```

### 4. Comunicacion Asincrona

```csharp
await PerformConversionAsync(category, fromUnit, toUnit, value);
```

La interfaz NO se congela mientras espera la respuesta del servidor.

### 5. Manejo de Errores de Conexion

| Error | Mensaje |
|-------|---------|
| Servidor caido | "No se pudo conectar al servidor SOAP..." |
| Timeout | "Tiempo de espera agotado..." |
| Error HTTP | "Error HTTP: {codigo}" |

### 6. Estados Visuales

| Estado | Color | Mensaje |
|--------|-------|---------|
| Listo | Gris (#636E72) | "Listo. Seleccione categoria..." |
| Cargando | Azul (#0984E3) | "Conectando al servidor SOAP..." |
| Exito | Verde (#00B894) | "Conversion exitosa." |
| Error | Rojo (#D63031) | "Error en la conexion." |

---

## Solucion de Problemas

### Error: "No se pudo conectar al servidor SOAP"

**Causas posibles**:
1. El servidor no esta ejecutandose
2. La direccion IP es incorrecta
3. El puerto es incorrecto
4. Firewall bloqueando la conexion

**Soluciones**:

1. Verifica que el servidor este corriendo:
   ```bash
   curl 'http://localhost:8080/ConversionService.svc?wsdl'
   ```

2. Verifica la IP del servidor:
   ```bash
   # En Windows (CMD)
   ipconfig
   
   # En Linux
   ip addr show
   ```

3. Verifica que el puerto este abierto:
   ```bash
   # En el servidor, verifica que el servicio escucha en el puerto
   netstat -tlnp | grep 8080
   ```

### Error: "Respuesta SOAP invalida"

**Causa**: El servidor devolvio un XML inesperado.

**Solucion**: Verifica que estas conectando al servicio SOAP correcto.

### La aplicacion no responde

**Causa**: Timeout esperando respuesta del servidor.

**Solucion**: Aumenta el timeout en `MainWindow.axaml.cs` o verifica la conexion de red.

### La ventana no aparece

**Causa**: Error en la carga de XAML.

**Solucion**: Verifica que todos los archivos `.axaml` tengan el namespace correcto (`desktop_client`).

---

## Comparacion: Tecnologias de Escritorio

| Tecnologia | Plataforma | Dificultad | UI Moderno |
|-----------|-------------|-----------|------------|
| **WPF** | Solo Windows | Media | Si |
| **WinForms** | Solo Windows | Baja | No |
| **Avalonia UI** | Todas | Media | Si |
| **Qt** | Todas | Alta | Si |

**Avalonia UI** fue elegido por:
1. Soporte multiplataforma (Linux, Windows, macOS)
2. UI moderna con FluentTheme
3. Sintaxis XAML similar a WPF
4. Integracion con JetBrains Rider

---

## Licencia

Este proyecto es parte del trabajo academico del Grupo 3 de la materia de Arquitectura de Software.
