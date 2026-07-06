package com.neurotutor.app.mobile.ui.components.ai

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.ai.AiTutorAction
import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import com.neurotutor.app.mobile.data.model.ai.AiTutorMessage
import com.neurotutor.app.mobile.ui.screens.learning.InteractiveExerciseUiState
import com.neurotutor.app.mobile.ui.screens.learning.interactiveExerciseStateKey

@Composable
fun NeoMessageBubble(
    message: AiTutorMessage,
    exerciseStates: Map<String, InteractiveExerciseUiState>,
    actionsEnabled: Boolean,
    onOptionSelected: (messageId: String, exerciseId: String, optionIndex: Int) -> Unit,
    onAction: (AiTutorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.neo_head),
            contentDescription = "Neo",
            modifier = Modifier.size(44.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            message.contents.forEachIndexed { index, content ->
                // 4. Log en NeoMessageBubble
                Log.d("NEO_DEBUG", "UI: Rendering content index=$index, type=${content::class.java.simpleName}")

                when (content) {
                    is AiTutorContent.Text -> TextContentBubble(content.text)
                    is AiTutorContent.StepExplanationContent ->
                        StepExplanationCard(content.explanation)
                    is AiTutorContent.SocraticQuestion ->
                        SocraticQuestionCard(content.question)
                    is AiTutorContent.MultipleChoice -> {
                        val exercise = content.exercise
                        InteractiveExerciseCard(
                            exercise = exercise,
                            state = exerciseStates[
                                interactiveExerciseStateKey(message.id, exercise.id)
                            ],
                            onOptionSelected = { selectedIndex ->
                                onOptionSelected(message.id, exercise.id, selectedIndex)
                            }
                        )
                    }
                    is AiTutorContent.HintCard ->
                        HintCard(content.text)
                    is AiTutorContent.ValidationCard ->
                        ValidationCard(content.text)
                    is AiTutorContent.SuccessCard ->
                        TextContentBubble(content.text, title = "¡Lo lograste!")
                }
            }
            if (message.suggestedActions.isNotEmpty()) {
                SuggestedActionChips(
                    actions = message.suggestedActions,
                    enabled = actionsEnabled,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun TextContentBubble(
    text: String,
    title: String = "Neo"
) {
    Card(
        shape = RoundedCornerShape(4.dp, 24.dp, 24.dp, 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EFFF))
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4F46E5)
            )
            Text(
                text = text,
                modifier = Modifier.padding(top = 6.dp),
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = Color(0xFF334155)
            )
        }
    }
}
