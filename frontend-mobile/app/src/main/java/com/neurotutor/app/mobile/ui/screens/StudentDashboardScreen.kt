package com.neurotutor.app.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // 🚀 IMPORTANTE
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.ui.models.ModuleItem
import com.neurotutor.app.mobile.ui.theme.FondoPanelEstudiante
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.viewmodels.StudentDashboardViewModel

@Composable
fun StudentDashboardScreen(
    studentId: String, // 🚀 RECIBE EL ID DE LA RUTA
    modifier: Modifier = Modifier,
    dashboardViewModel: StudentDashboardViewModel = viewModel(),
    onModuloSeleccionado: (ModuleItem) -> Unit
) {
    val state by dashboardViewModel.uiState.collectAsState()

    // 🚀 DISPARADOR: Saca la pantalla del bucle de carga ejecutando la petición HTTP
    LaunchedEffect(studentId) {
        dashboardViewModel.cargarInformacionReal(studentId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (!state.isLoading && state.errorMessage == null) {
                StudentDashboardHeader(
                    nombreEstudiante = state.nombreEstudiante,
                    gradoSeccion = state.gradoSeccion,
                    nivelActual = state.nivelActual,
                    puntosTotales = state.puntosTotales
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPanelEstudiante)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(color = MoradoActivo, strokeWidth = 4.dp)
                }

                state.errorMessage != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.errorMessage!!, color = MoradoActivo)
                        TextButton(onClick = { dashboardViewModel.cargarInformacionReal(studentId) }) {
                            Text("Reintentar")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(state.modulos) { modulo ->
                            StudentModuleCard(
                                modulo = modulo,
                                onModuloClick = { onModuloSeleccionado(modulo) }
                            )
                        }
                    }
                }
            }
        }
    }
}