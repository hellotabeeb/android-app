package com.example.hellotabeeb

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hellotabeeb.Screens.appointments.BookingScreen
import com.example.hellotabeeb.Screens.appointments.DoctorDetailScreen
import com.example.hellotabeeb.Screens.appointments.DoctorsAvailable
import com.example.hellotabeeb.Screens.labtests.AllLabsScreen
import com.example.hellotabeeb.Screens.labtests.LabDetailScreen
import com.example.hellotabeeb.Screens.labtests.ConfirmationScreen
import com.example.hellotabeeb.Screens.homePage.AppointmentsScreen
import com.example.hellotabeeb.Screens.homePage.RecordsScreen
import com.example.hellotabeeb.homePage.homePage
import java.net.URLDecoder

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Appointments : Screen("appointments")
    object Records : Screen("records")
    object AllLabs : Screen("all_labs_screen")
    object LabDetail : Screen("lab_detail_screen")
    object Confirmation : Screen("confirmation_screen/{testName}/{testFee}/{labName}") {
        fun createRoute(testName: String, testFee: String, labName: String) =
            "confirmation_screen/$testName/$testFee/$labName"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    onDestinationChanged: (String) -> Unit,
    city: String, // Add city as a parameter
    loading: Boolean // Add loading as a parameter
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(
            route = "lab_detail_screen/{labName}",
            arguments = listOf(navArgument("labName") { type = NavType.StringType })
        ) { backStackEntry ->
            val labName = backStackEntry.arguments?.getString("labName") ?: ""
            onDestinationChanged("lab_detail_screen")
            LabDetailScreen(navController, labName) // Pass labName to LabDetailScreen
        }

        composable(Screen.Home.route) {
            onDestinationChanged(Screen.Home.route)
            homePage(navController, city, loading, Screen.Home.route) // Pass city, loading, and currentRoute
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
        composable(
            route = Screen.Confirmation.route,
            arguments = listOf(
                navArgument("testName") { type = NavType.StringType },
                navArgument("testFee") { type = NavType.StringType },
                navArgument("labName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val testName = URLDecoder.decode(backStackEntry.arguments?.getString("testName"), "UTF-8")
            val testFee = URLDecoder.decode(backStackEntry.arguments?.getString("testFee"), "UTF-8")
            val labName = URLDecoder.decode(backStackEntry.arguments?.getString("labName"), "UTF-8")

            ConfirmationScreen(
                testName = testName,
                testFee = testFee,
                labName = labName,
                onConfirmationComplete = { navController.popBackStack() },
                navController = navController  // Pass the navController here
            )
        }



        // Keep only the parameterized version:
        composable(
            "doctors_available/{specialization}",
            arguments = listOf(navArgument("specialization") { type = NavType.StringType })
        ) { backStackEntry ->
            val specialization = URLDecoder.decode(backStackEntry.arguments?.getString("specialization"), "UTF-8")
            DoctorsAvailable(navController, specialization)
        }

        // In Navigation.kt
        composable(
            "booking/{doctorId}",
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            BookingScreen(navController, doctorId)
        }




    }
}