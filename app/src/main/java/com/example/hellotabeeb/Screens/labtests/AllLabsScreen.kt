package com.example.hellotabeeb.Screens.labtests

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellotabeeb.R

// AllLabsScreen.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllLabsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lab Tests",
                            modifier = Modifier.offset(x = -16.dp),
                            color = Color.Black  // Set text color to black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black  // Set icon color to black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White  // Set background color to white
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)  // Set background color to white
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Divider(
                    color = Color.Black,  // Set divider color to black
                    thickness = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Card for Lab Test
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),  // Set card color to white
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("lab_detail_screen") }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Placeholder for Lab Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, RectangleShape)  // Set box color to white
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.chughtai),  // Replace with the appropriate image resource
                                contentDescription = "Lab Icon",
                                modifier = Modifier.size(64.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Lab details
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Chughtai Lab",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black  // Set text color to black
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "upto 30% off on all tests",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.Black,  // Set text color to black
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Forward arrow icon
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Forward",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black  // Set icon color to black
                        )
                    }
                }
            }
        }
    )
}