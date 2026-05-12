package ec.edu.grupo3.mobile.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.grupo3.mobile.data.ConversionResponse
import ec.edu.grupo3.mobile.data.SoapRepository
import kotlinx.coroutines.launch

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Success(val response: ConversionResponse) : UiState
    data class Error(val message: String) : UiState
}

class ConversionViewModel : ViewModel() {
    private val repository = SoapRepository()

    val categories = listOf("Mass", "Length", "Temperature")

    private val unitsMap = mapOf(
        "Mass" to listOf("KILOGRAM", "GRAM", "POUND", "OUNCE"),
        "Length" to listOf("METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH"),
        "Temperature" to listOf("CELSIUS", "FAHRENHEIT", "KELVIN")
    )

    var selectedCategory by mutableStateOf(categories[0])
        private set

    var availableUnits by mutableStateOf(unitsMap[selectedCategory] ?: emptyList())
        private set

    var fromUnit by mutableStateOf(availableUnits.getOrNull(0) ?: "")
        private set

    var toUnit by mutableStateOf(availableUnits.getOrNull(1) ?: availableUnits.getOrNull(0) ?: "")
        private set

    var inputValue by mutableStateOf("1.0")
        private set

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    fun onCategoryChange(category: String) {
        selectedCategory = category
        availableUnits = unitsMap[category] ?: emptyList()
        fromUnit = availableUnits.getOrNull(0) ?: ""
        toUnit = availableUnits.getOrNull(1) ?: availableUnits.getOrNull(0) ?: ""
        uiState = UiState.Idle
    }

    fun onFromUnitChange(unit: String) {
        fromUnit = unit
    }

    fun onToUnitChange(unit: String) {
        toUnit = unit
    }

    fun onInputValueChange(value: String) {
        inputValue = value
    }

    fun convert() {
        val numericValue = inputValue.toDoubleOrNull()
        if (numericValue == null) {
            uiState = UiState.Error("Por favor, ingrese un valor numérico válido.")
            return
        }

        uiState = UiState.Loading
        viewModelScope.launch {
            val result = repository.convert(selectedCategory, numericValue, fromUnit, toUnit)
            uiState = result.fold(
                onSuccess = { response -> UiState.Success(response) },
                onFailure = { error -> UiState.Error(error.message ?: "Error desconocido en la conversión") }
            )
        }
    }
}