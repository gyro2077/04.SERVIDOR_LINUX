package ec.edu.grupo3.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ec.edu.grupo3.mobile.controller.AuthController
import ec.edu.grupo3.mobile.ui.ConversionScreen
import ec.edu.grupo3.mobile.ui.theme.MobileClientJavaTheme
import ec.edu.grupo3.mobile.view.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileClientJavaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var authenticatedUser by remember { mutableStateOf<String?>(null) }
                    val authController = remember { AuthController() }

                    if (authenticatedUser == null) {
                        LoginScreen(
                            onLoginSuccess = { username ->
                                authenticatedUser = username
                            },
                            authController = authController
                        )
                    } else {
                        ConversionScreen(
                            username = authenticatedUser!!,
                            onLogout = {
                                authController.logout()
                                authenticatedUser = null
                            }
                        )
                    }
                }
            }
        }
    }
}