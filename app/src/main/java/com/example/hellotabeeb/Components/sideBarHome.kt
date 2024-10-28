package com.example.hellotabeeb.homePage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.rpc.Help

@Composable
fun SidebarHome(onDismiss: () -> Unit) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(300.dp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Sidebar")
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
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text)
    }
}