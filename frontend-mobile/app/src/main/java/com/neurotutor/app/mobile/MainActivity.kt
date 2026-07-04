package com.neurotutor.app.mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.neurotutor.app.mobile.data.network.RetrofitClient
import com.neurotutor.app.mobile.navigation.AppNavigation
import com.neurotutor.app.mobile.ui.theme.NeuroTutorTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getDiagnosticQuestions()
                if (response.isSuccessful) {
                    val questions = response.body().orEmpty()
                    val firstQuestion = questions.firstOrNull()

                    Log.d("DiagnosticEndpoint", "Cantidad de preguntas: ${questions.size}")
                    Log.d("DiagnosticEndpoint", "ID primera pregunta: ${firstQuestion?.id}")
                    Log.d(
                        "DiagnosticEndpoint",
                        "Opciones primera pregunta: ${firstQuestion?.options?.size ?: 0}"
                    )
                } else {
                    Log.e(
                        "DiagnosticEndpoint",
                        "Error HTTP ${response.code()} al consultar preguntas"
                    )
                }
            } catch (error: Exception) {
                Log.e("DiagnosticEndpoint", "Error al consultar preguntas", error)
            }
        }

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
