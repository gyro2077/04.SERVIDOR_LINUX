# SOAP Unit Conversion Service

Servicio web SOAP para conversión de unidades (masa, longitud, temperatura) implementado con Jakarta EE y desplegado en Payara Server 7.

## 📋 Descripción

Este proyecto proporciona un servicio web REST/SOAP para realizar conversiones entre diferentes unidades de medida:

- **Masa**: Kilogramos, Gramos, Libras, Onzas
- **Longitud**: Metros, Centímetros, Pies, Pulgadas
- **Temperatura**: Celsius, Fahrenheit, Kelvin

## 🛠️ Requisitos

- Java 21 (OpenJDK)
- Maven 3.x
- Payara Server 7
- curl (para pruebas)

## 🚀 Inicio Rápido

### Scripts de Automatización

El proyecto incluye scripts para facilitar el despliegue y las pruebas:

```bash
# Desplegar el proyecto (compila + despliega)
./deploy.sh

# Probar el servicio SOAP
./test-soap.sh
```

### 1. Iniciar Payara Server

El servicio Payara está configurado como servicio systemd:

```bash
# Iniciar el servicio
sudo systemctl start payara.service

# Verificar estado
sudo systemctl status payara.service

# Detener el servicio
sudo systemctl stop payara.service
```

### 2. Compilar el Proyecto

```bash
# Limpiar y compilar
mvn clean package

# El archivo WAR se generará en: target/04.SERVIDOR-1.0-SNAPSHOT.war
```

### 3. Desplegar en Payara

```bash
# Desplegar la aplicación
/opt/payara7/bin/asadmin deploy --force=true --name=04.SERVIDOR target/04.SERVIDOR-1.0-SNAPSHOT.war

# Verificar aplicaciones desplegadas
/opt/payara7/bin/asadmin list-applications

# Redesplegar (si ya existe)
/opt/payara7/bin/asadmin redeploy --name=04.SERVIDOR target/04.SERVIDOR-1.0-SNAPSHOT.war

# Desinstalar
/opt/payara7/bin/asadmin undeploy 04.SERVIDOR
```

## 🌐 Endpoints

- **URL Base**: `http://localhost:8080/04.SERVIDOR/`
- **WSDL**: `http://localhost:8080/04.SERVIDOR/conversion?wsdl`
- **Endpoint SOAP**: `http://localhost:8080/04.SERVIDOR/conversion`

## 📡 Peticiones SOAP

### Estructura General

Las peticiones SOAP deben enviarse al endpoint `http://localhost:8080/04.SERVIDOR/conversion` con los siguientes headers:

```
Content-Type: text/xml
SOAPAction: ""
```

### Ejemplo 1: Conversión de Masa

**Convertir 5 kilogramos a libras:**

```bash
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertMass xmlns:ns2="http://ws.grupo3.edu.ec/">
      <value>5.0</value>
      <fromUnit>KILOGRAM</fromUnit>
      <toUnit>POUND</toUnit>
    </ns2:convertMass>
  </soap:Body>
</soap:Envelope>'
```

**Respuesta esperada:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
  <S:Body>
    <ns2:convertMassResponse xmlns:ns2="http://ws.grupo3.edu.ec/">
      <return>
        <category>MASS</category>
        <fromUnit>KILOGRAM</fromUnit>
        <toUnit>POUND</toUnit>
        <inputValue>5.0</inputValue>
        <resultValue>11.023113109243878</resultValue>
      </return>
    </ns2:convertMassResponse>
  </S:Body>
</S:Envelope>
```

### Ejemplo 2: Conversión de Longitud

**Convertir 100 centímetros a metros:**

```bash
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertLength xmlns:ns2="http://ws.grupo3.edu.ec/">
      <value>100.0</value>
      <fromUnit>CENTIMETER</fromUnit>
      <toUnit>METER</toUnit>
    </ns2:convertLength>
  </soap:Body>
</soap:Envelope>'
```

### Ejemplo 3: Conversión de Temperatura

**Convertir 25°C a Fahrenheit:**

```bash
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertTemperature xmlns:ns2="http://ws.grupo3.edu.ec/">
      <value>25.0</value>
      <fromUnit>CELSIUS</fromUnit>
      <toUnit>FAHRENHEIT</toUnit>
    </ns2:convertTemperature>
  </soap:Body>
</soap:Envelope>'
```

## 📚 Unidades Disponibles

### Masa (MassUnit)
- `KILOGRAM` - Kilogramos
- `GRAM` - Gramos
- `POUND` - Libras
- `OUNCE` - Onzas

### Longitud (LengthUnit)
- `METER` - Metros
- `KILOMETER` - Kilómetros
- `CENTIMETER` - Centímetros
- `MILE` - Millas
- `YARD` - Yardas
- `FOOT` - Pies
- `INCH` - Pulgadas

### Temperatura (TemperatureUnit)
- `CELSIUS` - Celsius
- `FAHRENHEIT` - Fahrenheit
- `KELVIN` - Kelvin

## 🧪 Herramientas de Prueba

### Usando SoapUI

1. Descarga SoapUI desde https://www.soapui.org/
2. Crea un nuevo proyecto SOAP
3. Importa el WSDL: `http://localhost:8080/04.SERVIDOR/conversion?wsdl`
4. SoapUI generará automáticamente las peticiones para cada operación

### Usando Postman

1. Crea una nueva solicitud POST
2. URL: `http://localhost:8080/04.SERVIDOR/conversion`
3. Headers:
   - `Content-Type: text/xml`
   - `SOAPAction: ""`
4. Body: Selecciona "raw" y "XML" y pega el XML SOAP

### Usando Python

```python
import requests

url = "http://localhost:8080/04.SERVIDOR/conversion"
headers = {
    "Content-Type": "text/xml",
    "SOAPAction": ""
}

soap_body = """<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertMass xmlns:ns2="http://ws.grupo3.edu.ec/">
      <value>5.0</value>
      <fromUnit>KILOGRAM</fromUnit>
      <toUnit>POUND</toUnit>
    </ns2:convertMass>
  </soap:Body>
</soap:Envelope>"""

response = requests.post(url, headers=headers, data=soap_body)
print(response.text)
```

## 🐛 Debugging

### Cambios Necesarios para Debugging

Para habilitar el debugging remoto en Payara Server, necesitas configurar las opciones de JVM:

```bash
# Agregar opciones de debugging al dominio
/opt/payara7/bin/asadmin create-jvm-options -Xdebug
/opt/payara7/bin/asadmin create-jvm-options -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005

# Reiniciar el dominio para aplicar los cambios
/opt/payara7/bin/asadmin restart-domain domain1
```

### Configuración de VSCode

El proyecto incluye un archivo `.vscode/launch.json` preconfigurado para debugging:

1. Presiona `F5` o ve a "Run and Debug"
2. Selecciona "Attach to Payara Server"
3. Coloca breakpoints en tu código
4. El debugger se conectará a Payara en el puerto 5005

### Habilitar Debugging en Payara

```bash
# Habilitar debugging en el dominio
/opt/payara7/bin/asadmin create-jvm-options -Xdebug
/opt/payara7/bin/asadmin create-jvm-options -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005

# Reiniciar el dominio
/opt/payara7/bin/asadmin restart-domain domain1
```

### Ver Logs en Tiempo Real

```bash
# Ver logs del servidor
tail -f /opt/payara7/glassfish/domains/domain1/logs/server.log

# Ver solo errores
tail -f /opt/payara7/glassfish/domains/domain1/logs/server.log | grep ERROR

# Ver logs de tu aplicación
tail -f /opt/payara7/glassfish/domains/domain1/logs/server.log | grep 04.SERVIDOR
```

### Debugging desde IDE (IntelliJ IDEA)

1. Configura un Remote Debug en IntelliJ:
   - Host: `localhost`
   - Port: `5005`
2. Asegúrate de que Payara esté corriendo con debugging habilitado
3. Coloca breakpoints en tu código
4. Inicia el Remote Debug desde IntelliJ

### Debugging desde VSCode

1. Instala la extensión "Java Debugger"
2. Crea un archivo `.vscode/launch.json`:
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Attach to Payara",
      "request": "attach",
      "hostName": "localhost",
      "port": 5005
    }
  ]
}
```

## 🧪 Testing

### Scripts de Prueba

El proyecto incluye scripts y ejemplos para facilitar las pruebas:

```bash
# Ejecutar todas las pruebas SOAP
./test-soap.sh

# Probar manualmente con curl
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d @soap-examples/mass-conversion.xml
```

### Archivos de Ejemplo

El directorio `soap-examples/` contiene archivos XML con ejemplos de peticiones:

- `mass-conversion.xml` - Ejemplo de conversión de masa
- `length-conversion.xml` - Ejemplo de conversión de longitud
- `temperature-conversion.xml` - Ejemplo de conversión de temperatura

### Pruebas Unitarias

El proyecto no incluye pruebas unitarias actualmente. Para agregarlas:

1. Agrega la dependencia de JUnit en `pom.xml`:
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

2. Crea pruebas en `src/test/java/`

### Pruebas de Integración SOAP

Crea un archivo de prueba `src/test/java/ec/edu/grupo3/ws/ConversionSoapWSTest.java`:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConversionSoapWSTest {
    
    @Test
    public void testMassConversion() {
        // Implementar prueba de conversión de masa
        // Puedes usar un cliente SOAP o mockear el servicio
    }
    
    @Test
    public void testLengthConversion() {
        // Implementar prueba de conversión de longitud
    }
    
    @Test
    public void testTemperatureConversion() {
        // Implementar prueba de conversión de temperatura
    }
}
```

## 🔧 Configuración Adicional

### Cambiar Puerto de Payara

```bash
# Cambiar puerto HTTP (default: 8080)
/opt/payara7/bin/asadmin set server-config.network-config.network-listeners.network-listener.http-listener-1.port=9090

# Cambiar puerto HTTPS (default: 8181)
/opt/payara7/bin/asadmin set server-config.network-config.network-listeners.network-listener.http-listener-2.port=9443

# Cambiar puerto de administración (default: 4848)
/opt/payara7/bin/asadmin set server-config.network-config.network-listeners.network-listener.admin-listener.port=5858
```

### Ver Configuración Actual

```bash
# Ver todos los puertos
/opt/payara7/bin/asadmin get server-config.network-config.network-listeners.network-listener.*.port

# Ver memoria configurada
/opt/payara7/bin/asadmin get server-config.java-config.*-options
```

## 📝 Notas de Implementación

### Inyección de Dependencias

El proyecto originalmente usaba CDI (`@Inject`) para la inyección de dependencias, pero JAX-WS no soporta directamente esta característica. Se modificó el código para usar instanciación directa:

**Antes:**
```java
@Inject
private MassConverterModel massConverterModel;
```

**Después:**
```java
private final MassConverterModel massConverterModel = new MassConverterModel();
```

Este cambio asegura que el servicio funcione correctamente sin dependencias complejas de CDI.

## 📝 Estructura del Proyecto

```
04.SERVIDOR/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ec/edu/grupo3/
│   │   │       ├── model/          # Modelos de conversión
│   │   │       ├── view/           # DTOs de respuesta
│   │   │       └── ws/             # Web Service SOAP
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml # Configuración JPA
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           ├── beans.xml      # Configuración CDI
│   │           ├── sun-jaxws.xml  # Configuración JAX-WS
│   │           └── web.xml        # Configuración web
│   └── test/                       # Pruebas unitarias
├── soap-examples/                  # Ejemplos de peticiones SOAP
│   ├── mass-conversion.xml
│   ├── length-conversion.xml
│   └── temperature-conversion.xml
├── .vscode/                        # Configuración VSCode
│   ├── launch.json                 # Configuración de debugging
│   └── settings.json
├── deploy.sh                       # Script de despliegue
├── test-soap.sh                    # Script de pruebas
├── pom.xml                         # Configuración Maven
└── README.md                       # Este archivo
```

## 🚨 Solución de Problemas

### Error: "404 Not Found" en endpoint SOAP

- Verifica que la aplicación esté desplegada: `/opt/payara7/bin/asadmin list-applications`
- El endpoint correcto es `/conversion`, no `/soap/conversion`
- Revisa los logs: `tail -f /opt/payara7/glassfish/domains/domain1/logs/server.log`

### Error: "Connection refused"

- Verifica que Payara esté corriendo: `sudo systemctl status payara.service`
- Verifica el puerto: `netstat -tlnp | grep 8080`

### Error de compilación

- Verifica que Java 21 esté instalado: `java -version`
- Limpia y recompila: `mvn clean install`

## 📚 Recursos Adicionales

- [Jakarta EE Documentation](https://jakarta.ee/)
- [Payara Server Documentation](https://docs.payara.fish/)
- [JAX-WS Documentation](https://eclipse-ee4j.github.io/metro-jax-ws/)
- [SOAP Tutorial](https://www.w3schools.com/xml/xml_soap.asp)

## 👥 Autores

- Grupo 3 - Arquitectura de Software

## 📄 Licencia

Este proyecto es parte del curso de Arquitectura de Software.