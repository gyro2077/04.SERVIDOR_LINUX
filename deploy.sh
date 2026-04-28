#!/bin/bash

# Script de despliegue para 04.SERVIDOR

echo "🚀 Iniciando despliegue de 04.SERVIDOR..."

# Verificar que Maven esté instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado"
    exit 1
fi

# Verificar que Payara esté instalado
if [ ! -d "/opt/payara7" ]; then
    echo "❌ Payara no está instalado en /opt/payara7"
    exit 1
fi

# Compilar el proyecto
echo "📦 Compilando proyecto..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar el proyecto"
    exit 1
fi

# Verificar que el archivo WAR exista
if [ ! -f "target/04.SERVIDOR-1.0-SNAPSHOT.war" ]; then
    echo "❌ No se encontró el archivo WAR"
    exit 1
fi

# Desplegar en Payara
echo "🌐 Desplegando en Payara Server..."
/opt/payara7/bin/asadmin deploy --force=true --name=04.SERVIDOR target/04.SERVIDOR-1.0-SNAPSHOT.war

if [ $? -eq 0 ]; then
    echo "✅ Despliegue exitoso!"
    echo "📍 URL: http://localhost:8080/04.SERVIDOR/"
    echo "📡 WSDL: http://localhost:8080/04.SERVIDOR/conversion?wsdl"
else
    echo "❌ Error al desplegar"
    exit 1
fi
