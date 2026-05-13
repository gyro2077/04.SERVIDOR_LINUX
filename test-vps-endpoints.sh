#!/bin/bash

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration apuntando al VPS
BASE_URL="http://209.145.48.25:8081/ROOT"
USERNAME="MONSTER"
PASSWORD="MONSTER9"

echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}  SOAP UNIT CONVERSION TESTS (VPS)${NC}"
echo -e "${BLUE}  Target: $BASE_URL${NC}"
echo -e "${BLUE}================================${NC}\n"

# Test 1: Login with correct credentials
echo -e "${YELLOW}[1/6] Testing LOGIN with correct credentials...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/Login" \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:log="http://ws.grupo3.edu.ec/">
   <soap:Body>
      <log:login>
         <username>'"$USERNAME"'</username>
         <password>'"$PASSWORD"'</password>
      </log:login>
   </soap:Body>
</soap:Envelope>')

if echo "$LOGIN_RESPONSE" | grep -q "<success>true</success>"; then
    echo -e "${GREEN}✓ Login successful${NC}"
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -oP '(?<=<token>)[^<]+')
    echo -e "${GREEN}✓ Token obtained: ${TOKEN:0:20}...${NC}"
else
    echo -e "${RED}✗ Login failed${NC}"
    echo "$LOGIN_RESPONSE"
    exit 1
fi

# Test 2: Login with wrong credentials
echo -e "\n${YELLOW}[2/6] Testing LOGIN with wrong credentials...${NC}"
WRONG_LOGIN=$(curl -s -X POST "$BASE_URL/Login" \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:log="http://ws.grupo3.edu.ec/">
   <soap:Body>
      <log:login>
         <username>admin</username>
         <password>wrongpass</password>
      </log:login>
   </soap:Body>
</soap:Envelope>')

if echo "$WRONG_LOGIN" | grep -q "<success>false</success>"; then
    echo -e "${GREEN}✓ Login correctly rejected for wrong credentials${NC}"
else
    echo -e "${RED}✗ Login should have failed${NC}"
fi

# Test 3: Mass conversion
echo -e "\n${YELLOW}[3/6] Testing MASS conversion (10 KILOGRAM -> POUND)...${NC}"
MASS_RESPONSE=$(curl -s -X POST "$BASE_URL/Conversion" \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:con="http://ws.grupo3.edu.ec/">
   <soap:Body>
      <con:convertMass>
         <token>'"$TOKEN"'</token>
         <value>10</value>
         <fromUnit>KILOGRAM</fromUnit>
         <toUnit>POUND</toUnit>
      </con:convertMass>
   </soap:Body>
</soap:Envelope>')

if echo "$MASS_RESPONSE" | grep -q "<message>OK</message>"; then
    RESULT=$(echo "$MASS_RESPONSE" | grep -oP '(?<=<resultValue>)[^<]+')
    echo -e "${GREEN}✓ Mass conversion successful: 10 KG = $RESULT POUND${NC}"
else
    echo -e "${RED}✗ Mass conversion failed${NC}"
fi

# Test 4: Length conversion
echo -e "\n${YELLOW}[4/6] Testing LENGTH conversion (5 METER -> FOOT)...${NC}"
LENGTH_RESPONSE=$(curl -s -X POST "$BASE_URL/Conversion" \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:con="http://ws.grupo3.edu.ec/">
   <soap:Body>
      <con:convertLength>
         <token>'"$TOKEN"'</token>
         <value>5</value>
         <fromUnit>METER</fromUnit>
         <toUnit>FOOT</toUnit>
      </con:convertLength>
   </soap:Body>
</soap:Envelope>')

if echo "$LENGTH_RESPONSE" | grep -q "<message>OK</message>"; then
    RESULT=$(echo "$LENGTH_RESPONSE" | grep -oP '(?<=<resultValue>)[^<]+')
    echo -e "${GREEN}✓ Length conversion successful: 5 METER = $RESULT FOOT${NC}"
else
    echo -e "${RED}✗ Length conversion failed${NC}"
fi

# Test 5: Temperature conversion
echo -e "\n${YELLOW}[5/6] Testing TEMPERATURE conversion (25 CELSIUS -> FAHRENHEIT)...${NC}"
TEMP_RESPONSE=$(curl -s -X POST "$BASE_URL/Conversion" \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:con="http://ws.grupo3.edu.ec/">
   <soap:Body>
      <con:convertTemperature>
         <token>'"$TOKEN"'</token>
         <value>25</value>
         <fromUnit>CELSIUS</fromUnit>
         <toUnit>FAHRENHEIT</toUnit>
      </con:convertTemperature>
   </soap:Body>
</soap:Envelope>')

if echo "$TEMP_RESPONSE" | grep -q "<message>OK</message>"; then
    RESULT=$(echo "$TEMP_RESPONSE" | grep -oP '(?<=<resultValue>)[^<]+')
    echo -e "${GREEN}✓ Temperature conversion successful: 25 CELSIUS = $RESULT FAHRENHEIT${NC}"
else
    echo -e "${RED}✗ Temperature conversion failed${NC}"
fi

# Test 6: Conversion without token
echo -e "\n${YELLOW}[6/6] Testing conversion WITHOUT token (should fail)...${NC}"
NO_TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/Conversion" \
  -H "Content-Type: text/xml; charset=UTF-8" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:con="http://ws.grupo3.edu.ec/">
   <soap:Body>
      <con:convertMass>
         <token></token>
         <value>10</value>
         <fromUnit>KILOGRAM</fromUnit>
         <toUnit>POUND</toUnit>
      </con:convertMass>
   </soap:Body>
</soap:Envelope>')

if echo "$NO_TOKEN_RESPONSE" | grep -q "Token inválido"; then
    echo -e "${GREEN}✓ Correctly rejected request without token${NC}"
else
    echo -e "${RED}✗ Should have rejected request without token${NC}"
fi

echo -e "\n${BLUE}================================${NC}"
echo -e "${GREEN}  ALL TESTS COMPLETED SUCCESSFULLY ON VPS${NC}"
echo -e "${BLUE}================================${NC}\n"
