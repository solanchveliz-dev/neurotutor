package com.neurotutor.app.mobile.ui.components.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.data.model.ai.AiTutorAction

@Composable
fun SuggestedActionChips(
    actions: List<AiTutorAction>,
    enabled: Boolean,
    onAction: (AiTutorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        actions.distinct().forEach { action ->
            AssistChip(
                onClick = { onAction(action) },
                enabled = enabled,
                label = { Text(action.label(), fontSize = 16.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = action.icon(),
                        contentDescription = null,
                        modifier = Modifier
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFFF5F3FF),
                    labelColor = Color(0xFF4338CA),
                    leadingIconContentColor = Color(0xFF6D28D9)
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = enabled,
                    borderColor = Color(0xFFC4B5FD)
                )
            )
        }
    }
}

private fun AiTutorAction.label(): String = when (this) {
    AiTutorAction.UNDERSTOOD -> "Entendí"
    AiTutorAction.HINT -> "Dame una pista"
    AiTutorAction.SIMILAR_EXERCISE -> "Intentar ejercicio parecido"
    AiTutorAction.EXPLAIN_STEP_BY_STEP -> "Ver procedimiento"
}

private fun AiTutorAction.icon(): ImageVector = when (this) {
    AiTutorAction.UNDERSTOOD -> Icons.Default.Check
    AiTutorAction.HINT -> Icons.Default.Lightbulb
    AiTutorAction.SIMILAR_EXERCISE -> Icons.Default.Refresh
    AiTutorAction.EXPLAIN_STEP_BY_STEP -> Icons.Default.TipsAndUpdates
}
