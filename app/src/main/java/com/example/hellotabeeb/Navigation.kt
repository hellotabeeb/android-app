package com.example.hellotabeeb


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hellotabeeb.Screens.labtests.AllLabsScreen
import com.example.hellotabeeb.Screens.labtests.LabDetailScreen
import com.example.hellotabeeb.homePage.AppointmentsScreen
import com.example.hellotabeeb.homePage.RecordsScreen
import com.example.hellotabeeb.homePage.homePage

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Appointments : Screen("appointments")
    object Records : Screen("records")
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController(), onDestinationChanged: (String) -> Unit) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            onDestinationChanged(Screen.Home.route)
            homePage(navController)
        }
        composable(Screen.Appointments.route) {
            onDestinationChanged(Screen.Appointments.route)
            AppointmentsScreen(navController)
        }
        composable(Screen.Records.route) {
            onDestinationChanged(Screen.Records.route)
            RecordsScreen(navController)
        }
        composable("all_labs_screen") {
            onDestinationChanged("all_labs_screen")
            AllLabsScreen(navController)
        }
        composable("lab_detail_screen") {
            onDestinationChanged("lab_detail_screen")
            LabDetailScreen(navController)
        }
    }
}