package com.neurotutor.app.mobile.ui.components.ai

import androidx.compose.foundation.Image
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R

@Composable
fun TutorLoadingBubble(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.neo_head),
            contentDescription = "Neo está preparando una respuesta",
            modifier = Modifier.size(44.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0EFFF))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF6366F1)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Neo está pensando…", fontSize = 16.sp, color = Color(0xFF334155))
            }
        }
    }
}
