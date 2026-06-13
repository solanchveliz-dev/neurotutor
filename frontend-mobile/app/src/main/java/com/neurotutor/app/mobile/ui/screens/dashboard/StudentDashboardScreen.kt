package com.neurotutor.app.mobile.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.data.model.learning.ModuleItem
import com.neurotutor.app.mobile.ui.components.StudentDashboardHeader
import com.neurotutor.app.mobile.ui.components.StudentModuleCard
import com.neurotutor.app.mobile.ui.theme.FondoPanelEstudiante
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase

@Composable
fun StudentDashboardScreen(
    studentId: String,
    modifier: Modifier = Modifier,
    dashboardViewModel: StudentDashboardViewModel = viewModel(),
    onModuloSeleccionado: (ModuleItem) -> Unit
) {
    val state by dashboardViewModel.uiState.collectAsState()

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
        },
        bottomBar = {
            if (!state.isLoading && state.errorMessage == null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = { /* Continuar aprendizaje */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MoradoActivo),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Continuar Aprendiendo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPanelEstudiante)
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MoradoActivo)
                }

                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.errorMessage!!, color = MoradoActivo)
                        TextButton(onClick = { dashboardViewModel.cargarInformacionReal(studentId) }) {
                            Text("Reintentar")
                        }
                    }
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📔", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Módulos de Aprendizaje",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoBase
                            )
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
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
}
