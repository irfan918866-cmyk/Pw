package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.PWWebViewScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("pw_settings", Context.MODE_PRIVATE)

        setContent {
            // State for light / dark mode (persisted in preferences)
            var isDarkMode by remember {
                mutableStateOf(prefs.getBoolean("is_dark_mode", false))
            }

            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PWWebViewScreen(
                        isDarkMode = isDarkMode,
                        onToggleTheme = {
                            val newMode = !isDarkMode
                            isDarkMode = newMode
                            prefs.edit().putBoolean("is_dark_mode", newMode).apply()
                        }
                    )
                }
            }
        }
    }
}
