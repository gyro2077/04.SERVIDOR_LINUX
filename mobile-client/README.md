# Mobile Client - React Native + Expo

Cliente moviil multiplataforma (iOS/Android) desarrollado con React Native y Expo SDK 55.

## Estructura del Proyecto

```
mobile-client/
├── src/
│   ├── app/
│   │   ├── _layout.tsx      # Configuracion de navegacion con Expo Router
│   │   └── index.tsx        # Pantalla principal de conversion
│   └── services/
│       └── soapService.ts   # Cliente SOAP con fetch nativo
├── package.json
└── tsconfig.json
```

## Requisitos

- Node.js 18+ y npm/yarn
- Expo CLI (`npx expo`)
- Dispositivo movil (iOS/Android) con Expo Go instalado, o emulador

## Instalacion

```bash
cd mobile-client
npm install
```

## Configuracion del Endpoint

Editar `src/services/soapService.ts` y actualizar la IP:

```typescript
const SOAP_ENDPOINT = 'http://192.168.100.171:8080/ConversionService.svc';
```

Para obtener tu IP local en Linux:
```bash
ip a show wlan0 | grep inet
# o
hostname -I | awk '{print $1}'
```

## Ejecucion

```bash
npx expo start
```

Escanear el codigo QR con la app Expo Go en tu dispositivo movil.

## Funcionalidades

- **Conversion de Masa**: KILOGRAM, GRAM, POUND, OUNCE
- **Conversion de Longitud**: METER, KILOMETER, CENTIMETER, MILE, YARD, FOOT, INCH
- **Conversion de Temperatura**: CELSIUS, FAHRENHEIT, KELVIN
- **Validacion de entrada numerica** con compatibilidad para teclado internacional
- **Selector horizontal tipo "pills"** para unidades (UX nativa en iOS y Android)
- **Indicador de carga** durante la peticion SOAP
- **Manejo de errores** con alertas nativas

## Arquitectura

- **soapService.ts**: Capa de servicios que construye el envelope SOAP y lo envia via `fetch`
- **index.tsx**: Componente principal con estado reactivo usando React Hooks
- **Expo Router**: Enrutamiento basado en archivos (file-based routing)

## Notas Importantes

1. No usar `localhost` en el endpoint - el dispositivo movil no puede resolverlo
2. El backend debe estar corriendo en la misma red Wi-Fi
3. El header `SOAPAction` es obligatorio para servicios WCF clasicos
4. El parsing del XML usa expresiones regulares para extraer `<ResultValue>`

## Tecnologias

- Expo SDK 55
- React Native 0.76.9
- TypeScript 5.3.3
- Expo Router 5.0.0