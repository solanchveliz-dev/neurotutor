package com.neurotutor.app.mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSectionsScreen(
    studentId: String,
    moduleId: String,
    levelName: String,
    topicTitle: String, // 🚀 RECIBE EL TÍTULO REAL DINÁMICO
    onNavigateToTheory: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onNavigateToExam: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 🍕 CABECERA (IGUAL A LA IMAGEN) ---
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(id = R.drawable.imagen_pregunta4_frutas),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = topicTitle, // 🚀 DINÁMICO: Mostrará "Fracciones", "Sumas Divertidas", etc.
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = TextoBase
            )
            Text(
                text = "Completa todas las secciones para avanzar",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- 📊 TARJETA DE PROGRESO ---
            Card(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Progreso del Módulo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { 0.65f },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                            color = MoradoActivo,
                            trackColor = Color(0xFFF1F5F9)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("65%", fontWeight = FontWeight.Black, color = MoradoActivo, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 📚 SECCIONES (ESTILO DE BOTONES GRANDES) ---
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionSectionCard(
                    title = "Teoría",
                    subtitle = "✓ Completado",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF22C55E),
                    onClick = onNavigateToTheory
                )
                ActionSectionCard(
                    title = "Ejercicios",
                    subtitle = "8/10 completados",
                    icon = Icons.Default.PlayArrow,
                    color = MoradoActivo,
                    onClick = onNavigateToExercises
                )
                ActionSectionCard(
                    title = "Examen Final",
                    subtitle = "Bloqueado",
                    icon = Icons.Default.Lock,
                    color = Color(0xFF94A3B8),
                    isLocked = true,
                    onClick = onNavigateToExam
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
            TutorIAFeedbackMessage()
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ActionSectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .clickable(enabled = !isLocked) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(45.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun TutorIAFeedbackMessage() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Card(
            modifier = Modifier.padding(end = 45.dp, bottom = 12.dp),
            shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("¡Bien hecho!", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF16A34A))
                Text(text = "Vas muy bien con la teoría. ¡Sigue así!", fontSize = 13.sp, color = TextoBase.copy(alpha = 0.7f))
            }
        }
        Surface(modifier = Modifier.size(60.dp).border(2.dp, Color.White, CircleShape), shape = CircleShape, color = Color(0xFFDCFCE7)) {
            Box(contentAlignment = Alignment.Center) { Text("🤩", fontSize = 32.sp) }
        }
    }
}
