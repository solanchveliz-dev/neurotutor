package com.neurotutor.app.mobile.ui.screens.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.ui.components.ConfirmationDialog

/**
 * Sección de cuenta optimizada visualmente.
 * Se unificaron bordes (20.dp) y padding (16.dp) para consistencia global.
 */
@Composable
fun AccountActionsCard(
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp), // Estandarizado a 20.dp
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Padding interno reducido
            Text(
                text = "Cuenta",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ActionItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Cerrar sesión",
                onClick = { showLogoutDialog = true }
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp), 
                color = Color(0xFFF1F5F9)
            )
            
            ActionItem(
                icon = Icons.Default.DeleteOutline,
                label = "Eliminar cuenta",
                color = Color(0xFFEF4444),
                onClick = { showDeleteDialog = true }
            )
        }
    }

    // Lógica de diálogos existente mantenida sin cambios
    ConfirmationDialog(
        show = showLogoutDialog,
        title = "¿Cerrar sesión?",
        message = "¿Estás seguro de que deseas salir de tu cuenta?",
        confirmText = "Salir",
        confirmColor = Color(0xFF6366F1),
        onConfirm = {
            showLogoutDialog = false
            onLogout()
        },
        onDismiss = { showLogoutDialog = false }
    )

    ConfirmationDialog(
        show = showDeleteDialog,
        title = "¿Eliminar cuenta?",
        message = "Esta acción es irreversible y perderás todo tu progreso.",
        confirmText = "Eliminar",
        confirmColor = Color(0xFFEF4444),
        onConfirm = {
            showDeleteDialog = false
            onDeleteAccount()
        },
        onDismiss = { showDeleteDialog = false }
    )
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    label: String,
    color: Color = Color(0xFF334155),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp), // Altura compacta estandarizada
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = color
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
