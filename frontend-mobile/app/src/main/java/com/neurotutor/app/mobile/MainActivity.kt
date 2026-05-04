package com.neurotutor.app.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.neurotutor.app.mobile.ui.screens.RegisterScreen
import com.neurotutor.app.mobile.ui.theme.NeuroTutorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeuroTutorTheme {
                // Scaffold con estructura moderna
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Pasamos el padding al contenido
                    RegisterScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Preview para Android Studio
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    NeuroTutorTheme {
        RegisterScreen()
    }
}