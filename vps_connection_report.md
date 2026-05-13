# Reporte de Confirmación de Conexión al VPS

**Fecha:** 13 de Mayo de 2026  
**Servidor VPS Objetivo:** `http://209.145.48.25:8081/ROOT`  
**Estado de la Conexión:** **¡CONEXIÓN EXITOSA! ✅**

---

## 🚀 Resumen de Verificación

Se realizaron pruebas de conexión y peticiones HTTP POST (SOAP) directamente desde esta máquina local hacia el servidor VPS público en la dirección IP `209.145.48.25` por el puerto `8081`. 

Ambos servicios web (`Login` y `Conversion`) se encuentran plenamente accesibles, procesando las solicitudes correctamente y retornando las respuestas esperadas en formato XML.

---

## 🛠️ Detalle de las Pruebas Realizadas

### 1. Servicio de Autenticación (`/Login`)
Se envió una petición de inicio de sesión con las credenciales oficiales (`MONSTER` / `MONSTER9`).

**Petición enviada:**
```bash
curl -s -X POST "http://209.145.48.25:8081/ROOT/Login" \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:log="http://ws.grupo3.edu.ec/">
   <soap:Body>
      <log:login>
         <username>MONSTER</username>
         <password>MONSTER9</password>
      </log:login>
   </soap:Body>
</soap:Envelope>'
```

**Respuesta recibida del VPS:**
```xml
<?xml version='1.0' encoding='UTF-8'?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <ns2:loginResponse xmlns:ns2="http://ws.grupo3.edu.ec/">
         <return>
            <success>true</success>
            <token>TU9OU1RFUjoxNzc4Njc3MDM0ODMy</token>
            <message>Login exitoso</message>
         </return>
      </ns2:loginResponse>
   </S:Body>
</S:Envelope>
```
* **Resultado:** El servidor validó las credenciales exitosamente y generó el token de autenticación válido.

---

### 2. Servicio de Conversión (`/Conversion`)
Utilizando el token obtenido en el paso anterior, se solicitó la conversión de **10 KILOGRAMOS a LIBRAS**.

**Petición enviada:**
```bash
curl -s -X POST "http://209.145.48.25:8081/ROOT/Conversion" \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:con="http://ws.grupo3.edu.ec/">
   <soap:Body>
      <con:convertMass>
         <token>TU9OU1RFUjoxNzc4Njc3MDM0ODMy</token>
         <value>10</value>
         <fromUnit>KILOGRAM</fromUnit>
         <toUnit>POUND</toUnit>
      </con:convertMass>
   </soap:Body>
</soap:Envelope>'
```

**Respuesta recibida del VPS:**
```xml
<?xml version='1.0' encoding='UTF-8'?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
   <S:Body>
      <ns2:convertMassResponse xmlns:ns2="http://ws.grupo3.edu.ec/">
         <return>
            <category>MASS</category>
            <fromUnit>KILOGRAM</fromUnit>
            <toUnit>POUND</toUnit>
            <inputValue>10.0</inputValue>
            <resultValue>22.046226218487757</resultValue>
            <message>OK</message>
         </return>
      </ns2:convertMassResponse>
   </S:Body>
</S:Envelope>
```
* **Resultado:** La conversión se calculó de forma precisa y el servidor respondió de inmediato con el valor correcto (`22.046226...` libras).

---

## 🎯 Conclusión
La comunicación en red desde esta computadora hacia el VPS funciona a la perfección. No existen bloqueos de firewall de salida ni problemas de enrutamiento hacia la IP `209.145.48.25` en el puerto `8081`. 

Se ha creado además el script ejecutable `test-vps-endpoints.sh` en este directorio para que puedas ejecutar el set completo de pruebas contra el VPS en cualquier momento.
