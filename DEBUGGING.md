# Debugging y Pruebas - Guía Detallada

## 🐛 Configuración de Debugging

### 1. Habilitar Debugging Remoto en Payara

```bash
# Paso 1: Agregar opciones de JVM para debugging
/opt/payara7/bin/asadmin create-jvm-options -Xdebug
/opt/payara7/bin/asadmin create-jvm-options -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005

# Paso 2: Verificar las opciones agregadas
/opt/payara7/bin/asadmin list-jvm-options | grep debug

# Paso 3: Reiniciar el dominio
/opt/payara7/bin/asadmin restart-domain domain1

# Paso 4: Verificar que el puerto esté abierto
netstat -tlnp | grep 5005
```

### 2. Configuración de VSCode

El archivo `.vscode/launch.json` ya está configurado. Para usarlo:

1. Abre el proyecto en VSCode
2. Presiona `F5` o ve a "Run and Debug"
3. Selecciona "Attach to Payara Server"
4. Coloca breakpoints en tu código
5. El debugger se conectará automáticamente

### 3. Configuración de IntelliJ IDEA

1. Ve a `Run` → `Edit Configurations`
2. Crea una nueva configuración "Remote JVM Debug"
3. Configura:
   - Host: `localhost`
   - Port: `5005`
   - Use module classpath: Selecciona tu módulo
4. Coloca breakpoints y presiona "Debug"

## 🧪 Pruebas SOAP

### Pruebas Manuales con curl

#### Conversión de Masa

```bash
# Kilogramos a Libras
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertMass xmlns:ns2="http://ws.grupo3.edu.ec/">
      <value>10.0</value>
      <fromUnit>KILOGRAM</fromUnit>
      <toUnit>POUND</toUnit>
    </ns2:convertMass>
  </soap:Body>
</soap:Envelope>'

# Gramos a Kilogramos
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertMass xmlns:ns2="http://ws.grupo3.edu.ec/">
      <value>1000.0</value>
      <fromUnit>GRAM</fromUnit>
      <toUnit>KILOGRAM</toUnit>
    </ns2:convertMass>
  </soap:Body>
</soap:Envelope>'
```

#### Conversión de Longitud

```bash
# Metros a Kilómetros
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertLength xmlns:ns2="http://ws.grupo3.edu.ec/">
      <value>1000.0</value>
      <fromUnit>METER</fromUnit>
      <toUnit>KILOMETER</toUnit>
    </ns2:convertLength>
  </soap:Body>
</soap:Envelope>'

# Millas a Kilómetros
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertLength xmlns:ns2="http://ws.grupo3.edu.ec/">
      <value>1.0</value>
      <fromUnit>MILE</fromUnit>
      <toUnit>KILOMETER</toUnit>
    </ns2:convertLength>
  </soap:Body>
</soap:Envelope>'
```

#### Conversión de Temperatura

```bash
# Celsius a Kelvin
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertTemperature xmlns:ns2="http://ws.grupo3.edu.ec/">
      <value>0.0</value>
      <fromUnit>CELSIUS</fromUnit>
      <toUnit>KELVIN</toUnit>
    </ns2:convertTemperature>
  </soap:Body>
</soap:Envelope>'

# Fahrenheit a Celsius
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <ns2:convertTemperature xmlns:ns2="http://ws.grupo3.edu.ec/">
      <value>32.0</value>
      <fromUnit>FAHRENHEIT</fromUnit>
      <toUnit>CELSIUS</toUnit>
    </ns2:convertTemperature>
  </soap:Body>
</soap:Envelope>'
```

### Pruebas Automatizadas

```bash
# Ejecutar todas las pruebas
./test-soap.sh

# Ver resultados formateados
./test-soap.sh | grep -A 5 "Probando"
```

## 🔍 Análisis de Logs

### Ver Logs en Tiempo Real

```bash
# Ver todos los logs
tail -f /opt/payara7/glassfish/domains/domain1/logs/server.log

# Ver solo errores
tail -f /opt/payara7/glassfish/domains/domain1/logs/server.log | grep -i "error\|exception"

# Ver logs de tu aplicación
tail -f /opt/payara7/glassfish/domains/domain1/logs/server.log | grep "04.SERVIDOR"

# Ver logs de JAX-WS
tail -f /opt/payara7/glassfish/domains/domain1/logs/server.log | grep -i "jaxws\|soap"
```

### Análisis de Errores Comunes

#### Error: "Cannot invoke... because this.xxxModel is null"

**Causa:** Las dependencias no se están inyectando correctamente.

**Solución:** Verifica que los modelos se estén instanciando directamente en el constructor o como campos finales.

#### Error: "404 Not Found"

**Causa:** El endpoint no está configurado correctamente.

**Solución:** 
- Verifica que la aplicación esté desplegada: `/opt/payara7/bin/asadmin list-applications`
- El endpoint correcto es `/conversion`, no `/soap/conversion`
- Revisa `sun-jaxws.xml` para verificar la configuración del endpoint

#### Error: "Connection refused"

**Causa:** Payara no está corriendo o el puerto está incorrecto.

**Solución:**
- Verifica que Payara esté corriendo: `sudo systemctl status payara.service`
- Verifica el puerto: `netstat -tlnp | grep 8080`

## 📊 Monitoreo de Rendimiento

### Ver Estadísticas de Payara

```bash
# Ver memoria usada
/opt/payara7/bin/asadmin get server.monitoring.server.memory

# Ver número de threads
/opt/payara7/bin/asadmin get server.monitoring.server.thread-count

# Ver uptime del servidor
/opt/payara7/bin/asadmin get server.monitoring.server.uptime-millis
```

### Ver Configuración de JVM

```bash
# Ver todas las opciones de JVM
/opt/payara7/bin/asadmin list-jvm-options

# Ver memoria heap configurada
/opt/payara7/bin/asadmin get server-config.java-config.jvm-options | grep -i "xmx\|xms"
```

## 🛠️ Solución de Problemas Avanzada

### Reiniciar Servicios Específicos

```bash
# Reiniciar solo la aplicación
/opt/payara7/bin/asadmin restart 04.SERVIDOR

# Reiniciar el dominio completo
/opt/payara7/bin/asadmin restart-domain domain1

# Recargar la aplicación sin reiniciar
/opt/payara7/bin/asadmin redeploy --name=04.SERVIDOR target/04.SERVIDOR-1.0-SNAPSHOT.war
```

### Ver Configuración de Red

```bash
# Ver todos los listeners de red
/opt/payara7/bin/asadmin get server-config.network-config.network-listeners.network-listener.*.port

# Ver configuración HTTP
/opt/payara7/bin/asadmin get server-config.network-config.protocols.protocol.http-listener-1.http.*
```

### Limpieza de Caché

```bash
# Limpiar caché de Payara
/opt/payara7/bin/asadmin stop-domain domain1
rm -rf /opt/payara7/glassfish/domains/domain1/generated/*
rm -rf /opt/payara7/glassfish/domains/domain1/applications/*
/opt/payara7/bin/asadmin start-domain domain1
```

## 📝 Notas de Desarrollo

### Hot Reload

Para desarrollo rápido, puedes usar hot reload:

1. Modifica el código
2. Compila: `mvn compile`
3. Redespliega: `./deploy.sh`

### Debugging de Métodos Específicos

Para debugear métodos específicos:

1. Coloca un breakpoint en el método que quieres debuggear
2. Inicia el debugger remoto
3. Haz una petición SOAP que active ese método
4. El debugger se detendrá en el breakpoint

### Variables de Entorno Útiles

```bash
# Ver variables de entorno de Payara
/opt/payara7/bin/asadmin get server.env

# Ver directorio de dominio
/opt/payara7/bin/asadmin get domains.domain.domain1
```

## 🚨 Errores Comunes y Soluciones

### OutOfMemoryError

```bash
# Aumentar memoria heap
/opt/payara7/bin/asadmin delete-jvm-options "-Xmx512m"
/opt/payara7/bin/asadmin create-jvm-options "-Xmx1024m"
/opt/payara7/bin/asadmin restart-domain domain1
```

### Permiso Denegado

```bash
# Verificar permisos del directorio de Payara
ls -la /opt/payara7/

# Asegurarse de que el usuario tenga permisos
sudo chown -R gyro:gyro /opt/payara7/
```

### Puerto ya en uso

```bash
# Ver qué proceso está usando el puerto
sudo lsof -i :8080

# Matar el proceso si es necesario
sudo kill -9 <PID>
```

## 📚 Recursos Adicionales

- [Payara Debugging Guide](https://docs.payara.fish/community/docs/5.2021.8/documentation/documentation-1-0.html#debugging)
- [JAX-WS Debugging](https://eclipse-ee4j.github.io/metro-jax-ws/guide/Debugging.html)
- [Java Remote Debugging](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/introclientissues005.html)
