package com.example.hellotabeeb.homePage

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.hellotabeeb.CitySelectionDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import android.Manifest
import android.location.Location
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalConfiguration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBanner(
    city: String,
    loading: Boolean,
    currentRoute: String,
    onCityClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((1 / 3.3f) * LocalConfiguration.current.screenHeightDp.dp)
            .background(Color(0xFF193F6C)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Improved location selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onCityClick() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = city,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "HelloTabeeb",
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
                    .fillMaxWidth(0.9f)
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
fun homePage(navController: NavHostController, city: String, loading: Boolean, currentRoute: String) {
    var showCityDialog by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf(city) }
    var isLoading by remember { mutableStateOf(loading) }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isLoading = true
            getLastLocation(context, fusedLocationClient) { location ->
                selectedCity = getCityName(context, location)
                isLoading = false
            }
        } else {
            selectedCity = "Unknown"
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isLoading = true
            getLastLocation(context, fusedLocationClient) { location ->
                selectedCity = getCityName(context, location)
                isLoading = false
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HomeTopBanner(
            selectedCity,
            isLoading,
            currentRoute,
            onCityClick = { showCityDialog = true }
        )
        HomeMainSection(navController)

        // City Selection Dialog
        if (showCityDialog) {
            CitySelectionDialog1(
                cities = listOf(
                    "Lahore", "Karachi", "Islamabad", "Faisalabad", "Multan",
                    "Abbotabad", "Ahmedpur", "Bahawalnagar", "Bahawalpur", "Bajaur Agency",
                    "Bhakkar", "Bhimber", "Burewala", "Chakwal", "Charsadda",
                    "Chichawatni", "Chiniot City", "Chishtian", "Dadu", "Daska",
                    "Depalpur", "Dera Ghazi Khan", "Dera Ismail Khan", "Dharki", "Dunyapur",
                    "Farooqabad", "Ghotki", "Gojra", "Gujjar Khan", "Gujranwala",
                    "Gujrat", "Hafizabad", "Haripur Hazara", "Haroonabad", "Haveli Lakha",
                    "Jacobabad", "Jaranwala", "Jehlum", "Jhang", "Joharabad",
                    "Kamalia", "Karak", "Kasur", "Khanewal", "Khanpur",
                    "Kharian", "Kohat", "Kot Addu", "KPK", "Larkana",
                    "Layyah", "Lodhran", "Malakand", "Mandi Bahaudin", "Mansehra",
                    "Mardan", "Mirpur", "Mirpur Mathelo", "Muridke", "Muzaffargarh",
                    "Muzaffarabad", "Nankana Sahib", "Narrowal", "Nowshera", "Oghi",
                    "Okara", "Okara Cantt", "Pakpattan", "Pano Akil", "Pasrur",
                    "Pattoki", "Peshawar", "Pir Mahal", "Qaidabad", "Quetta",
                    "Rahim Yar Khan", "Raiwind", "Rajanpur", "Rawalkot", "Rawalpindi",
                    "Sadiqabad", "Sahiwal", "Sahiwal & Burewala", "Sambrial", "Sangla Hill",
                    "Sargodha", "Shahdakot", "Shahkot", "Shakar Garh", "Sheikupura",
                    "Sialkot", "Sukkur", "Swabi", "Swat", "Tanlianwala",
                    "Timargara", "Toba Tek Singh", "Vehari", "Wazirabad"
                ),
                onCitySelected = { city ->
                    selectedCity = city
                    showCityDialog = false
                },
                onDismiss = { showCityDialog = false }
            )
        }
    }
}



private fun getLastLocation(
    context: android.content.Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location) -> Unit
) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationReceived(it)
            }
        }
    }
}

private fun getCityName(context: android.content.Context, location: Location): String {
    val geocoder = android.location.Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    return addresses?.get(0)?.locality ?: "Unknown"
}





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
                        "Appointments",
                        Icons.Default.Event,
                        "appointments"
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
                        "Nursing",
                        Icons.Default.LocalHospital,
                        "nursing_route",
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


@Composable
fun CitySelectionDialog1(
    cities: List<String>,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select City",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF193F6C),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(cities) { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCitySelected(city)
                                    onDismiss()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF193F6C),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = city,
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}