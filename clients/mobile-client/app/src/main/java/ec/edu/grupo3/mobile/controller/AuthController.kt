package ec.edu.grupo3.mobile.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ec.edu.grupo3.mobile.model.AuthRepository

class AuthController : ViewModel() {
    var username by mutableStateOf("")
        private set
    
    var password by mutableStateOf("")
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var isAuthenticated by mutableStateOf(false)
        private set

    fun onUsernameChange(value: String) {
        username = value
        errorMessage = null
    }

    fun onPasswordChange(value: String) {
        password = value
        errorMessage = null
    }

    fun login() {
        if (AuthRepository.authenticate(username, password)) {
            errorMessage = null
            isAuthenticated = true
        } else {
            errorMessage = "Usuario o contraseña incorrectos"
        }
    }

    fun logout() {
        username = ""
        password = ""
        errorMessage = null
        isAuthenticated = false
    }
}