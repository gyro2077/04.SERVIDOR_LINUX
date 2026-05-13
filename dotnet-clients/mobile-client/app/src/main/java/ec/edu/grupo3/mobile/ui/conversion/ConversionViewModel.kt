package ec.edu.grupo3.mobile.ui.conversion

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

    val categories = listOf("Mass" to "Masa", "Length" to "Longitud", "Temperature" to "Temperatura")

    private val unitsMap = mapOf(
        "Mass" to listOf("KILOGRAM", "GRAM", "POUND", "OUNCE"),
        "Length" to listOf("METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH"),
        "Temperature" to listOf("CELSIUS", "FAHRENHEIT", "KELVIN")
    )

    var selectedCategory by mutableStateOf("Mass")
        private set

    var availableUnits by mutableStateOf(unitsMap["Mass"] ?: emptyList())
        private set

    var fromUnit by mutableStateOf("KILOGRAM")
        private set

    var toUnit by mutableStateOf("POUND")
        private set

    var inputValue by mutableStateOf("")
        private set

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    fun onCategoryChange(newCategory: String) {
        selectedCategory = newCategory
        availableUnits = unitsMap[newCategory] ?: emptyList()
        fromUnit = availableUnits.firstOrNull() ?: ""
        toUnit = if (availableUnits.size > 1) availableUnits[1] else availableUnits.firstOrNull() ?: ""
        uiState = UiState.Idle
    }

    fun onFromUnitChange(unit: String) { fromUnit = unit }
    fun onToUnitChange(unit: String) { toUnit = unit }
    fun onInputValueChange(value: String) { inputValue = value }

    fun convert() {
        val valueDouble = inputValue.toDoubleOrNull()
        if (valueDouble == null) {
            uiState = UiState.Error("Por favor, ingrese un número positivo válido.")
            return
        }

        uiState = UiState.Loading
        viewModelScope.launch {
            val result = repository.convert(selectedCategory, valueDouble, fromUnit, toUnit)
            uiState = result.fold(
                onSuccess = { response -> UiState.Success(response) },
                onFailure = { error -> UiState.Error(error.message ?: "Error de red al conectar al SOAP") }
            )
        }
    }
}