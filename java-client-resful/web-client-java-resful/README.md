# Web Client RESTful - Java Spring Boot

Cliente web MVC para conversor de unidades RESTful.

## Tecnologias

- Java 21
- Spring Boot 3.x (MVC + Thymeleaf)
- Bootstrap 5
- REST Client interno

## Estructura

```
src/main/java/ec/edu/grupo3/webclient/
├── WebClientRestfulApplication.java
├── controllers/
│   ├── AuthController.java
│   └── ConversionController.java
├── models/
│   ├── LoginViewModel.java
│   ├── ConversionViewModel.java
│   └── ConversionResponse.java
└── services/
    └── RestServiceModel.java
```

## Ejecutar

```bash
cd /home/gyro/Documents/ULTIMO_SEMESTRE/ARQUITECTURA/04.SERVIDOR/java-client-resful/web-client-java-resful
mvn clean package
mvn spring-boot:run
```

## URL

http://localhost:8080/auth/login

## Credenciales

- Usuario: MONSTER
- Contrasena: MONSTER9

## Backend REST

POST http://209.145.48.25:8082/ROOT/api/convert/{category}
- Mass: KILOGRAM, GRAM, POUND, OUNCE
- Length: METER, KILOMETER, CENTIMETER, MILE, YARD, FOOT, INCH
- Temperature: CELSIUS, FAHRENHEIT, KELVIN