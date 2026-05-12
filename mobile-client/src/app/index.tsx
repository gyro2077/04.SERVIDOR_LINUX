import React, { useState, useEffect } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TextInput,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView
} from 'react-native';
import { invokeSoapConversion } from '../services/soapService';

type CategoryType = 'Mass' | 'Length' | 'Temperature';

const UNITS_MAP: Record<CategoryType, string[]> = {
  Mass: ['KILOGRAM', 'GRAM', 'POUND', 'OUNCE'],
  Length: ['METER', 'KILOMETER', 'CENTIMETER', 'MILE', 'YARD', 'FOOT', 'INCH'],
  Temperature: ['CELSIUS', 'FAHRENHEIT', 'KELVIN']
};

export default function HomeScreen() {
  const [category, setCategory] = useState<CategoryType>('Mass');
  const [inputValue, setInputValue] = useState<string>('');
  const [fromUnit, setFromUnit] = useState<string>('KILOGRAM');
  const [toUnit, setToUnit] = useState<string>('GRAM');
  const [result, setResult] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    const availableUnits = UNITS_MAP[category];
    setFromUnit(availableUnits[0]);
    setToUnit(availableUnits[1] || availableUnits[0]);
    setResult(null);
  }, [category]);

  const handleConversion = async () => {
    if (!inputValue || inputValue.trim() === '') {
      Alert.alert('Validacion', 'Por favor ingrese un valor numerico para convertir.');
      return;
    }

    const sanitizedInput = inputValue.replace(',', '.');
    const numericValue = parseFloat(sanitizedInput);

    if (isNaN(numericValue)) {
      Alert.alert('Error', 'El valor ingresado no es un numero valido.');
      return;
    }

    setIsLoading(true);
    setResult(null);

    const response = await invokeSoapConversion(category, numericValue, fromUnit, toUnit);

    setIsLoading(false);

    if (response.success && response.resultValue !== undefined) {
      setResult(`${numericValue} ${fromUnit} = ${response.resultValue.toFixed(5)} ${toUnit}`);
    } else {
      Alert.alert('Fallo en la Conversion', response.errorMessage || 'Error desconocido.');
    }
  };

  const renderSelector = (
    title: string,
    items: string[],
    selectedValue: string,
    onSelect: (val: string) => void
  ) => (
    <View style={styles.selectorContainer}>
      <Text style={styles.label}>{title}</Text>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.scrollSelector}>
        {items.map((item) => {
          const isSelected = item === selectedValue;
          return (
            <TouchableOpacity
              key={item}
              style={[styles.pill, isSelected && styles.pillSelected]}
              onPress={() => onSelect(item)}
            >
              <Text style={[styles.pillText, isSelected && styles.pillTextSelected]}>
                {item}
              </Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>
    </View>
  );

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      style={styles.container}
    >
      <ScrollView contentContainerStyle={styles.scrollContent}>

        <View style={styles.header}>
          <Text style={styles.title}>Conversor SOAP</Text>
          <Text style={styles.subtitle}>React Native & Expo</Text>
        </View>

        {renderSelector('Categoria', ['Mass', 'Length', 'Temperature'], category, setCategory)}
        {renderSelector('Desde (Origen)', UNITS_MAP[category], fromUnit, setFromUnit)}
        {renderSelector('Hacia (Destino)', UNITS_MAP[category], toUnit, setToUnit)}

        <View style={styles.inputContainer}>
          <Text style={styles.label}>Valor a convertir</Text>
          <TextInput
            style={styles.input}
            keyboardType="numeric"
            placeholder="Ej. 100.5"
            value={inputValue}
            onChangeText={setInputValue}
            editable={!isLoading}
          />
        </View>

        <TouchableOpacity
          style={[styles.button, isLoading && styles.buttonDisabled]}
          onPress={handleConversion}
          disabled={isLoading}
        >
          {isLoading ? (
            <ActivityIndicator color="#ffffff" size="large" />
          ) : (
            <Text style={styles.buttonText}>Convertir</Text>
          )}
        </TouchableOpacity>

        {result && (
          <View style={styles.resultBox}>
            <Text style={styles.resultLabel}>Resultado Exitosa</Text>
            <Text style={styles.resultText}>{result}</Text>
          </View>
        )}

      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f6fa',
  },
  scrollContent: {
    padding: 20,
    paddingTop: 60,
  },
  header: {
    alignItems: 'center',
    marginBottom: 30,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#2f3640',
  },
  subtitle: {
    fontSize: 16,
    color: '#718093',
    marginTop: 5,
  },
  selectorContainer: {
    marginBottom: 20,
  },
  label: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#353b48',
    marginBottom: 8,
  },
  scrollSelector: {
    flexDirection: 'row',
  },
  pill: {
    backgroundColor: '#dcdde1',
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 20,
    marginRight: 10,
  },
  pillSelected: {
    backgroundColor: '#00a8ff',
  },
  pillText: {
    color: '#2f3640',
    fontWeight: '600',
    fontSize: 13,
  },
  pillTextSelected: {
    color: '#ffffff',
    fontWeight: 'bold',
  },
  inputContainer: {
    marginBottom: 25,
  },
  input: {
    backgroundColor: '#ffffff',
    borderWidth: 1,
    borderColor: '#dcdde1',
    borderRadius: 12,
    padding: 15,
    fontSize: 18,
    color: '#2f3640',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  button: {
    backgroundColor: '#4cd137',
    paddingVertical: 16,
    borderRadius: 12,
    alignItems: 'center',
    shadowColor: '#4cd137',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 5,
    elevation: 4,
    marginBottom: 25,
  },
  buttonDisabled: {
    backgroundColor: '#7f8fa6',
  },
  buttonText: {
    color: '#ffffff',
    fontSize: 18,
    fontWeight: 'bold',
  },
  resultBox: {
    backgroundColor: '#ffffff',
    borderLeftWidth: 5,
    borderLeftColor: '#00a8ff',
    padding: 18,
    borderRadius: 10,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  resultLabel: {
    fontSize: 12,
    color: '#718093',
    textTransform: 'uppercase',
    fontWeight: 'bold',
    marginBottom: 5,
  },
  resultText: {
    fontSize: 17,
    fontWeight: 'bold',
    color: '#2f3640',
    textAlign: 'center',
  },
});