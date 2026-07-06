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
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import com.neurotutor.app.mobile.data.model.ai.AiTutorMessage
import com.neurotutor.app.mobile.data.model.ai.AiTutorMessageSender
import com.neurotutor.app.mobile.ui.components.ai.NeoMessageBubble
import com.neurotutor.app.mobile.ui.components.ai.TutorLoadingBubble
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
    viewModel: AiTutorViewModel,
    onBack: () -> Unit
) {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF007AFF), Color(0xFF5AC8FA), Color(0xFFF1F5F9))
    )

    val entryPoint = when (mode) {
        TutorMode.DASHBOARD -> AiTutorEntryPoint.DASHBOARD
        TutorMode.PRACTICE -> AiTutorEntryPoint.PRACTICE
    }
    val conversationContext = remember(
        studentId,
        entryPoint,
        moduleId,
        exerciseId,
        studentName,
        moduleName,
        topicName,
        exerciseQuestion,
        exerciseOptions
    ) {
        AiTutorConversationContext(
            studentId = studentId,
            entryPoint = entryPoint,
            moduleId = moduleId,
            exerciseId = exerciseId.takeIf(String::isNotBlank),
            studentName = studentName,
            moduleName = moduleName,
            topicName = topicName,
            exerciseQuestion = exerciseQuestion,
            exerciseOptions = exerciseOptions
        )
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isCurrentConversation =
        uiState.conversationKey == conversationContext.conversationKey
    val messages = if (isCurrentConversation) {
        uiState.messages
    } else {
        emptyList()
    }
    val inputText = if (isCurrentConversation) uiState.inputText else ""
    val isAssistantLoading = isCurrentConversation && uiState.isSending
    val errorMessage = uiState.errorMessage.takeIf { isCurrentConversation }

    LaunchedEffect(conversationContext.conversationKey) {
        viewModel.selectConversation(conversationContext)
    }

    fun askNeo(question: String, action: String? = null) {
        viewModel.sendMessage(question, action)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // Fondo base claro
    ) {
        // 🟦 HEADER CON DEGRADADO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(gradientBackground)
        )

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("NeoTutor", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text(
                                text = "Tu asistente de aprendizaje", 
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent,
            bottomBar = {
                MessageInput(
                    value = inputText,
                    onValueChange = viewModel::updateInput,
                    onSend = {
                        viewModel.sendMessage()
                    }
                )
            }
        ) { paddingValues ->
            val listState = rememberLazyListState()
            
            LaunchedEffect(messages.size) {
                if (messages.size > 1) {
                    listState.animateScrollToItem(messages.size + 4)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SALUDO Y NEO
                item {
                    TutorGreetingHeader(studentName = studentName, mode = mode)
                }

                // TARJETA DE CONTEXTO (Módulo o Práctica)
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

                // ACCIONES RÁPIDAS
                item {
                    Column {
                        Text(
                            text = if (mode == TutorMode.DASHBOARD) "¿En qué puedo ayudarte hoy?" else "¿En qué puedo ayudarte?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        QuickActionsGrid(
                            mode = mode,
                            onAction = { actionType ->
                                val prompt = when (actionType) {
                                    "EXPLAIN" -> if (mode == TutorMode.DASHBOARD) "Explícame el módulo de $moduleName" else "Explícame cómo resolver este ejercicio: $exerciseQuestion"
                                    "HINT" -> "Dame una pista para resolver mi ejercicio actual"
                                    "THEORY" -> "Muéstrame la teoría relacionada con este tema"
                                    "SIMILAR" -> "Crea un ejercicio parecido al que estoy resolviendo"
                                    else -> "Hola Neo"
                                }
                                val backendAction = when (actionType) {
                                    "HINT" -> "HINT"
                                    "SIMILAR" -> "SIMILAR_EXERCISE"
                                    "EXPLAIN" -> if (mode == TutorMode.PRACTICE) {
                                        "EXPLAIN_STEP_BY_STEP"
                                    } else {
                                        null
                                    }
                                    else -> null
                                }
                                askNeo(prompt, backendAction)
                            }
                        )
                    }
                }

                // MENSAJES DEL CHAT
                items(messages, key = { it.id }) { message ->
                    if (message.sender == AiTutorMessageSender.STUDENT) {
                        ChatMessageBubble(message = message.toLegacyChatMessage())
                    } else {
                        NeoMessageBubble(
                            message = message,
                            exerciseStates = uiState.interactiveExerciseStates,
                            actionsEnabled = !uiState.isSending,
                            onOptionSelected = viewModel::selectInteractiveOption,
                            onAction = viewModel::onSuggestedAction
                        )
                    }
                }

                errorMessage?.let { safeError ->
                    item(key = "error-${conversationContext.conversationKey}") {
                        ChatMessageBubble(
                            message = ChatMessage(
                                id = "error-${conversationContext.conversationKey}",
                                text = safeError,
                                isFromUser = false
                            )
                        )
                    }
                }
                
                if (isAssistantLoading) {
                    item { TutorLoadingBubble() }
                }

                // SUGERENCIAS INFERIORES
                item {
                    SuggestedQuestions(onQuestionClick = { askNeo(it) })
                }
                
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

private fun AiTutorMessage.toLegacyChatMessage(): ChatMessage = ChatMessage(
    id = id,
    text = contents.joinToString(separator = "\n\n", transform = AiTutorContent::asPlainText),
    isFromUser = sender == AiTutorMessageSender.STUDENT,
    timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault())
        .format(Date(timestampMillis))
)

private fun AiTutorContent.asPlainText(): String = when (this) {
    is AiTutorContent.Text -> text
    is AiTutorContent.StepExplanationContent -> buildList {
        explanation.title?.let(::add)
        explanation.introduction?.let(::add)
        explanation.steps.forEachIndexed { index, step ->
            add("${index + 1}. $step")
        }
        explanation.conclusion?.let(::add)
    }.joinToString("\n")
    is AiTutorContent.SocraticQuestion -> question
    is AiTutorContent.MultipleChoice -> buildString {
        append(exercise.question)
        exercise.options.forEachIndexed { index, option ->
            append("\n${index + 1}. $option")
        }
    }
    is AiTutorContent.HintCard -> text
    is AiTutorContent.ValidationCard -> text
    is AiTutorContent.SuccessCard -> text
}

@Composable
fun TutorGreetingHeader(studentName: String, mode: TutorMode) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "¡Hola, $studentName! 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (mode == TutorMode.DASHBOARD)
                    "Estoy aquí para ayudarte a\nentender, practicar y aprender mejor."
                else
                    "Estoy aquí para ayudarte a\nentender y aprender mejor.",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
        Image(
            painter = painterResource(id = if (mode == TutorMode.DASHBOARD) R.drawable.neo_chat_dashboard else R.drawable.neo_tutor_practice),
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun DashboardContextCard(moduleName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = Color(0xFFF1F5F9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.fraction_neo_chat),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Módulo actual", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(
                    moduleName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Sigue aprendiendo y alcanza tus metas.", fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PracticeContextCard(moduleName: String, topicName: String, questionStatus: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Estás practicando",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF6366F1),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContextInfoItem(modifier = Modifier.weight(1f), icon = R.drawable.fraction_neo_chat, label = "Módulo", value = moduleName)
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFFF1F5F9)))
                ContextInfoItem(modifier = Modifier.weight(1f), icon = R.drawable.icon_topic_fraction, label = "Tema", value = topicName)
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color(0xFFF1F5F9)))
                ContextInfoItem(modifier = Modifier.weight(1f), icon = R.drawable.icon_question, label = "Pregunta", value = questionStatus)
            }
        }
    }
}

@Composable
fun ContextInfoItem(modifier: Modifier = Modifier, icon: Int, label: String, value: String) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            fontSize = 16.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF334155),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuickActionsGrid(mode: TutorMode, onAction: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f).clickable { onAction("EXPLAIN") },
                icon = "💡",
                title = if (mode == TutorMode.DASHBOARD) "Explícame" else "Explícame",
                subtitle = if (mode == TutorMode.DASHBOARD) "este tema" else "este ejercicio",
                iconBg = Color(0xFFFEF9C3)
            )
            QuickActionCard(
                modifier = Modifier.weight(1f).clickable { onAction("HINT") },
                icon = "🎯",
                title = "Dame una",
                subtitle = "pista",
                iconBg = Color(0xFFFEE2E2)
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f).clickable { onAction("THEORY") },
                icon = "📚",
                title = "Ver teoría",
                subtitle = "relacionada",
                iconBg = Color(0xFFE0F2FE)
            )
            QuickActionCard(
                modifier = Modifier.weight(1f).clickable { onAction(if (mode == TutorMode.DASHBOARD) "CHAT" else "SIMILAR") },
                icon = if (mode == TutorMode.DASHBOARD) "💬" else "📝",
                title = if (mode == TutorMode.DASHBOARD) "Pregúntale" else "Ejercicio",
                subtitle = if (mode == TutorMode.DASHBOARD) "a Neo" else "parecido",
                iconBg = Color(0xFFF3E8FF)
            )
        }
    }
}

@Composable
fun QuickActionCard(modifier: Modifier, icon: String, title: String, subtitle: String, iconBg: Color) {
    Card(
        modifier = modifier.height(76.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text(subtitle, fontSize = 16.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isFromUser) Color(0xFFE0DBFF) else Color(0xFFF0EFFF)
    val shape = if (message.isFromUser)
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    else
        RoundedCornerShape(4.dp, 24.dp, 24.dp, 24.dp)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if (!message.isFromUser) {
                Image(
                    painter = painterResource(id = R.drawable.neo_head),
                    contentDescription = "Neo",
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            Card(
                shape = shape,
                colors = CardDefaults.cardColors(containerColor = bgColor),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Text(
                        text = if (message.isFromUser) "Tú" else "Neo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (message.isFromUser) Color(0xFF6366F1) else Color(0xFF4F46E5)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        message.text,
                        fontSize = 16.sp,
                        color = Color(0xFF334155),
                        lineHeight = 20.sp
                    )
                    Text(
                        text = message.timestamp,
                        fontSize = 10.sp,
                        color = Color.Gray.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun NeoLoadingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(0.5f),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.neo_head),
            contentDescription = null,
            modifier = Modifier.size(44.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EFFF))
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color(0xFF6366F1))
            }
        }
    }
}

@Composable
fun SuggestedQuestions(onQuestionClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            "Puedes preguntarme cosas como:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6366F1),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        SuggestedQuestionItem(icon = R.drawable.icon_question, text = "¿Qué es una fracción equivalente?", onClick = { onQuestionClick("¿Qué es una fracción equivalente?") })
        Spacer(modifier = Modifier.height(8.dp))
        SuggestedQuestionItem(icon = R.drawable.icon_explain2, text = "Explícame cómo multiplicar fracciones", onClick = { onQuestionClick("Explícame cómo multiplicar fracciones") })
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
            Text(text, fontSize = 16.sp, color = Color(0xFF334155), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun MessageInput(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(16.dp),
        color = Color.White,
        shape = RoundedCornerShape(32.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF334155)),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text("Escribe tu pregunta aquí...", fontSize = 16.sp, color = Color.Gray)
                    }
                    innerTextField()
                }
            )
            IconButton(
                onClick = onSend,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF6366F1)),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}
