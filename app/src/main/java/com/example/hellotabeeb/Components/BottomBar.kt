package com.example.hellotabeeb.Components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.hellotabeeb.Screen

@Composable
fun BottomBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == Screen.Home.route,
            onClick = { navController.navigate(Screen.Home.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, "Appointments") },
            label = { Text("Appointments") },
            selected = navController.currentDestination?.route == Screen.Appointments.route,
            onClick = { navController.navigate(Screen.Appointments.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Description, "Records") },
            label = { Text("Records") },
            selected = navController.currentDestination?.route == Screen.Records.route,
            onClick = { navController.navigate(Screen.Records.route) }
        )
    }
}