# Cliente de Consola C# para Servicio SOAP

## Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Arquitectura del Cliente](#arquitectura-del-cliente)
3. [Estructura de Archivos](#estructura-de-archivos)
4. [Explicación de Componentes](#explicación-de-componentes)
5. [Configuración y Ejecución](#configuración-y-ejecución)
6. [Uso del Cliente](#uso-del-cliente)
7. [Validaciones Implementadas](#validaciones-implementadas)
8. [Estructura del Código](#estructura-del-código)

---

## Descripción General

Este cliente de consola permite interactuar con el servicio SOAP de conversión de unidades. Fue migrado de Java a **C# (.NET 10)** y proporciona una **interfaz TUI (Terminal User Interface)** interactiva con navegación por teclado.

### Características Principales

- **Navegación por flechas** ↑↓ para seleccionar opciones
- **Menú visual** con selección destacada
- **Selección de unidades por menú** (no escribe, solo selecciona)
- **Validación robusta** de valores numéricos
- **Manejo de excepciones** para conexiones perdidas
- **Colores en consola** para mejor experiencia de usuario (UX)

---

## Arquitectura del Cliente

```
┌─────────────────────────────────────────────────────────────────┐
│                   CLIENTE DE CONSOLA                           │
│                      (Usuario)                                │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Program.cs                               │
│                    (Punto de entrada)                         │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     MenuHandler.cs                             │
│  • Gestiona el menú principal TUI                             │
│  • Lee y valida entrada del usuario                            │
│  • Controla el flujo de la aplicación                          │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   ConsoleHelper.cs                             │
│  • Renderiza menús visuales                                     │
│  • Maneja navegación con flechas                               │
│  • Proporciona selección por teclado                          │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    ServiceInvoker.cs                           │
│  • Crea conexión SOAP                                           │
│  • Invoca los métodos del servicio                             │
│  • Maneja errores de comunicación                              │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              SERVICIO SOAP (Backend .NET)                       │
│         http://localhost:8080/ConversionService.svc             │
└─────────────────────────────────────────────────────────────────┘
```

---

## Estructura de Archivos

```
dotnet-clients/
└── console-client/
    ├── ConsoleClient.csproj     # Archivo de proyecto .NET
    ├── Program.cs               # Punto de entrada
    ├── MenuHandler.cs            # Gestor de menú e interacción
    ├── ConsoleHelper.cs          # Utilidades TUI para menús
    ├── ServiceInvoker.cs        # Comunicador SOAP
    └── README.md                # Este archivo
```

---

## Explicación de Componentes

### 1. Program.cs - Punto de Entrada

```csharp
using System;
using System.Threading.Tasks;

namespace ConsoleClient
{
    class Program
    {
        static async Task Main(string[] args)
        {
            Console.Title = "SOAP Conversion Client";
            Console.WriteLine("========================================");
            Console.WriteLine("    Sistema de Conversion Universal");
            Console.WriteLine("========================================");

            var menu = new MenuHandler();
            await menu.RunMainMenuAsync();
        }
    }
}
```

### 2. ConsoleHelper.cs - Utilidades TUI

Este componente maneja toda la interfaz visual:

```csharp
public static class ConsoleHelper
{
    public static int ShowSelectionMenu(string[] options, int startY = -1)
    {
        int currentSelection = 0;
        // Renderiza opciones con ↑↓ y Enter para seleccionar
        // Resalta la opción actual con colores invertidos
    }

    public static string ShowUnitSelection(string[] units)
    {
        // Muestra menú de selección de unidades
    }
}
```

### 3. MenuHandler.cs - Gestor de Menú

```csharp
public class MenuHandler
{
    private readonly string[] _massUnits = { "KILOGRAM", "GRAM", "POUND", "OUNCE" };
    private readonly string[] _lengthUnits = { "METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH" };
    private readonly string[] _tempUnits = { "CELSIUS", "FAHRENHEIT", "KELVIN" };

    public async Task RunMainMenuAsync()
    {
        // Menú principal con navegación por flechas
        int option = ConsoleHelper.ShowSelectionMenu(new[]
        {
            "Conversion de Masa",
            "Conversion de Longitud",
            "Conversion de Temperatura",
            "Salir"
        });
        // ...
    }

    private string ReadUnitFromMenu(string[] units, string label)
    {
        // Selección de unidades por menú (flechas + ENTER)
    }

    private double ReadValidatedDouble(string prompt)
    {
        // Validación: no vacío, numérico, positivo
    }
}
```

---

## Configuración y Ejecución

### Requisitos Previos

- **.NET SDK 10.0** o superior instalado
- El servicio backend debe estar ejecutándose en `http://localhost:8080`

### Compilación

```bash
cd dotnet-clients/console-client
dotnet build
```

### Ejecución

```bash
dotnet run
```

### Flujo de Uso

1. **Iniciar el servidor** (en otra terminal):
   ```bash
   cd dotnet-wcf/ConversionApp && dotnet run
   ```

2. **Iniciar el cliente**:
   ```bash
   cd dotnet-clients/console-client && dotnet run
   ```

3. **Interfaz TUI**:
   ```
   ╔════════════════════════════════════════╗
   ║   SISTEMA DE CONVERSION UNIVERSAL      ║
   ║         Cliente SOAP .NET              ║
   ╚════════════════════════════════════════╝

     Presione ENTER para seleccionar
     Use ↑↓ para navegar

     ▼ Conversion de Masa
       Conversion de Longitud
       Conversion de Temperatura
       Salir
   ```

4. **Selección de unidades**:
   ```
   Seleccione unidad de ORIGEN (flechas ↑↓ + ENTER):

     ▼ KILOGRAM
       GRAM
       POUND
       OUNCE
   ```

5. **Resultado**:
   ```
   ========== RESULTADO ==========
     Categoria: MASS
     1 KILOGRAM
          = 2.204623 POUND
   ==============================
   ```

---

## Uso del Cliente

### Controles del Menú

| Tecla | Acción |
|-------|--------|
| ↑ | Navegar hacia arriba |
| ↓ | Navegar hacia abajo |
| ENTER | Seleccionar opción |
| ESC | Cancelar/volver |

### Menú Principal

| Opción | Descripción |
|--------|-------------|
| Conversion de Masa | Conversión de unidades de masa |
| Conversion de Longitud | Conversión de unidades de longitud |
| Conversion de Temperatura | Conversión de unidades de temperatura |
| Salir | Cerrar el programa |

### Unidades Disponibles (Selección por Menú)

#### Masa
- KILOGRAM, GRAM, POUND, OUNCE

#### Longitud
- METER, KILOMETER, CENTIMETER, MILE, YARD, FOOT, INCH

#### Temperatura
- CELSIUS, FAHRENHEIT, KELVIN

---

## Validaciones Implementadas

### 1. Valores Numéricos Positivos

El usuario SOLO puede ingresar números positivos. No se permiten:
- Texto (letras)
- Números negativos
- Campos vacíos

```
Ingrese valor: -5
⚠ Solo valores positivos.

Ingrese valor: abc
⚠ Ingrese un numero valido.

Ingrese valor: (vacío)
⚠ No puede estar vacio.
```

### 2. Selección de Unidades por Menú

El usuario selecciona unidades desde menús visuales:
- Ya no puede escribir "MMM" o "CC"
- Si desea escribir manualmente, puede presionar ESC
- Las opciones válidas están claramente visibles

### 3. Manejo de Errores de Conexión

Si el servidor no está disponible:

```
Conectando con el servidor SOAP...

[Error]: No se pudo conectar al servicio. Verifique que el servidor este activo.
```

### 4. Colores de Consola

| Color | Uso |
|-------|-----|
| 🔵 Cyan | Banner principal |
| 🟡 Amarillo | Sub-headers y advertencias |
| 🔴 Rojo | Errores |
| 🟢 Verde | Resultados exitosos |
| ⚫ Negro/⚪ Blanco | Selección activa (invertido) |

---

## Estructura del Código

### Flujo de una Conversión

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Usuario ve menú con opciones destacadas                 │
│    (usa ↑↓ para navegar)                                  │
└─────────────────────────────┬───────────────────────────────┘
                              │ ENTER
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. Menú pide valor numérico (solo positivos)              │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Selección de unidad de ORIGEN (menú visual)            │
│    KILOGRAM, GRAM, POUND, OUNCE                            │
└─────────────────────────────┬───────────────────────────────┘
                              │ ENTER
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Selección de unidad de DESTINO (menú visual)            │
└─────────────────────────────┬───────────────────────────────┘
                              │ ENTER
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. ServiceInvoker envía petición SOAP                      │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. Muestra resultado o error                               │
└─────────────────────────────────────────────────────────────┘
```

---

## Comparación: Cliente Java vs Cliente C#

| Aspecto | Cliente Java | Cliente C# (Nuevo) |
|---------|-------------|-------------------|
| Plataforma | Multiplataforma | Multiplataforma |
| Protocolo | Jakarta SOAP | System.ServiceModel.Http |
| Interfaz | Línea de comandos | **TUI con flechas** |
| Validación | Manual | **Integrada con menús** |
| Unidades | Se escriben | **Se seleccionan** |
| Valores | Cualquiera | **Solo positivos** |

---

## Solución de Problemas

### El menú no responde a las flechas

**Causa:** Terminal incompatible o keys no reconocidas.

**Solución:** Presiona ENTER después de navegar mentalmente.

### El cliente se cierra inmediatamente

**Causa:** Error en la ejecución.

**Solución:**
```bash
cd dotnet-clients/console-client
dotnet build
dotnet run
```

### "No se pudo conectar al servicio"

**Causa:** El servidor no está corriendo.

**Solución:** Inicia el servidor primero:
```bash
cd dotnet-wcf/ConversionApp && dotnet run
```

---

## Licencia

Este proyecto es parte del trabajo académico del Grupo 3 de la materia de Arquitectura de Software.
