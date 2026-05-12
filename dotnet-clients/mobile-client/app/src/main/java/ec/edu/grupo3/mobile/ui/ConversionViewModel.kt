package ec.edu.grupo3.mobile.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.grupo3.mobile.data.ConversionResponse
import ec.edu.grupo3.mobile.data.SoapRepository
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val response: ConversionResponse) : UiState()
    data class Error(val message: String) : UiState()
}

class ConversionViewModel : ViewModel() {
    private val repository = SoapRepository()

    var selectedCategory by mutableStateOf("Mass")
    var inputValue by mutableStateOf("")
    var fromUnit by mutableStateOf("KILOGRAM")
    var toUnit by mutableStateOf("POUND")

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    private val unitsMap = mapOf(
        "Mass" to listOf("KILOGRAM", "GRAM", "POUND", "OUNCE"),
        "Length" to listOf("METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH"),
        "Temperature" to listOf("CELSIUS", "FAHRENHEIT", "KELVIN")
    )

    fun getAvailableUnits(): List<String> = unitsMap[selectedCategory] ?: emptyList()

    fun onCategoryChange(newCategory: String) {
        selectedCategory = newCategory
        val units = getAvailableUnits()
        fromUnit = units.first()
        toUnit = if (units.size > 1) units[1] else units.first()
        uiState = UiState.Idle
    }

    fun convert() {
        val valueDouble = inputValue.toDoubleOrNull()
        if (valueDouble == null) {
            uiState = UiState.Error("Ingrese un número válido")
            return
        }

        uiState = UiState.Loading
        viewModelScope.launch {
            val result = repository.convert(selectedCategory, valueDouble, fromUnit, toUnit)
            result.onSuccess { response ->
                uiState = UiState.Success(response)
            }.onFailure { error ->
                uiState = UiState.Error(error.localizedMessage ?: "Error de red al conectar al SOAP")
            }
        }
    }
}