package com.example.hellotabeeb

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hellotabeeb.Screens.labtests.AllLabsScreen
import com.example.hellotabeeb.Screens.labtests.LabDetailScreen
import com.example.hellotabeeb.Screens.labtests.ConfirmationScreen
import com.example.hellotabeeb.Screens.homePage.AppointmentsScreen
import com.example.hellotabeeb.Screens.homePage.RecordsScreen
import com.example.hellotabeeb.homePage.homePage

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Appointments : Screen("appointments")
    object Records : Screen("records")
    object AllLabs : Screen("all_labs_screen")
    object LabDetail : Screen("lab_detail_screen")
    object Confirmation : Screen("confirmation_screen/{testName}/{testFee}") {
        fun createRoute(testName: String, testFee: String) = "confirmation_screen/$testName/$testFee"
    }
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
        composable(Screen.AllLabs.route) {
            onDestinationChanged(Screen.AllLabs.route)
            AllLabsScreen(navController)
        }
        composable(Screen.LabDetail.route) {
            onDestinationChanged(Screen.LabDetail.route)
            LabDetailScreen(navController)
        }
        composable(Screen.Confirmation.route) { backStackEntry ->
            val testName = backStackEntry.arguments?.getString("testName") ?: ""
            val testFee = backStackEntry.arguments?.getString("testFee") ?: ""
            onDestinationChanged(Screen.Confirmation.route)
            ConfirmationScreen(
                testName = testName,
                testFee = testFee,
                onConfirmationComplete = {
                    navController.popBackStack(Screen.Home.route, false)
                }
            )
        }
    }
}