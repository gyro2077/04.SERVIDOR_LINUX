import { useState, useCallback } from "react";
import {
  convertMass,
  convertLength,
  convertTemperature,
  type MassUnit,
  type LengthUnit,
  type TemperatureUnit,
  type ConversionResult,
} from "@/models/conversionModel";

type Category = "length" | "mass" | "temperature";

export const useConversionController = () => {
  const [category, setCategory] = useState<Category>("length");
  const [value, setValue] = useState("1");
  const [from, setFrom] = useState<string>("METER");
  const [to, setTo] = useState<string>("KILOMETER");
  const [result, setResult] = useState<ConversionResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const massUnits: MassUnit[] = ["KILOGRAM", "GRAM", "POUND", "OUNCE"];
  const lengthUnits: LengthUnit[] = [
    "METER",
    "KILOMETER",
    "CENTIMETER",
    "MILE",
    "YARD",
    "FOOT",
    "INCH",
  ];
  const temperatureUnits: TemperatureUnit[] = [
    "CELSIUS",
    "FAHRENHEIT",
    "KELVIN",
  ];

  const getUnitsForCategory = (cat: Category): string[] => {
    switch (cat) {
      case "mass":
        return massUnits;
      case "temperature":
        return temperatureUnits;
      default:
        return lengthUnits;
    }
  };

  const handleCategoryChange = (newCategory: Category) => {
    setCategory(newCategory);
    const units = getUnitsForCategory(newCategory);
    setFrom(units[0]);
    setTo(units[1] || units[0]);
    setResult(null);
    setError(null);
  };

  const handleConversion = useCallback(async () => {
    const num = parseFloat(value);
    if (isNaN(num)) {
      setError("Valor inválido");
      return;
    }

    setLoading(true);
    setError(null);

    try {
      let convResult: ConversionResult;

      if (category === "mass") {
        convResult = await convertMass(
          num,
          from as MassUnit,
          to as MassUnit
        );
      } else if (category === "temperature") {
        convResult = await convertTemperature(
          num,
          from as TemperatureUnit,
          to as TemperatureUnit
        );
      } else {
        convResult = await convertLength(
          num,
          from as LengthUnit,
          to as LengthUnit
        );
      }

      setResult(convResult);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Error en la conversión"
      );
      setResult(null);
    } finally {
      setLoading(false);
    }
  }, [value, category, from, to]);

  return {
    category,
    setCategory: handleCategoryChange,
    value,
    setValue,
    from,
    setFrom,
    to,
    setTo,
    result,
    loading,
    error,
    handleConversion,
    availableUnits: getUnitsForCategory(category),
  };
};
