# Cliente Móvil Android - Conversor SOAP

## Descripción del Proyecto

Este proyecto es un **cliente móvil nativo para Android** desarrollado en **Kotlin** que consume un servicio web SOAP para realizar conversiones de unidades. La aplicación implementa una arquitectura moderna siguiendo las mejores prácticas de desarrollo Android recomendadas por Google.

### Características Principales

- **Interfaz de Usuario Moderna**: Utiliza Jetpack Compose con Material Design 3 para una experiencia de usuario fluida y atractiva.
- **Comunicación SOAP**: Implementa clientes SOAP ligeros usando OkHttp con parseo XML nativo.
- **Arquitectura MVVM**: Separa completamente la lógica de negocio de la interfaz de usuario mediante el patrón Model-View-ViewModel.
- **Asincronía con Corrutinas**: Todas las operaciones de red se ejecutan en hilos secundarios para mantener la interfaz fluida a 60 FPS.
- **Manejo de Estados Reactivo**: La UI reacciona automáticamente a los cambios de estado (carga, éxito, error).

---

## Requisitos Previos

### Software Necesario

1. **Java Development Kit (JDK) 17 o superior**
   - Verificar instalación: `java -version`
   - Descargar: https://adoptium.net/

2. **Android SDK**
   - Located typically at `~/Android/Sdk`
   - Debe incluir Platform 34 y Build-Tools 34

3. **Gradle 9.0.0**
   - Incluido automáticamente con el proyecto (wrapper)

4. **Opcional: Android Studio**
   - Versión 2024.1 (Ladybug) o superior
   - https://developer.android.com/studio

### hardware Recomendado

- Mínimo 8 GB de RAM
- 10 GB de espacio en disco para Android SDK

---

## Estructura del Proyecto

```
mobile-client/
├── build.gradle.kts              # Configuración de plugins del proyecto
├── settings.gradle.kts           # Configuración de repos y módulos
├── gradle.properties             # Propiedades de Gradle
├── gradlew                       # Script de Gradle (Linux/Mac)
├── gradlew.bat                   # Script de Gradle (Windows)
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── app/
    ├── build.gradle.kts          # Configuración de la app
    └── src/main/
        ├── AndroidManifest.xml  # Manifiesto de Android
        └── java/ec/edu/grupo3/mobile/
            ├── MainActivity.kt              # Activity principal (Entry point)
            ├── data/
            │   ├── ConversionResponse.kt    # Modelo de datos
            │   └── SoapRepository.kt        # Lógica de comunicación SOAP
            └── ui/
                └── ConversionViewModel.kt    # Gestor de estado y lógica
```

---

## Configuración del Entorno

### Paso 1: Configurar Variables de Entorno

Agregar al archivo `~/.bashrc` o `~/.zshrc`:

```bash
# JAVA
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Android SDK
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
```

### Paso 2: VerificarSDK de Android

El proyecto automáticamente descargará los componentes necesarios, pero asegúrate de que el SDK esté accesible:

```bash
echo $ANDROID_HOME
ls $ANDROID_HOME
```

### Paso 3: Permisos de Ejecución

```bash
chmod +x gradlew
```

---

## Compilación del Proyecto

### Comando Básico

```bash
cd /home/gyro/Documents/ULTIMO_SEMESTRE/ARQUITECTURA/04.SERVIDOR/dotnet-clients/mobile-client
./gradlew assembleDebug
```

### Comandos Útiles

| Comando | Descripción |
|---------|-------------|
| `./gradlew assembleDebug` | Compila el APK de debug |
| `./gradlew assembleRelease` | Compila el APK release (requiere firma) |
| `./gradlew clean` | Limpia archivos de compilación |
| `./gradlew clean assembleDebug` | Limpia y recompila desde cero |
| `./gradlew tasks` | Lista todas las tareas disponibles |

### Salida del Build

El APK compilado se genera en:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## Cómo Ejecutar en un Dispositivo Móvil

### Opción 1: Emulador de Android

1. **Abrir Android Studio** y abrir el proyecto `mobile-client/`
2. **Crear un emulador**: Tools > Device Manager > Create Device
3. **Seleccionar** un dispositivo (Pixel 4推荐)
4. **Elegir imagen del sistema**: API 34 (más reciente)
5. **Iniciar emulador**: Clic en el botón de Play
6. **Ejecutar**: Run > Run 'app' o Shift+F10

El emulador se comunicará automáticamente con el servidor local usando la IP `10.0.2.2`.

### Opción 2: Dispositivo Físico

#### Por USB:

1. **Habilitar desarrollador** en el teléfono: Configuración > Acerca del teléfono > Toca 7 veces "Número de compilación"
2. **Habilitar USB Debugging**: Configuración > Opciones de desarrollador > Depuración USB
3. **Conectar** el teléfono por USB
4. **Ejecutar** en Android Studio

#### Por WiFi (sin cable):

1. Conectar el dispositivo por USB inicialmente
2. Ejecutar:
   ```bash
   adb tcpip 5555
   adb connect <IP_DEL_TELEFONO>:5555
   ```
3. Desconectar el cable USB

#### Instalación Directa (APK):

```bash
# Instalar APK directamente
adb install app/build/outputs/apk/debug/app-debug.apk

# Reinstalar (preserve data)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Explicación Técnica del Código

### Arquitectura General

La aplicación sigue el patrón **MVVM (Model-View-ViewModel)** con una clara separación de responsabilidades:

```
┌─────────────────────────────────────────────────────────────┐
│                         UI Layer                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              MainActivity.kt                         │   │
│  │  - Entry point de la aplicación                      │   │
│  │  - Define el tema de Compose                         │   │
│  │  - Llama a ConversionScreen                          │   │
│  └──────────────────────────────────────────────────────┘   │
│                           │                                   │
│                           ▼                                   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │           ConversionScreen (Composables)            │   │
│  │  - Construye la interfaz de usuario                  │   │
│  │  - Observa el ViewModel                              │   │
│  │  - Responde a estados (Idle/Loading/Success/Error)  │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     Business Logic                           │
│  ┌──────────────────────────────────────────────────────┐   │
│  │            ConversionViewModel.kt                    │   │
│  │  - Mantiene el estado de la UI (State)              │   │
│  │  - Gestiona la lógica de presentación               │   │
│  │  - Define las listas de unidades por categoría      │   │
│  │  - Expone funciones: convert(), onCategoryChange()  │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              SoapRepository.kt                       │   │
│  │  - Construye envelopes SOAP 1.1                     │   │
│  │  - Ejecuta peticiones HTTP con OkHttp               │   │
│  │  - Parsea respuestas XML con XmlPullParser          │   │
│  │  - Usa Dispatchers.IO para operaciones de red      │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │           ConversionResponse.kt                      │   │
│  │  - Data class: modelo de datos de respuesta          │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

### Componente: MainActivity.kt

**Propósito**: Punto de entrada de la aplicación Android.

**Funcionalidades**:
- Hereda de `ComponentActivity` (la clase base para Compose)
- En el método `onCreate()` define el contenido con `setContent`
- Establece el `MaterialTheme` que aplica los colores y tipografía de Material Design 3
- El Surface(fillMaxSize) asegura que el contenido ocupe toda la pantalla

**Código Relevante**:
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConversionScreen()  // Llama al composable principal
                }
            }
        }
    }
}
```

---

### Componente: ConversionScreen (Composable)

**Propósito**: Construye toda la interfaz de usuario de forma declarativa.

**Funcionalidades**:
- **Dropdown de Categoría**: Permite seleccionar entre Masa, Longitud, Temperatura
- **Dropdowns de Unidades**: Dos listas desplegables para seleccionar unidad origen y destino
- **Campo de Texto Numérico**: Input para el valor a convertir
- **Botón Convertir**: Ejecuta la conversión, muestra loading state
- **Cards de Resultado**: Muestra el resultado exitoso (verde) o error (rojo)

**Estados de UI**:
- `Idle`: Estado inicial, sin acción
- `Loading`: Operación en progreso (muestra spinner)
- `Success`: Conversión exitosa (muestra resultado)
- `Error`: Fallo en la comunicación (muestra mensaje de error)

**Componentes de Material 3 utilizados**:
- `ExposedDropdownMenuBox`: Menú desplegable moderno
- `OutlinedTextField`: Campos de texto con borde
- `Button`: Botón principal
- `Card`: Contenedor visual para resultados
- `CircularProgressIndicator`: Indicador de carga

---

### Componente: ConversionViewModel.kt

**Propósito**: Gestor de estado centralizado que conecta la UI con la lógica de negocio.

**Variables de Estado**:
- `selectedCategory`: Categoría actual ("Mass", "Length", "Temperature")
- `inputValue`: Valor textual del input del usuario
- `fromUnit` / `toUnit`: Unidades seleccionadas
- `uiState`: Estado reactivo de la UI (sealed class)

**Mapa de Unidades**:
```kotlin
private val unitsMap = mapOf(
    "Mass" to listOf("KILOGRAM", "GRAM", "POUND", "OUNCE"),
    "Length" to listOf("METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH"),
    "Temperature" to listOf("CELSIUS", "FAHRENHEIT", "KELVIN")
)
```

**Función convert()**:
1. Valida que el input sea numérico
2. Cambia estado a `Loading`
3. Lanza corrutina con `viewModelScope.launch`
4. Llama al repositorio de forma asíncrona
5. Actualiza el estado según el resultado (Success o Error)

---

### Componente: SoapRepository.kt

**Propósito**: Maneja toda la comunicación de red con el servidor SOAP.

**URL del Servicio**:
```kotlin
private val baseUrl = "http://10.0.2.2:8080/ConversionService.svc"
```
**Nota**: `10.0.2.2` es la IP especial del emulador de Android que apunta al `localhost` de la máquina host.

**Función convert()** - Flujo completo:

1. **Construcción del Envelope SOAP**:
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
     <soap:Body>
       <convertMass xmlns="http://ws.grupo3.edu.ec/">
         <value>1.0</value>
         <fromUnit>KILOGRAM</fromUnit>
         <toUnit>POUND</toUnit>
       </convertMass>
     </soap:Body>
   </soap:Envelope>
   ```

2. **Petición HTTP con OkHttp**:
   - Método: POST
   - Content-Type: text/xml; charset=utf-8
   - Header SOAPAction obligatorio según el backend

3. **Ejecución en Hilo Secundario**:
   - Usa `withContext(Dispatchers.IO)` para no bloquear la UI
   - Evita `NetworkOnMainThreadException`

4. **Parseo de Respuesta**:
   - Utiliza `XmlPullParser` (estándar Android, sin librerías externas)
   - Itera sobre las etiquetas del XML
   - Extrae: Category, FromUnit, ToUnit, InputValue, ResultValue

5. **Manejo de Errores**:
   - Captura excepciones y las envuelve en `Result.failure()`
   - El ViewModel decide cómo mostrar el error al usuario

---

### Componente: ConversionResponse.kt

**Propósito**: Modelo de datos inmutable para representar la respuesta del servicio.

```kotlin
data class ConversionResponse(
    val category: String = "",
    val fromUnit: String = "",
    val toUnit: String = "",
    val inputValue: Double = 0.0,
    val resultValue: Double = 0.0
)
```

**Uso**: Se crea en el repositorio tras parsear el XML y se pasa al ViewModel para mostrar en la UI.

---

## Configuración del Backend

Para que la aplicación funcione correctamente, el servidor SOAP debe estar ejecutándose en:

- **URL**: `http://localhost:8080/ConversionService.svc`
- **IP del emulador**: `10.0.2.2:8080`
- **IP del dispositivo físico**: `<IP_LOCAL>:8080`

### Métodos SOAP Esperados

La aplicación envía llamadas a estos métodos:

| Categoría | Método SOAP |
|-----------|-------------|
| Mass | `convertMass` |
| Length | `convertLength` |
| Temperature | `convertTemperature` |

### Estructura de Respuesta Esperada

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <convertMassResponse xmlns="http://ws.grupo3.edu.ec/">
      <convertMassResult>
        <Category>Mass</Category>
        <FromUnit>KILOGRAM</FromUnit>
        <ToUnit>POUND</ToUnit>
        <InputValue>1.0</InputValue>
        <ResultValue>2.20462</ResultValue>
      </convertMassResult>
    </convertMassResponse>
  </soap:Body>
</soap:Envelope>
```

---

## Solución de Problemas Comunes

### Error: "Plugin not found"

**Causa**: No se configuraron los repositorios de plugins.

**Solución**: El proyecto ya incluye `settings.gradle.kts` con los repositorios necesarios (Google, Maven Central). Si ocurre, verificar conexión a internet.

### Error: "android.useAndroidX property is not enabled"

**Causa**: Falta la propiedad en `gradle.properties`.

**Solución**: El proyecto ya incluye:
```properties
android.useAndroidX=true
android.enableJetifier=true
```

### Error: "mipmap/ic_launcher not found"

**Causa**: El Manifiesto referenciaba un icono que no existe.

**Solución**: Se eliminó la referencia del `AndroidManifest.xml`. La app usará el icono por defecto.

### Error: "NetworkOnMainThreadException"

**Causa**: Las operaciones de red se ejecutan en el hilo principal.

**Solución**: El código ya usa `Dispatchers.IO` en el `SoapRepository`. Verificar que no se esté ejecutando código de red en el hilo principal.

### Error: "Gradle Daemon stopped (JVM thrashing)"

**Causa**: Memoria insuficiente para Gradle.

**Solución**: En `gradle.properties` aumentar memoria:
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
```

### Error: "Connection refused" al hacer conversión

**Causas posibles**:
1. El servidor SOAP no está ejecutándose
2. La IP es incorrecta (10.0.2.2 para emulador, IP local para dispositivo físico)
3. El firewall bloquea las conexiones

**Solución**:
- Verificar que el servidor esté corriendo: `curl http://localhost:8080`
- Para emulador usar `10.0.2.2`
- Para dispositivo físico, cambiar la URL en `SoapRepository.kt` a la IP de la PC

### Error: "Cleartext traffic not permitted"

**Causa**: Android bloquea HTTP por defecto desde Android 9.

**Solución**: Ya está habilitado en el Manifiesto:
```xml
android:usesCleartextTraffic="true"
```

---

## Personalización

### Cambiar la IP del Servidor

Editar `app/src/main/java/ec/edu/grupo3/mobile/data/SoapRepository.kt`:

```kotlin
// Para emulador (localhost del host)
private val baseUrl = "http://10.0.2.2:8080/ConversionService.svc"

// Para dispositivo físico (reemplazar con tu IP local)
// private val baseUrl = "http://192.168.1.X:8080/ConversionService.svc"
```

### Agregar Nuevas Categorías

1. **En SoapRepository.kt**: Agregar el método SOAP entsprechend
2. **En ConversionViewModel.kt**: Agregar al `unitsMap`
3. **En MainActivity.kt**: Agregar al dropdown de categorías

### Cambiar Colores de la UI

Editar el `MaterialTheme` en `MainActivity.kt` para usar un esquema de colores diferente.

---

## Mejores Prácticas Implementadas

1. **Seguridad de Hilos**: Todas las operaciones de red usan `Dispatchers.IO`
2. **Manejo de Errores**: Try-catch con Result tipo funcional
3. **UX Reactiva**: Loading states, botones deshabilitados durante carga
4. **Validación de Input**: Validación de números antes de enviar
5. **Separación de Responsabilidades**: Cada clase tiene una única función
6. **Inmutabilidad**: Uso de `data class` y estados inmutables
7. **Compose Moderno**: Sin XML, 100% Kotlin declarativo

---

## Información del Build

- **Versión de Gradle**: 9.0.0
- **Android Gradle Plugin**: 8.5.0
- **Kotlin**: 1.9.24
- **Compose Compiler**: 1.5.14
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

---

## Licencia

Este proyecto fue desarrollado como parte de un trabajo académico de arquitectura de software.

---

## Autores

- Desarrollado por: [Tu Nombre]
- Curso: Arquitectura de Software
- Fecha: Mayo 2026