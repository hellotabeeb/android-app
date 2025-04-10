package com.example.hellotabeeb

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.hellotabeeb.Components.BottomBar
import com.example.hellotabeeb.homePage.SidebarHome
import com.example.hellotabeeb.ui.theme.HelloTabeebTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var city by mutableStateOf("Unknown")
    private var loading by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Remove the system status bar (black stripe)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                loading = true
                getLastLocation { location ->
                    city = getCityName(location)
                    loading = false
                }
            } else {
                city = "Unknown"
                loading = false
                // Schedule to ask for permission again after 12 hours
                schedulePermissionRequest()
            }
        }

        setContent {
            HelloTabeebTheme {
                var city by remember { mutableStateOf("Unknown") }
                val locationPermissionGranted = remember { mutableStateOf(false) }

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted.value = true
                    getLastLocation { location ->
                        city = getCityName(location)
                        loading = false
                    }
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }

                MainScreen(city, loading)
            }
        }
    }

    private fun getLastLocation(onLocationReceived: (Location) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationReceived(it)
            }
        }
    }

    private fun getCityName(location: Location): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        return addresses?.get(0)?.locality ?: "Unknown"
    }

    private fun schedulePermissionRequest() {
        val handler = android.os.Handler(mainLooper)
        handler.postDelayed({
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }, 12 * 60 * 60 * 1000) // 12 hours in milliseconds
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(city: String, loading: Boolean) {
    val navController = rememberNavController()
    var showSidebar by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf(Screen.Home.route) }
    var showCityDropdown by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf(city) }

    Scaffold(
        bottomBar = {
            if (currentRoute !in listOf(Screen.Home.route, "all_labs_screen", "lab_detail_screen", "confirmation_screen/{testName}/{testFee}", "appointments")) {
                BottomBar(navController)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavigation(
                navController = navController,
                onDestinationChanged = { route -> currentRoute = route },
                city = selectedCity,
                loading = loading
            )

            // Sidebar toggle button
            if (currentRoute !in listOf(Screen.Home.route, "all_labs_screen", "lab_detail_screen", "confirmation_screen/{testName}/{testFee}", "appointments")) {
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
                            contentDescription = "Menu"
                        )
                    }
                }
            }

            // Show sidebar when toggled
            if (showSidebar && currentRoute !in listOf(Screen.Home.route, "all_labs_screen", "lab_detail_screen", "confirmation_screen/{testName}/{testFee}", "appointments")) {
                SidebarHome(onDismiss = { showSidebar = false })
            }

            // City Dropdown Menu
            if (showCityDropdown) {
                CitySelectionDialog(
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
                    onCitySelected = { selectedCity = it; showCityDropdown = false },
                    onDismiss = { showCityDropdown = false }
                )
            }
        }
    }
}

@Composable
fun CitySelectionDialog(cities: List<String>, onCitySelected: (String) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Select City", color = Color(0xFF193F6C), fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.height(300.dp)) {
                cities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(text = city, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                        onClick = { onCitySelected(city) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}