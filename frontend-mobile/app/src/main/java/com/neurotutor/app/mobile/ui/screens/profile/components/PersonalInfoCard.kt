package com.neurotutor.app.mobile.ui.screens.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PersonalInfoCard(
    name: String,
    email: String,
    level: String,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Información personal",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            InfoItem(icon = Icons.Default.Person, label = "Nombre", value = name)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
            InfoItem(icon = Icons.Default.Email, label = "Correo electrónico", value = email)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
            InfoItem(
                icon = Icons.Default.School,
                label = "Recomendación inicial",
                value = level,
                valueColor = Color(0xFF8B5CF6)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onEditProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F3FF),
                    contentColor = Color(0xFF7C3AED)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar perfil", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF334155)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFF8FAFC)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF64748B)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label, 
                fontSize = 12.sp, 
                color = Color(0xFF94A3B8)
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(18.dp)
        )
    }
}
