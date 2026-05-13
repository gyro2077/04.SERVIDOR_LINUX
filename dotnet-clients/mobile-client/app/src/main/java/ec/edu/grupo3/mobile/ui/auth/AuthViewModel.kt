package ec.edu.grupo3.mobile.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    private val validUser = "MONSTER"
    private val validPass = "MONSTER9"

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onUsernameChange(value: String) {
        username = value
        errorMessage = null
    }

    fun onPasswordChange(value: String) {
        password = value
        errorMessage = null
    }

    fun login(onSuccess: (String) -> Unit) {
        if (username.trim() == validUser && password == validPass) {
            errorMessage = null
            onSuccess(username.trim())
        } else {
            errorMessage = "Usuario o contraseña incorrectos."
        }
    }
}