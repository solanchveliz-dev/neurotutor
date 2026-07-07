package com.neurotutor.app.mobile.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R

val AuthNavy = Color(0xFF111B5C)
val AuthPurple = Color(0xFF5417F5)
val AuthMuted = Color(0xFF747DB6)
val AuthBorder = Color(0xFFD8DCF1)

@Composable
fun AuthBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF007AFF),
                        Color(0xFF5AC8FA),
                        Color(0xFFEAF7FF),
                        Color(0xFFF8FAFC)
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(R.drawable.cloud_bottom),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth,
            alpha = 0.78f
        )
        content()
    }
}

@Composable
fun AuthHeader(
    title: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterStart),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF1838C7),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Text(
            text = title,
            color = AuthNavy,
            fontSize = 24.sp,
            lineHeight = 29.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 54.dp)
        )
    }
}

@Composable
fun NeoSpeechHero(
    headline: String,
    message: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.weight(1.18f),
            color = Color.White,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 5.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
                Text(
                    text = headline,
                    color = AuthNavy,
                    fontSize = 18.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = message,
                    color = AuthNavy,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(Modifier.width(4.dp))
        Image(
            painter = painterResource(R.drawable.neo_theory),
            contentDescription = "Neo te acompaña durante la recuperación de contraseña",
            modifier = Modifier
                .weight(0.92f)
                .height(195.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun AuthPrimaryButton(
    text: String,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    val buttonBrush = if (enabled) {
        Brush.horizontalGradient(listOf(Color(0xFF7627F5), Color(0xFF2437E9)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFFAAA5C9), Color(0xFF8E93BD)))
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(buttonBrush),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        } else {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.width(12.dp))
            Text(text = "→", color = Color.White, fontSize = 25.sp)
        }
    }
}
