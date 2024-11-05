package com.example.hellotabeeb.homePage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.rpc.Help

@Composable
fun SidebarHome(onDismiss: () -> Unit) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,  // This ensures white background
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(300.dp)
                .verticalScroll(rememberScrollState())
                .background(Color.White)  // Additional white background
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Sidebar",
                    tint = Color.Black  // Ensure icon is visible on white background
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SidebarItem(icon = Icons.Default.Person, text = "Profile")
            SidebarItem(icon = Icons.Default.Settings, text = "Settings")
            SidebarItem(icon = Icons.Default.Notifications, text = "Notifications")
        }
    }
}

@Composable
fun SidebarItem(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Black  // Ensure icons are black
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = Color.Black  // Ensure text is black
        )
    }
}