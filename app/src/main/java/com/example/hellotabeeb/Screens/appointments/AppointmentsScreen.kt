package com.example.hellotabeeb.Screens.homePage

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import androidx.compose.ui.platform.LocalConfiguration

val BrandColor = Color(0xFF193F6C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(navController: NavHostController) {
    val context = LocalContext.current
    var selectedCity by remember { mutableStateOf("Unknown") }
    var showCityDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var loading by remember { mutableStateOf(true) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loading = true
            getLastLocation(context, fusedLocationClient) { location ->
                selectedCity = getCityName(context, location)
                loading = false
            }
        } else {
            selectedCity = "Unknown"
            loading = false
        }
    }

    // Check for location permission and fetch location
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loading = true
            getLastLocation(context, fusedLocationClient) { location ->
                selectedCity = getCityName(context, location)
                loading = false
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        AppointmentsTopBanner(
            selectedCity = selectedCity,
            onCityClick = { showCityDialog = true },
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            loading = loading
        )
        Spacer(modifier = Modifier.height(16.dp))
        CategoryGrid(searchQuery = searchQuery.text)

        // City Selection Dialog
        if (showCityDialog) {
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
                onCitySelected = { city ->
                    selectedCity = city
                    showCityDialog = false
                },
                onDismiss = { showCityDialog = false }
            )
        }
    }
}

// Helper function to get the last known location
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

// Helper function to get the city name from location
private fun getCityName(context: android.content.Context, location: Location): String {
    val geocoder = android.location.Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    return addresses?.get(0)?.locality ?: "Unknown"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsTopBanner(
    selectedCity: String,
    onCityClick: () -> Unit,
    searchQuery: TextFieldValue,
    onSearchQueryChange: (TextFieldValue) -> Unit,
    loading: Boolean
) {
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
                            Spacer(Modifier.width(4.dp))
                            Text(
                                selectedCity,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            )

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                placeholder = { Text("Search for doctors or specialties...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Subtitle
            Text(
                "Expert healthcare at your fingertips",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun CategoryGrid(searchQuery: String) {
    val categories = listOf(
        Category("Skin Specialist", Icons.Default.Spa),
        Category("Gynecologist", Icons.Default.Female),
        Category("Urologist", Icons.Default.Male),
        Category("Child Specialist", Icons.Default.ChildCare),
        Category("Orthopedic Surgeon", Icons.Default.Accessibility),
        Category("Consultant Physician", Icons.Default.MedicalServices),
        Category("ENT Specialist", Icons.Default.Hearing),
        Category("Neurologist", Icons.Default.Memory),
        Category("Eye Specialist", Icons.Default.RemoveRedEye),
        Category("Psychiatrist", Icons.Default.Psychology),
        Category("Dentist", Icons.Default.MedicalServices),
        Category("Gastroenterologist", Icons.Default.MedicalServices),
        Category("Heart Specialist", Icons.Default.Favorite),
        Category("Pulmonologist", Icons.Default.Air),
        Category("General Physician", Icons.Default.MedicalServices),
        Category("Diabetes Specialist", Icons.Default.MedicalServices),
        Category("General Surgeon", Icons.Default.MedicalServices),
        Category("Endocrinologist", Icons.Default.MedicalServices),
        Category("Kidney Specialist", Icons.Default.MedicalServices),
        Category("Pain Management", Icons.Default.MedicalServices)
    )

    // Filter categories based on search query
    val filteredCategories = if (searchQuery.isEmpty()) {
        categories
    } else {
        categories.filter { category ->
            category.name.contains(searchQuery, ignoreCase = true)
        }
    }

    if (filteredCategories.isEmpty()) {
        // Display "Nothing related" message if no categories match the search query
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nothing related",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    } else {
        // Display the filtered categories in a grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(16.dp)
        ) {
            items(filteredCategories) { category ->
                CategoryItem(category)
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(120.dp) // Fixed height for all cards
            .clickable { /* Handle category click */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                category.icon,
                contentDescription = category.name,
                tint = BrandColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                fontSize = 14.sp, // Smaller font size
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class Category(val name: String, val icon: ImageVector)





@Composable
fun CitySelectionDialog(
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