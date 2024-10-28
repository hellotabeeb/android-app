package com.example.hellotabeeb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.hellotabeeb.Components.BottomBar
import com.example.hellotabeeb.homePage.SidebarHome
import com.example.hellotabeeb.homePage.homePage
import com.example.hellotabeeb.ui.theme.HelloTabeebTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloTabeebTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var showSidebar by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf(Screen.Home.route) }

    Scaffold(
        bottomBar = {
            if (currentRoute !in listOf("all_labs_screen", "lab_detail_screen")) {
                BottomBar(navController)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AppNavigation(navController) { route ->
                currentRoute = route
            }

            // Sidebar toggle button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { showSidebar = !showSidebar }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Sidebar",
                        tint = Color.White
                    )
                }
            }

            // Show sidebar when toggled
            if (showSidebar) {
                SidebarHome(onDismiss = { showSidebar = false })
            }
        }
    }
}