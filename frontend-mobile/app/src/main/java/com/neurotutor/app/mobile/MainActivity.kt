package com.neurotutor.app.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.neurotutor.app.mobile.navigation.AppNavigation
import com.neurotutor.app.mobile.ui.theme.NeuroTutorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            NeuroTutorTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Llamamos a nuestro contenedor limpio de navegación
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
