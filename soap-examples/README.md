# Ejemplos de Peticiones SOAP

Este directorio contiene ejemplos de peticiones SOAP para probar el servicio de conversión de unidades.

## 📁 Archivos Disponibles

### mass-conversion.xml
Ejemplo de conversión de masa: 5 kilogramos a libras

### length-conversion.xml  
Ejemplo de conversión de longitud: 100 centímetros a metros

### temperature-conversion.xml
Ejemplo de conversión de temperatura: 25°C a Fahrenheit

## 🚀 Cómo Usar

### Usar curl directamente

```bash
# Conversión de masa
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d @soap-examples/mass-conversion.xml

# Conversión de longitud
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d @soap-examples/length-conversion.xml

# Conversión de temperatura
curl -X POST http://localhost:8080/04.SERVIDOR/conversion \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d @soap-examples/temperature-conversion.xml
```

### Usar el script de pruebas

```bash
./test-soap.sh
```

### Modificar los ejemplos

Puedes modificar los valores en los archivos XML para probar diferentes conversiones:

```xml
<value>10.0</value>              <!-- Valor a convertir -->
<fromUnit>KILOGRAM</fromUnit>    <!-- Unidad de origen -->
<toUnit>POUND</toUnit>           <!-- Unidad de destino -->
```

## 📚 Unidades Disponibles

### Masa
- KILOGRAM
- GRAM
- POUND
- OUNCE

### Longitud
- METER
- KILOMETER
- CENTIMETER
- MILE
- YARD
- FOOT
- INCH

### Temperatura
- CELSIUS
- FAHRENHEIT
- KELVIN

## 🧪 Crear Nuevos Ejemplos

Para crear un nuevo ejemplo, copia uno de los archivos existentes y modifica los valores:

```bash
cp soap-examples/mass-conversion.xml soap-examples/my-test.xml
# Edita my-test.xml con tus valores
```

## 📊 Formato de Respuesta

Todas las respuestas siguen este formato:

```xml
<return>
  <category>TIPO_DE_CONVERSION</category>
  <fromUnit>UNIDAD_ORIGEN</fromUnit>
  <toUnit>UNIDAD_DESTINO</toUnit>
  <inputValue>VALOR_ORIGINAL</inputValue>
  <resultValue>VALOR_CONVERTIDO</resultValue>
</return>
```
