package com.example.hellotabeeb.Screens.appointments

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.hellotabeeb.Screens.homePage.BrandColor
import com.example.hellotabeeb.Screens.appointments.DoctorsAvailableViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    navController: NavHostController,
    doctorId: String,
    viewModel: DoctorsAvailableViewModel = viewModel()
) {
    val doctors by viewModel.doctors.collectAsState()

    // Log the received doctorId and all available doctor IDs for debugging
    LaunchedEffect(Unit) {
        Log.d("DoctorDetail", "Received doctorId: $doctorId")
        doctors.forEach { doctor ->
            Log.d("DoctorDetail", "Available doctor: ${doctor.name}, ID: ${doctor.id}")
        }
    }

    // Find doctor by ID, with more detailed logging
    val doctor = doctors.find { it.id == doctorId }
    Log.d("DoctorDetail", "Found doctor: ${doctor?.name ?: "Not found"}")

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top App Bar with back button
        TopAppBar(
            title = {
                Text(
                    "Medical Center Details",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
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
                containerColor = BrandColor
            )
        )

        if (doctor == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Medical center information not available", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Doctor ID: $doctorId", color = Color.Gray, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { navController.popBackStack() }) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main details card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = doctor.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandColor
                        )

                        Text(
                            text = doctor.specialization,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )

                        // Distance
                        val distanceInKm = String.format("%.1f", doctor.distance / 1000.0)
                        Text(
                            text = "$distanceInKm kilometers away",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Contact Information Card
                if (!doctor.phone.isNullOrEmpty() || !doctor.website.isNullOrEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Contact Information",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandColor
                            )

                            // Phone Number
                            doctor.phone?.let {
                                if (it.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_DIAL)
                                                intent.data = Uri.parse("tel:$it")
                                                context.startActivity(intent)
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = BrandColor
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = it,
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            // Website
                            doctor.website?.let {
                                if (it.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_VIEW)
                                                intent.data = Uri.parse(it)
                                                context.startActivity(intent)
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Public,
                                            contentDescription = null,
                                            tint = BrandColor
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = it,
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Hours Card
                doctor.hours?.let { hours ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Hours",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandColor
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = BrandColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = hours,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                doctor.isOpen?.let {
                                    Text(
                                        text = if (it) "Open Now" else "Closed",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (it) Color(0xFF4CAF50) else Color(0xFFE53935)
                                    )
                                }
                            }
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { /* TODO: Book appointment functionality */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Book Appointment")
                    }

                    Button(
                        onClick = {
                            doctor.phone?.let { phone ->
                                if (phone.isNotEmpty()) {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:$phone")
                                    context.startActivity(intent)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A9B6E))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call")
                    }
                }
            }
        }
    }
}
