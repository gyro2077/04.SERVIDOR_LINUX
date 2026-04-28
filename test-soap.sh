#!/bin/bash

# Script para probar peticiones SOAP

ENDPOINT="http://localhost:8080/04.SERVIDOR/conversion"
SOAP_EXAMPLES_DIR="soap-examples"

echo "🧪 Probando servicio SOAP de conversión..."
echo "📍 Endpoint: $ENDPOINT"
echo ""

# Verificar que el directorio de ejemplos exista
if [ ! -d "$SOAP_EXAMPLES_DIR" ]; then
    echo "❌ Directorio $SOAP_EXAMPLES_DIR no encontrado"
    exit 1
fi

# Probar conversión de masa
echo "📊 Probando conversión de masa (5kg a libras)..."
curl -X POST "$ENDPOINT" \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d @"$SOAP_EXAMPLES_DIR/mass-conversion.xml" \
  --silent --show-error
echo ""
echo ""

# Probar conversión de longitud
echo "📏 Probando conversión de longitud (100cm a metros)..."
curl -X POST "$ENDPOINT" \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d @"$SOAP_EXAMPLES_DIR/length-conversion.xml" \
  --silent --show-error
echo ""
echo ""

# Probar conversión de temperatura
echo "🌡️  Probando conversión de temperatura (25°C a Fahrenheit)..."
curl -X POST "$ENDPOINT" \
  -H "Content-Type: text/xml" \
  -H "SOAPAction: \"\"" \
  -d @"$SOAP_EXAMPLES_DIR/temperature-conversion.xml" \
  --silent --show-error
echo ""
echo ""

echo "✅ Pruebas completadas!"
