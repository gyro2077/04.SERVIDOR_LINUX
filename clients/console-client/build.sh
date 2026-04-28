#!/bin/bash

# Script para compilar el cliente de consola
# Usa el plugin jaxws-maven-plugin de Maven para generar los stubs

echo "🔧 Compilando cliente de consola..."
echo "📍 WSDL: http://localhost:8080/04.SERVIDOR/conversion?wsdl"
echo ""

# Verificar que el servidor esté corriendo
if ! curl -s --head "http://localhost:8080/04.SERVIDOR/conversion?wsdl" 2>/dev/null | head -n 1 | grep "HTTP" > /dev/null; then
    echo "❌ Error: El servidor SOAP no está disponible"
    echo "   Asegúrate de que Payara esté corriendo:"
    echo "   sudo systemctl start payara.service"
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