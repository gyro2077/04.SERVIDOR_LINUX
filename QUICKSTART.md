# Guía de Inicio Rápido

## 🚀 Despliegue en 3 Pasos

### 1. Iniciar Payara Server

```bash
sudo systemctl start payara.service
sudo systemctl status payara.service
```

### 2. Compilar y Desplegar

```bash
# Opción A: Usar el script automático
./deploy.sh

# Opción B: Manualmente
mvn clean package
/opt/payara7/bin/asadmin deploy --force=true --name=04.SERVIDOR target/04.SERVIDOR-1.0-SNAPSHOT.war
```

### 3. Probar el Servicio

```bash
# Opción A: Usar el script de pruebas
./test-soap.sh

# Opción B: Prueba manual
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

## 📍 URLs Importantes

- **Aplicación**: http://localhost:8080/04.SERVIDOR/
- **WSDL**: http://localhost:8080/04.SERVIDOR/conversion?wsdl
- **Endpoint SOAP**: http://localhost:8080/04.SERVIDOR/conversion

## 🛠️ Comandos Útiles

### Verificar Estado

```bash
# Ver aplicaciones desplegadas
/opt/payara7/bin/asadmin list-applications

# Ver logs en tiempo real
tail -f /opt/payara7/glassfish/domains/domain1/logs/server.log

# Ver puertos en uso
netstat -tlnp | grep 8080
```

### Gestión de Aplicación

```bash
# Redesplegar
/opt/payara7/bin/asadmin redeploy --name=04.SERVIDOR target/04.SERVIDOR-1.0-SNAPSHOT.war

# Desinstalar
/opt/payara7/bin/asadmin undeploy 04.SERVIDOR

# Reiniciar aplicación
/opt/payara7/bin/asadmin restart 04.SERVIDOR
```

### Gestión de Servidor

```bash
# Reiniciar Payara
sudo systemctl restart payara.service

# Detener Payara
sudo systemctl stop payara.service

# Ver configuración de puertos
/opt/payara7/bin/asadmin get server-config.network-config.network-listeners.network-listener.*.port
```

## 🧪 Pruebas Rápidas

### Prueba de Masa
```bash
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d @soap-examples/mass-conversion.xml
```

### Prueba de Longitud
```bash
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d @soap-examples/length-conversion.xml
```

### Prueba de Temperatura
```bash
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d @soap-examples/temperature-conversion.xml
```

## 🐛 Debugging

### Habilitar Debugging Remoto

```bash
/opt/payara7/bin/asadmin create-jvm-options -Xdebug
/opt/payara7/bin/asadmin create-jvm-options -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
/opt/payara7/bin/asadmin restart-domain domain1
```

### Conectar desde VSCode

1. Presiona `F5`
2. Selecciona "Attach to Payara Server"
3. Coloca breakpoints en tu código

## 📚 Documentación Adicional

- [README.md](README.md) - Documentación completa
- [DEBUGGING.md](DEBUGGING.md) - Guía detallada de debugging
- [soap-examples/README.md](soap-examples/README.md) - Ejemplos de peticiones SOAP

## ⚠️ Solución de Problemas

### Error: "Connection refused"
```bash
# Verificar que Payara esté corriendo
sudo systemctl status payara.service

# Iniciar si está detenido
sudo systemctl start payara.service
```

### Error: "404 Not Found"
```bash
# Verificar que la aplicación esté desplegada
/opt/payara7/bin/asadmin list-applications

# Redesplegar si es necesario
./deploy.sh
```

### Error de compilación
```bash
# Limpiar y recompilar
mvn clean install

# Verificar versión de Java
java -version
```

## 🎯 Checklist de Despliegue

- [ ] Java 21 instalado
- [ ] Payara Server 7 corriendo
- [ ] Proyecto compilado exitosamente
- [ ] Aplicación desplegada
- [ ] Servicio accesible en http://localhost:8080/04.SERVIDOR/
- [ ] WSDL accesible en http://localhost:8080/04.SERVIDOR/conversion?wsdl
- [ ] Pruebas SOAP funcionando

## 📞 Soporte

Si encuentras problemas:

1. Revisa los logs: `tail -f /opt/payara7/glassfish/domains/domain1/logs/server.log`
2. Verifica la documentación en [DEBUGGING.md](DEBUGGING.md)
3. Consulta la documentación completa en [README.md](README.md)
