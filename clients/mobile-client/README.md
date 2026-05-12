# Cliente Móvil Android - Conversor SOAP (Backend Java/Payara)

## Descripción del Proyecto

Este proyecto es un **cliente móvil nativo para Android** desarrollado en **Kotlin** que consume el servicio web SOAP del servidor **Java (Payara)** para realizar conversiones de unidades (Masa, Longitud, Temperatura).

### Características Principales

- **Interfaz Declarativa**: Jetpack Compose con Material Design 3
- **Comunicación SOAP**: OkHttp con parseo XML nativo (XmlPullParser)
- **Arquitectura MVVM**: Separación de UI (Compose), ViewModel y Repository
- **Asincronía**: Corrutinas con Dispatchers.IO para operaciones de red
- **Estados Reactivos**: UI que reacciona automáticamente a Loading/Success/Error

---

## Diferencias Clave con el Cliente .NET

| Aspecto | Cliente .NET | Cliente Java (Payara) |
|---------|-------------|----------------------|
| URL Base | `http://10.0.2.2:8080/ConversionService.svc` | `http://10.0.2.2:8080/04.SERVIDOR/conversion` |
| SOAPAction Header | `http://ws.grupo3.edu.ec/ConversionService/convertX` | `http://ws.grupo3.edu.ec/convertX` |
| Namespaces XML | WCF (.NET) | JAX-WS (Java) |
| Respuesta XML | Nombres en mayúsculas (Category) | Nombres en mayúsculas (category) |

---

## Requisitos Previos

1. **JDK 17** o superior
2. **Android SDK** (API 34)
3. **Servidor Payara** ejecutándose en `http://localhost:8080/04.SERVIDOR/conversion`
4. **Mismo segmento de red** (si usas dispositivo físico)

---

## Estructura del Proyecto

```
clients/mobile-client/
├── build.gradle.kts              # Plugins del proyecto (AGP, Kotlin)
├── settings.gradle.kts           # Repositorios y configuración
├── gradle.properties             # Propiedades de Gradle
├── local.properties              # SDK de Android
├── gradlew / gradle/wrapper/      # Gradle Wrapper
└── app/
    ├── build.gradle.kts           # Dependencias de la app
    └── src/main/
        ├── AndroidManifest.xml   # Permisos y configuración
        └── java/ec/edu/grupo3/mobile/
            ├── MainActivity.kt              # Entry point
            ├── data/
            │   ├── ConversionResponse.kt    # Modelo de datos
            │   └── SoapRepository.kt        # Comunicación SOAP
            └── ui/
                ├── ConversionScreen.kt      # UI Compose
                ├── ConversionViewModel.kt   # Gestor de estado
                └── theme/
                    ├── Color.kt
                    ├── Theme.kt
                    └── Type.kt
```

---

## Configuración de Red

### IMPORTANTE: Configuración de IP

El cliente móvil necesita conectarse a tu servidor Payara. Dependiendo de cómo pruebes:

**Para Emulador Android:**
```kotlin
// En SoapRepository.kt
private val baseUrl = "http://10.0.2.2:8080/04.SERVIDOR/conversion"
```
`10.0.2.2` es la IP especial del emulador que apunta al `localhost` de la máquina host.

**Para Dispositivo Físico:**
1. Ejecuta `hostname -I` para obtener tu IP local
2. Edita `SoapRepository.kt`:
```kotlin
private val baseUrl = "http://192.168.X.X:8080/04.SERVIDOR/conversion"
// Reemplaza 192.168.X.X con tu IP real
```

---

## Compilación

```bash
cd /home/gyro/Documents/ULTIMO_SEMESTRE/ARQUITECTURA/04.SERVIDOR/clients/mobile-client
./gradlew assembleDebug
```

**APK generado**: `app/build/outputs/apk/debug/app-debug.apk`

---

## Explicación Técnica del Código

### SoapRepository.kt - Comunicación con Payara

Este es el componente más crítico. Maneja la comunicación SOAP con el servidor JAX-WS/Payara.

**URL y Namespace:**
```kotlin
private val namespace = "http://ws.grupo3.edu.ec/"
private val baseUrl = "http://192.168.100.171:8080/04.SERVIDOR/conversion"
```

**Envelope SOAP CORRECTO (formato que funciona):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:ns="http://ws.grupo3.edu.ec/">
  <soap:Body>
    <ns:convertMass>
      <value>1.0</value>
      <fromUnit>KILOGRAM</fromUnit>
      <toUnit>POUND</toUnit>
    </ns:convertMass>
  </soap:Body>
</soap:Envelope>
```

**Puntos clave del envelope:**
- El elemento raíz del método (`<ns:convertMass>`) lleva el namespace
- Los parámetros hijos (`<value>`, `<fromUnit>`, `<toUnit>`) NO llevan prefijo de namespace
- Esto es diferente a otros servidores SOAP y fue el cause de errores iniciales

**Cabeceras HTTP:**
```kotlin
.addHeader("Content-Type", "text/xml; charset=utf-8")
.addHeader("SOAPAction", "http://ws.grupo3.edu.ec/convertMass")
```

**Parseo de respuesta:**
```kotlin
// La respuesta viene dentro de <return>
<convertMassResponse>
  <return>
    <category>MASS</category>
    <fromUnit>KILOGRAM</fromUnit>
    <toUnit>POUND</toUnit>
    <inputValue>1.0</inputValue>
    <resultValue>2.2046226218487757</resultValue>
  </return>
</convertMassResponse>
```

El parser busca específicamente el tag `<return>` y extrae los valores de dentro.

### ConversionViewModel.kt - Gestor de Estado

```kotlin
sealed interface UiState {
    object Idle : UiState       // Sin acción
    object Loading : UiState    // Ejecutando conversión
    data class Success(val response: ConversionResponse) : UiState
    data class Error(val message: String) : UiState
}
```

**Flujo de ejecución:**
1. Usuario presiona "Convertir"
2. ViewModel valida que el input sea numérico
3. Cambia estado a `Loading`
4. Llama al Repository con corrutina (`viewModelScope.launch`)
5. Repository ejecuta en hilo secundario (`Dispatchers.IO`)
6. Según resultado: `Success` o `Error`

### ConversionScreen.kt - UI Jetpack Compose

Componentes principales:
- **TopAppBar**: Barra de título "Conversor SOAP Java"
- **ExposedDropdownMenuBox**: Dropdowns modernos para categoría y unidades
- **OutlinedTextField**: Campo de texto numérico
- **Button**: Botón de conversión con indicador de carga
- **AnimatedVisibility**: Animaciones para mostrar/ocultar resultados
- **Card**: Tarjetas de resultado (verde=éxito, rojo=error)

---

## Historial de Correcciones

### Error HTTP 500 - Problema y Solución

**Síntoma:** El servidor respondía con HTTP 500 al enviar peticiones desde el cliente móvil.

**Causa raíz:** El formato del XML SOAP era incorrecto. Los parámetros estaban enviando con prefijo de namespace (`<tns:value>`), pero el servidor JAX-WS/Payara espera los parámetros sin prefijo.

**Corrección aplicada:**

❌ **INCORRECTO:**
```xml
<tns:convertMass>
  <tns:value>$value</tns:value>
  <tns:fromUnit>$fromUnit</tns:fromUnit>
</tns:convertMass>
```

✅ **CORRECTO:**
```xml
<ns:convertMass>
  <value>$value</value>
  <fromUnit>$fromUnit</fromUnit>
</ns:convertMass>
```

**Verificación:** Se probó con `curl` directamente antes de actualizar el cliente:
```bash
curl -X POST "http://localhost:8080/04.SERVIDOR/conversion" \
  -H "SOAPAction: http://ws.grupo3.edu.ec/convertMass" \
  -d '...CORRECT FORMAT...'
```

---

## Configuración del Backend

### Endpoint del Servicio

```
URL Base: http://localhost:8080/04.SERVIDOR/conversion
WSDL:     http://localhost:8080/04.SERVIDOR/conversion?wsdl
```

### Métodos SOAP Disponibles

| Método | Descripción | Ejemplo |
|--------|-------------|---------|
| `convertMass` | Convierte unidades de masa | KILOGRAM → POUND |
| `convertLength` | Convierte unidades de longitud | METER → MILE |
| `convertTemperature` | Convierte unidades de temperatura | CELSIUS → FAHRENHEIT |

### Unidades Soportadas

**Masa:** KILOGRAM, GRAM, POUND, OUNCE
**Longitud:** METER, KILOMETER, CENTIMETER, MILE, YARD, FOOT, INCH
**Temperatura:** CELSIUS, FAHRENHEIT, KELVIN

### Estructura de Respuesta XML

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertMassResponse xmlns:ns2="http://ws.grupo3.edu.ec/">
      <return>
        <category>MASS</category>
        <fromUnit>KILOGRAM</fromUnit>
        <toUnit>POUND</toUnit>
        <inputValue>1.0</inputValue>
        <resultValue>2.2046226218487757</resultValue>
      </return>
    </ns2:convertMassResponse>
  </soap:Body>
</soap:Envelope>
```

---

## Ejecución en Dispositivo

### Emulador Android

1. Iniciar el emulador desde Android Studio
2. El emulador accederá automáticamente a `10.0.2.2` → localhost de tu PC
3. El servidor Payara debe estar corriendo

### Dispositivo Físico (Recomendado)

1. **Obtener tu IP local:**
   ```bash
   hostname -I
   ```

2. **Actualizar SoapRepository.kt** con tu IP:
   ```kotlin
   private val baseUrl = "http://TU_IP:8080/04.SERVIDOR/conversion"
   ```

3. **Compilar de nuevo:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Instalar APK:**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

5. **Importante:** El dispositivo debe estar en la misma red WiFi que el servidor.

---

## Solución de Problemas

| Error | Causa Posible | Solución |
|-------|---------------|----------|
| `HTTP 500` | Formato XML incorrecto | Verificar envelope SOAP (parámetros sin prefijo de namespace) |
| `Connection refused` | Servidor no corriendo | Iniciar Payara: `sudo systemctl start payara` |
| `Connection timeout` | IP incorrecta | Verificar IP con `hostname -I` y actualizar en SoapRepository |
| `NetworkOnMainThreadException` | Operaciones en hilo principal | El código ya usa Dispatchers.IO - no debería ocurrir |
| `Cleartext traffic not permitted` | Android bloquea HTTP | Ya está habilitado en AndroidManifest.xml |
| `resource style/Theme not found` | Tema XML inexistente | Ya se removió del AndroidManifest |

### Verificar que el servidor responde

```bash
# Probar el WSDL
curl "http://localhost:8080/04.SERVIDOR/conversion?wsdl" | head -20

# Probar una conversión directamente
curl -X POST "http://localhost:8080/04.SERVIDOR/conversion" \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: http://ws.grupo3.edu.ec/convertMass" \
  -d '<?xml version="1.0"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:ns="http://ws.grupo3.edu.ec/">
  <soap:Body>
    <ns:convertMass>
      <value>1.0</value>
      <fromUnit>KILOGRAM</fromUnit>
      <toUnit>POUND</toUnit>
    </ns:convertMass>
  </soap:Body>
</soap:Envelope>'
```

---

## Información del Build

| Componente | Versión |
|------------|---------|
| Gradle | 9.0.0 |
| Android Gradle Plugin | 8.5.0 |
| Kotlin | 1.9.24 |
| Compose Compiler | 1.5.14 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |
| OkHttp | 4.12.0 |

---

## Autor

Desarrollado para el proyecto de Arquitectura de Software - Cliente SOAP para Servidor Java (Payara).

**Mayo 2026**