package com.example.hellotabeeb.Screens.appointments


import com.example.hellotabeeb.Screens.appointments.Doctor
import com.example.hellotabeeb.Screens.appointments.DoctorsAvailableViewModel
import com.example.hellotabeeb.Screens.homePage.BrandColor
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.hellotabeeb.Screens.homePage.CitySelectionDialog
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorsAvailable(
    navController: NavHostController,
    specialization: String,
    viewModel: DoctorsAvailableViewModel = viewModel()
) {
    val doctors by viewModel.doctors.collectAsState()
    var selectedCity by remember { mutableStateOf("Lahore") }
    var showCityDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    // Get location and fetch doctors on launch
    LaunchedEffect(specialization) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    viewModel.findNearbyDoctors(it.latitude, it.longitude, specialization)
                    isLoading = false
                } ?: run {
                    // Handle case when location is null
                    Log.e("DoctorsAvailable", "Location is null")
                    isLoading = false
                }
            }.addOnFailureListener { e ->
                Log.e("DoctorsAvailable", "Failed to get location: ${e.message}")
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    // City Selection Dialog
    if (showCityDialog) {
        CitySelectionDialog(
            cities = listOf(
                "Lahore", "Karachi", "Islamabad", "Faisalabad", "Multan",
                // Add more cities as needed
            ),
            onCitySelected = { selectedCity = it; showCityDialog = false },
            onDismiss = { showCityDialog = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // TopAppBar with back button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BrandColor,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                TopAppBar(
                    title = {
                        Text(
                            specialization,
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
                    ),
                    actions = {
                        // City Selector
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showCityDialog = true }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color.White
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                selectedCity,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                )

                // Subtitle
                Text(
                    "Medical centers near you",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Loading state
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandColor)
            }
        }
        // Empty state
        else if (doctors.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No medical centers found nearby",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
        // Results list
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(doctors) { doctor ->
                    SimplifiedDoctorCard(
                        doctor = doctor,
                        navController = navController,
                        onClick = { /* your existing onClick code */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SimplifiedDoctorCard(
    doctor: Doctor,
    navController: NavHostController,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val distanceInKm = String.format("%.1f", doctor.distance / 1000.0)
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = BrandColor.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Medical Center Name
            Text(
                text = doctor.name,
                color = BrandColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Specialization
            Text(
                text = doctor.specialization,
                fontSize = 14.sp,
                color = Color.Gray
            )

            // Location with icon and distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = BrandColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = doctor.address,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$distanceInKm km",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrandColor
                )
            }

            // Expandable content
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Divider
                    Divider(color = Color.LightGray)

                    // Hours (if available)
                    doctor.hours?.let { hours ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = BrandColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = hours,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            doctor.isOpen?.let {
                                Text(
                                    text = if (it) "Open Now" else "Closed",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (it) Color(0xFF4CAF50) else Color(0xFFE53935)
                                )
                            }
                        }
                    }

                    // Contact Information (if available)
                    doctor.phone?.takeIf { it.isNotEmpty() }?.let { phone ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:$phone")
                                    context.startActivity(intent)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = BrandColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = phone,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Website (if available)
                    doctor.website?.takeIf { it.isNotEmpty() }?.let { website ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse(website)
                                    context.startActivity(intent)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = BrandColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = website,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Action buttons in SimplifiedDoctorCard
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                // Cache the doctor before navigating
                                DoctorsAvailableViewModel.addDoctorToCache(doctor)
                                navController.navigate("booking/${doctor.id}")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = BrandColor
                            ),
                            border = BorderStroke(1.dp, BrandColor)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = BrandColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Book", fontSize = 12.sp)
                        }

                        // Call Button
                        if (!doctor.phone.isNullOrEmpty()) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:${doctor.phone}")
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = BrandColor
                                ),
                                border = BorderStroke(1.dp, BrandColor)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = BrandColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call", fontSize = 12.sp)
                            }
                        }

                        // Directions Button (if coordinates available)
                        if (doctor.latitude != null && doctor.longitude != null) {
                            OutlinedButton(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("google.navigation:q=${doctor.latitude},${doctor.longitude}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = BrandColor
                                ),
                                border = BorderStroke(1.dp, BrandColor)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = BrandColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Map", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}