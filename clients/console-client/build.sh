#!/bin/bash

# Script para compilar el cliente de consola
# Usa el plugin jaxws-maven-plugin de Maven para generar los stubs

echo "🔧 Compilando cliente de consola..."
echo "📍 WSDL: http://209.145.48.25:8081/ROOT/Conversion?wsdl"
echo ""

# Verificar que el servidor VPS esté disponible
if ! curl -s --head "http://209.145.48.25:8081/ROOT/Conversion?wsdl" 2>/dev/null | head -n 1 | grep "HTTP" > /dev/null; then
    echo "❌ Error: El servidor VPS no está disponible"
    echo "   Asegúrate de que el servidor SOAP esté corriendo en:"
    echo "   http://209.145.48.25:8081/ROOT/Conversion"
    exit 1
fi

# Compilar con Maven (el plugin wsimport generará los stubs automáticamente)
echo "📦 Compilando con Maven (wsimport se ejecuta automáticamente)..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Cliente compilado exitosamente"
    echo "📦 JAR ejecutable: target/conversion-console-client.jar"
    echo ""
    echo "🚀 Para ejecutar el cliente:"
    echo "   java -jar target/conversion-console-client.jar"
else
    echo "❌ Error al compilar el cliente"
    exit 1
fi