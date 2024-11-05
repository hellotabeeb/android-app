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
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.hellotabeeb.Screen

// BottomBar.kt

@Composable
fun BottomBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.White  // White background
    ) {
        val selectedColor = Color(0xFF2196F3)  // Material Blue color for selected items
        val unselectedColor = Color.Gray       // Gray color for unselected items

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    "Home",
                    tint = if (navController.currentDestination?.route == Screen.Home.route)
                        selectedColor else unselectedColor
                )
            },
            label = {
                Text(
                    "Home",
                    color = if (navController.currentDestination?.route == Screen.Home.route)
                        selectedColor else unselectedColor
                )
            },
            selected = navController.currentDestination?.route == Screen.Home.route,
            onClick = { navController.navigate(Screen.Home.route) }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.DateRange,
                    "Appointments",
                    tint = if (navController.currentDestination?.route == Screen.Appointments.route)
                        selectedColor else unselectedColor
                )
            },
            label = {
                Text(
                    "Appointments",
                    color = if (navController.currentDestination?.route == Screen.Appointments.route)
                        selectedColor else unselectedColor
                )
            },
            selected = navController.currentDestination?.route == Screen.Appointments.route,
            onClick = { navController.navigate(Screen.Appointments.route) }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Description,
                    "Records",
                    tint = if (navController.currentDestination?.route == Screen.Records.route)
                        selectedColor else unselectedColor
                )
            },
            label = {
                Text(
                    "Records",
                    color = if (navController.currentDestination?.route == Screen.Records.route)
                        selectedColor else unselectedColor
                )
            },
            selected = navController.currentDestination?.route == Screen.Records.route,
            onClick = { navController.navigate(Screen.Records.route) }
        )
    }
}