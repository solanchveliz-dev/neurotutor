package com.neurotutor.app.mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.models.ModuleStatus
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase
import com.neurotutor.app.mobile.ui.viewmodels.LevelItem
import com.neurotutor.app.mobile.ui.viewmodels.TopicDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    studentId: String,
    moduleId: String,
    topicTitle: String, // 🚀 RECIBE EL TÍTULO REAL DINÁMICO
    viewModel: TopicDetailViewModel,
    onLevelSelected: (String, String) -> Unit, // 🚀 RECIBE ID Y TAG
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(moduleId) {
        viewModel.loadTopicDetails(studentId, moduleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Ruta de Aprendizaje", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MoradoActivo)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Image(
                                painter = painterResource(id = R.drawable.imagen_pregunta4_frutas),
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )
                            // 🚀 TÍTULO DINÁMICO
                            Text(text = topicTitle, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextoBase)
                            Text(text = "Domina los 3 niveles para completar el tema", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                    
                    if (state.levels.isEmpty()) {
                        item {
                            Text(
                                text = "No se encontraron niveles asociados a este tema.",
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                            )
                        }
                    }

                    items(state.levels) { level ->
                        LevelCard(
                            level = level, 
                            onClick = { 
                                val tag = if(level.name.contains("Básico")) "B" else if(level.name.contains("Intermedio")) "I" else "A"
                                onLevelSelected(level.levelId, tag) 
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LevelCard(level: LevelItem, onClick: () -> Unit) {
    val isLocked = level.status == ModuleStatus.BLOQUEADO
    val isEnCurso = level.status == ModuleStatus.EN_CURSO

    val emoji = when {
        level.name.contains("Básico", true) -> "🌱"
        level.name.contains("Intermedio", true) -> "🔥"
        else -> "🚀"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = !isLocked) { onClick() }
            .border(
                width = if (isEnCurso) 2.dp else 0.dp,
                color = if (isEnCurso) MoradoActivo else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) Color(0xFFE2E8F0) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLocked) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = if (isLocked) Color.LightGray else MoradoActivo.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                } else {
                    Text(text = emoji, fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = level.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLocked) Color.Gray else TextoBase
                )
                Text(
                    text = if (isLocked) "Nivel bloqueado" else "¡Haz clic para entrar!",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
