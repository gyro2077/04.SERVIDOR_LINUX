package ec.edu.grupo3.webclient.models;

public class ConversionViewModel {
    private String category = "Mass";
    private double inputValue;
    private String fromUnit = "KILOGRAM";
    private String toUnit = "POUND";
    private Double resultValue;
    private String successMessage;
    private String errorMessage;

    public static ConversionViewModel defaultModel() {
        return new ConversionViewModel();
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getInputValue() { return inputValue; }
    public void setInputValue(double inputValue) { this.inputValue = inputValue; }
    public String getFromUnit() { return fromUnit; }
    public void setFromUnit(String fromUnit) { this.fromUnit = fromUnit; }
    public String getToUnit() { return toUnit; }
    public void setToUnit(String toUnit) { this.toUnit = toUnit; }
    public Double getResultValue() { return resultValue; }
    public void setResultValue(Double resultValue) { this.resultValue = resultValue; }
    public String getSuccessMessage() { return successMessage; }
    public void setSuccessMessage(String successMessage) { this.successMessage = successMessage; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}