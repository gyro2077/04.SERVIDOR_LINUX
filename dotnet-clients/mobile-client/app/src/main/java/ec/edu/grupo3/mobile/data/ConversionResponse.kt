package ec.edu.grupo3.mobile.data

data class ConversionResponse(
    val category: String = "",
    val fromUnit: String = "",
    val toUnit: String = "",
    val inputValue: Double = 0.0,
    val resultValue: Double = 0.0,
    val message: String = "OK"
)