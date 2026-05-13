package ec.edu.grupo3.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ec.edu.grupo3.mobile.ui.auth.LoginScreen
import ec.edu.grupo3.mobile.ui.conversion.ConversionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var authenticatedUser by remember { mutableStateOf<String?>(null) }

                    if (authenticatedUser == null) {
                        LoginScreen(
                            onLoginSuccess = { username ->
                                authenticatedUser = username
                            }
                        )
                    } else {
                        ConversionScreen(
                            username = authenticatedUser!!,
                            onLogout = {
                                authenticatedUser = null
                            }
                        )
                    }
                }
            }
        }
    }
}