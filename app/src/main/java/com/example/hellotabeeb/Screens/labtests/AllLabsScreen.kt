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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellotabeeb.R
import android.app.Activity
import android.view.WindowManager
import androidx.core.view.WindowCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllLabsScreen(navController: NavController) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    val primaryColor = Color(0xFF193F6C)

    // Make the status bar and navigation bar adapt to our theme
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = primaryColor.toArgb()
    window.navigationBarColor = primaryColor.toArgb()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lab Tests",
                            modifier = Modifier.offset(x = (-16).dp),
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Chughtai Lab Card
                LabCard(
                    navController = navController,
                    labName = "Chughtai Lab",
                    discount = "upto 30% off on all tests",
                    imageResource = R.drawable.chughtai
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Excel Lab Card
                LabCard(
                    navController = navController,
                    labName = "Excel Labs",
                    discount = "upto 25% off on all tests",
                    imageResource = R.drawable.excel
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dr. Essa Lab Card
                LabCard(
                    navController = navController,
                    labName = "Dr. Essa Laboratory & Diagnostic Centre",
                    discount = "upto 20% off on all tests",
                    imageResource = R.drawable.essa
                )

                Spacer(modifier = Modifier.height(16.dp))

                // IDC Card
                LabCard(
                    navController = navController,
                    labName = "Islamabad Diagnostic Center",
                    discount = "upto 15% off on all tests",
                    imageResource = R.drawable.idc
                )
            }
        }
    )
}

@Composable
private fun LabCard(
    navController: NavController,
    labName: String,
    discount: String,
    imageResource: Int
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("lab_detail_screen/$labName") } // Pass labName as a parameter
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, RectangleShape)
            ) {
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = "$labName Icon",
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = labName,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = discount,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Forward",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }
    }
}