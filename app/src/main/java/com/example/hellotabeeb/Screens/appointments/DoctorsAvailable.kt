package com.example.hellotabeeb.Screens.appointments

import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.android.gms.location.LocationServices
import android.Manifest


val BrandColor = Color(0xFF193F6C)
val LightBrandColor = Color(0xFFE8EDF3)

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

    val cities = listOf("Lahore", "Islamabad", "Karachi")


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
                }
            }
        } else {
            // Handle permission not granted
            isLoading = false
        }
    }



    // City Selection Dialog
    if (showCityDialog) {
        Dialog(
            onDismissRequest = { showCityDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Select City",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    cities.forEach { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCity = city
                                    showCityDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (city == selectedCity) BrandColor else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = city,
                                fontSize = 16.sp,
                                color = if (city == selectedCity) BrandColor else Color.Black,
                                fontWeight = if (city == selectedCity) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // Enhanced TopAppBar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BrandColor,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                TopAppBar(
                    title = {
                        Text(
                            "Find Doctors",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                    "Expert healthcare at your fingertips",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        if (doctors.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(doctors) { doctor ->
                    DoctorCard(doctor)
                }
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = BrandColor.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Doctor Image with Coil3
            var isLoading by remember { mutableStateOf(false) }
            var isError by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightBrandColor),
                contentAlignment = Alignment.Center
            ) {
                if (doctor.profilePicture.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(doctor.profilePicture)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile picture of ${doctor.name}",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                        onLoading = { isLoading = true },
                        onError = { isError = true },
                        onSuccess = {
                            isLoading = false
                            isError = false
                        }
                    )

                    if (isLoading || isError) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = doctor.name,
                            modifier = Modifier.size(60.dp),
                            tint = BrandColor
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = doctor.name,
                        modifier = Modifier.size(60.dp),
                        tint = BrandColor
                    )
                }
            }

            // Doctor Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "${doctor.distance}m away",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = BrandColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = doctor.address,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = doctor.name,
                    color = BrandColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = doctor.qualification,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = doctor.specialization,
                    fontSize = 16.sp,
                    color = BrandColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}