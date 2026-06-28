package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.common.AiTutorRequest
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class TutorMode {
    DASHBOARD,
    PRACTICE
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorHelpScreen(
    mode: TutorMode = TutorMode.PRACTICE,
    studentId: Long = 1L,
    moduleId: Long = 1L,
    studentName: String = "",
    moduleName: String = "Fracciones",
    topicName: String = "Suma de fracciones",
    questionStatus: String = "4 de 10",
    exerciseId: String = "",
    exerciseQuestion: String = "",
    exerciseOptions: List<String> = emptyList(),
    correctAnswer: String = "",
    onClose: () -> Unit,
    viewModel: ExerciseViewModel
) {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF007AFF), Color(0xFF5AC8FA), Color(0xFFFFFFFF))
    )

    var inputText by remember { mutableStateOf("") }
    val messages = remember { 
        mutableStateListOf(
            ChatMessage(text = "¡Hola $studentName! 👋\n\nEstoy aquí para ayudarte a entender, practicar y aprender mejor cada día.", isFromUser = false),
            ChatMessage(text = "¿En qué tema necesitas ayuda hoy?\n\nPuedo explicarte, darte ejemplos o resolver tus dudas. 😊", isFromUser = false)
        )
    }
    var isAssistantLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Función para llamar a la IA Neo
    fun askNeo(question: String) {
        if (question.isBlank()) return
        
        // Agregar mensaje del usuario
        messages.add(ChatMessage(text = question, isFromUser = true))
        isAssistantLoading = true

        coroutineScope.launch {
            try {
                val request = AiTutorRequest(
                    studentId = studentId,
                    moduleId = moduleId,
                    question = question,
                    context = "Módulo: $moduleName, Tema: $topicName. Pregunta actual: $exerciseQuestion"
                )
                
                val response = RetrofitClient.apiService.askTutor(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val neoResponse = response.body()?.answer ?: "No recibí respuesta de Neo."
                    messages.add(ChatMessage(text = neoResponse, isFromUser = false))
                } else {
                    messages.add(ChatMessage(text = "Lo siento, tuve un problema al procesar tu solicitud. (Error ${response.code()})", isFromUser = false))
                }
            } catch (e: Exception) {
                messages.add(ChatMessage(text = "Parece que hay un problema con la conexión. Intenta de nuevo.", isFromUser = false))
            } finally {
                isAssistantLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NeoTutor", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text(
                            text = "Tu asistente de aprendizaje", 
                            fontSize = 14.sp, 
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Visible
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    Surface(
                        onClick = { /* Navegar al historial */ },
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF6366F1))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Historial", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.background(gradientBackground),
        bottomBar = {
            MessageInput(
                value = inputText,
                onValueChange = { inputText = it },
                onSend = {
                    val textToSend = inputText
                    inputText = ""
                    askNeo(textToSend)
                }
            )
        }
    ) { paddingValues ->
        val listState = rememberLazyListState()
        
        // Auto-scroll al recibir nuevos mensajes
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size + 5)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if (mode == TutorMode.DASHBOARD) {
                    TutorDashboardHeader(studentName = studentName)
                } else {
                    TutorPracticeHeader(studentName = studentName)
                }
            }

            item {
                if (mode == TutorMode.DASHBOARD) {
                    DashboardContextCard(moduleName = moduleName)
                } else {
                    PracticeContextCard(
                        moduleName = moduleName,
                        topicName = topicName,
                        questionStatus = questionStatus
                    )
                }
            }

            item {
                Text(
                    "¿En qué puedo ayudarte hoy?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                QuickActionsGrid(
                    mode = mode,
                    onAction = { actionType ->
                        val prompt = when (actionType) {
                            "EXPLAIN" -> if (mode == TutorMode.DASHBOARD) "Explícame el módulo de $moduleName" else "Explícame cómo resolver este ejercicio: $exerciseQuestion"
                            "HINT" -> "Dame una pista para resolver mi ejercicio actual"
                            else -> "¿Qué puedo aprender hoy?"
                        }
                        askNeo(prompt)
                    }
                )
            }

            items(messages, key = { it.id }) { message ->
                ChatMessageBubble(message = message)
            }
            
            if (isAssistantLoading) {
                item { NeoLoadingBubble() }
            }
            
            item {
                SuggestedQuestions(onQuestionClick = { askNeo(it) })
            }

            if (mode == TutorMode.PRACTICE) {
                item {
                    NeoAdviceCard()
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun TutorDashboardHeader(studentName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1.2f)) {
            Text(text = "¡Hola, $studentName! 👋", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Estoy aquí para ayudarte a\nentender, practicar y aprender mejor.",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.neo_chat_dashboard),
            contentDescription = null,
            modifier = Modifier.sizeIn(maxWidth = 160.dp, maxHeight = 160.dp).weight(0.8f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun TutorPracticeHeader(studentName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1.2f)) {
            Text(text = "¡Hola, $studentName! 👋", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Estoy aquí para ayudarte a\nentender y aprender mejor.",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.neo_tutor_practice),
            contentDescription = null,
            modifier = Modifier.sizeIn(maxWidth = 160.dp, maxHeight = 160.dp).weight(0.8f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun DashboardContextCard(moduleName: String) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.fraction_neo_chat), contentDescription = null, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Módulo actual", fontSize = 12.sp, color = Color.Gray)
                Text(
                    moduleName, 
                    fontSize = 22.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color(0xFF1E293B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Sigue aprendiendo y alcanza tus metas.", fontSize = 12.sp, color = Color.Gray)
            }
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Continuar", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun PracticeContextCard(moduleName: String, topicName: String, questionStatus: String) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Estás practicando", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1), modifier = Modifier.padding(bottom = 12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                ContextInfoItem(modifier = Modifier.weight(1f), icon = R.drawable.fraction_neo_chat, label = "Módulo", value = moduleName)
                Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                ContextInfoItem(modifier = Modifier.weight(1f), icon = R.drawable.icon_topic_fraction, label = "Tema", value = topicName)
                Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                ContextInfoItem(modifier = Modifier.weight(1f), icon = R.drawable.icon_question, label = "Pregunta", value = questionStatus)
            }
        }
    }
}

@Composable
fun ContextInfoItem(modifier: Modifier = Modifier, icon: Int, label: String, value: String) {
    Row(modifier = modifier.padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(label, fontSize = 9.sp, color = Color.Gray, maxLines = 1)
            Text(
                value, 
                fontSize = 11.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF1E293B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun QuickActionsGrid(mode: TutorMode, onAction: (String) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f).clickable { onAction("EXPLAIN") },
                icon = R.drawable.icon_explain2,
                text = if (mode == TutorMode.DASHBOARD) "Explícame este tema" else "Explícame este ejercicio"
            )
            QuickActionCard(
                modifier = Modifier.weight(1f).clickable { onAction("HINT") },
                icon = R.drawable.target_bullseye,
                text = "Dame una pista"
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = R.drawable.icon_theory2,
                text = "Ver teoría relacionada"
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = R.drawable.icon_hint,
                text = if (mode == TutorMode.DASHBOARD) "Pregúntale a Neo" else "Ejercicio parecido"
            )
        }
    }
}

@Composable
fun QuickActionCard(modifier: Modifier, icon: Int, text: String) {
    Card(
        modifier = modifier.height(72.dp).shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(modifier = Modifier.size(34.dp).background(Color(0xFFF8FAFC), CircleShape), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text, 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF1E293B), 
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    if (message.isFromUser) {
        UserMessageBubble(message = message)
    } else {
        NeoMessageBubble(message = message)
    }
}

@Composable
fun NeoMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.neo_head),
            contentDescription = "Neo",
            modifier = Modifier.size(72.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Card(
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EFFF)),
            modifier = Modifier.widthIn(min = 160.dp, max = 340.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Neo", 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    color = Color(0xFF4F46E5)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    message.text, 
                    fontSize = 15.sp, 
                    color = Color(0xFF334155),
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    message.timestamp,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun UserMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 64.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 2.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0DBFF)),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    "Tú", 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color(0xFF6366F1), 
                    modifier = Modifier.align(Alignment.End)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    message.text, 
                    fontSize = 15.sp, 
                    color = Color(0xFF334155),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.align(Alignment.End), 
                    horizontalArrangement = Arrangement.End, 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(message.timestamp, fontSize = 10.sp, color = Color.Gray.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF6366F1))
                }
            }
        }
    }
}

@Composable
fun NeoLoadingBubble() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top) {
        Image(
            painter = painterResource(id = R.drawable.neo_head), 
            contentDescription = null, 
            modifier = Modifier.size(72.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Card(
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EFFF))
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp, color = Color(0xFF6366F1))
            }
        }
    }
}

@Composable
fun SuggestedQuestions(onQuestionClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text("Puedes preguntarme cosas como:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
        Spacer(modifier = Modifier.height(12.dp))
        SuggestedQuestionItem(icon = R.drawable.icon_question, text = "¿Qué es una fracción equivalente?", onClick = { onQuestionClick("¿Qué es una fracción equivalente?") })
        Spacer(modifier = Modifier.height(8.dp))
        SuggestedQuestionItem(icon = R.drawable.icon_explain2, text = "Explícame cómo multiplicar fracciones", onClick = { onQuestionClick("Explícame cómo multiplicar fracciones") })
        Spacer(modifier = Modifier.height(8.dp))
        SuggestedQuestionItem(icon = R.drawable.icon_topic_fraction, text = "¿Cómo convertir fracciones mixtas?", onClick = { onQuestionClick("¿Cómo convertir fracciones mixtas?") })
    }
}

@Composable
fun SuggestedQuestionItem(icon: Int, text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).background(Color(0xFFF8FAFC), CircleShape), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 13.sp, color = Color(0xFF334155), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun NeoAdviceCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.neo_head),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Consejo de Neo ✨", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF6366F1))
                Text(
                    "Practicar con ayuda es la mejor forma de aprender. ¡Tú puedes!",
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
            Surface(
                onClick = {},
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF6366F1))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Mi progreso", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                }
            }
        }
    }
}

@Composable
fun MessageInput(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        color = Color.White,
        shape = RoundedCornerShape(32.dp),
        shadowElevation = 8.dp
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) { Icon(Icons.Default.AttachFile, contentDescription = null, tint = Color.Gray) }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                textStyle = TextStyle(fontSize = 15.sp, color = Color(0xFF334155)),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text("Escribe tu pregunta aquí...", fontSize = 15.sp, color = Color.LightGray)
                    }
                    innerTextField()
                }
            )
            IconButton(
                onClick = onSend,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF6366F1)),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}
