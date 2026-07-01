package com.neurotutor.app.mobile.ui.screens.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neurotutor.app.mobile.R

@Composable
fun ProfileAvatar(
    avatarUrl: String?,
    size: Dp = 100.dp,
    borderWidth: Dp = 4.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .border(borderWidth, Color.White.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder image while we don't have Coil for loading remote URLs
        // Following the reference style
        Image(
            painter = painterResource(id = R.drawable.neo_head), // Using neo_head as a temporary avatar placeholder
            contentDescription = "Avatar",
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
