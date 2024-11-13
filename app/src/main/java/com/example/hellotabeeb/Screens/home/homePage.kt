package com.example.hellotabeeb.homePage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.hellotabeeb.Screen


@Composable
fun HomeServiceCard(
    navController: NavHostController,
    title: String,
    icon: ImageVector,
    route: String,
    showPopup: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .width(80.dp)
                .height(120.dp)
                .clickable {
                    if (showPopup) {
                        showDialog = true
                    } else {
                        navController.navigate(route)
                    }
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4782DE)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            color = Color.Black,
            fontSize = 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Feature Coming Soon") },
                text = { Text("This feature will be available soon. Please stay connected.") },
                confirmButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homePage(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HomeTopBanner()
        HomeMainSection(navController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1 / 3.3f * LocalConfiguration.current.screenHeightDp.dp)
            .background(Color(0xFF193F6C)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = " Welcome To HelloTabeeb",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Find doctors, specialists...") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)  // Increased width
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMainSection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color(0xFF4782DE),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Our Services",
                color = Color(0xFF4782DE),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Main Card containing the grid
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // First row of cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HomeServiceCard(
                        navController,
                        "Lab tests",
                        Icons.Default.Biotech,
                        "all_labs_screen",
                        showPopup = false
                    )
                    HomeServiceCard(
                        navController,
                        "Nursing",
                        Icons.Default.LocalHospital,
                        "nursing_route",
                        showPopup = true
                    )
                    HomeServiceCard(
                        navController,
                        "Physiotherapy",
                        Icons.Default.FitnessCenter,
                        "physiotherapy_route",
                        showPopup = true
                    )
                }
                Spacer(modifier = Modifier.height(26.dp))
                // Second row of cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HomeServiceCard(
                        navController,
                        "Medicines",
                        Icons.Default.LocalPharmacy,
                        "medicines_route",
                        showPopup = true
                    )
                    HomeServiceCard(
                        navController,
                        "Appointments",
                        Icons.Default.Event,
                        Screen.Appointments.route,
                        showPopup = true
                    )
                    HomeServiceCard(
                        navController,
                        "Home health care",
                        Icons.Default.Home,
                        "home_health_care_route",
                        showPopup = true
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewHomePage() {
    val navController = rememberNavController()
    homePage(navController)
}