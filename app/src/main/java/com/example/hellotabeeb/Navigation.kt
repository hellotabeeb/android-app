package com.example.hellotabeeb

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        composable(
            route = Screen.Confirmation.route,
            arguments = listOf(
                navArgument("testName") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("testFee") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val encodedTestName = backStackEntry.arguments?.getString("testName") ?: ""
            val encodedTestFee = backStackEntry.arguments?.getString("testFee") ?: ""
            // Decode the parameters
            val testName = java.net.URLDecoder.decode(encodedTestName, "UTF-8")
            val testFee = java.net.URLDecoder.decode(encodedTestFee, "UTF-8")
            onDestinationChanged(Screen.Confirmation.route)
            ConfirmationScreen(
                testName = testName,
                testFee = testFee,
                onConfirmationComplete = { navController.popBackStack() }
            )
        }
    }
}