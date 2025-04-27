package com.example.hellotabeeb.Screens.appointments

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.navigation.NavHostController
import com.example.hellotabeeb.Screens.homePage.BrandColor
import java.util.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    navController: NavHostController,
    doctorId: String,
    viewModel: DoctorsAvailableViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val doctors by viewModel.doctors.collectAsState()
    val doctor = remember(doctorId) {
        DoctorsAvailableViewModel.getDoctorFromCache(doctorId)
            ?: doctors.find { it.id == doctorId }
    }

    // State variables
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedTimeSlot by remember { mutableStateOf<String?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf("cash") }
    var showDatePicker by remember { mutableStateOf(false) }

    // Generated time slots
    val timeSlots = remember {
        listOf("9:00 AM", "10:00 AM", "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Book Appointment", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BrandColor,
                titleContentColor = Color.White
            )
        )

        if (doctor == null) {
            // Error state
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Doctor information not available", color = Color.Gray)
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = BrandColor
                        ),
                        border = BorderStroke(1.dp, BrandColor)
                    ) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            // Doctor Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        doctor.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = BrandColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        doctor.specialization,
                        color = Color.DarkGray,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = BrandColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            doctor.address,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Date Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Select Date",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Date button
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = BrandColor
                        ),
                        border = BorderStroke(1.dp, BrandColor)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        val date = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
                            .format(selectedDate.time)
                        Text(date)
                    }

                    // Date picker dialog
                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = selectedDate.timeInMillis
                        )
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        selectedDate.timeInMillis = millis
                                    }
                                    showDatePicker = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
            }

            // Time Slots
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Select Time",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(timeSlots.size) { index ->
                            val slot = timeSlots[index]
                            val isSelected = selectedTimeSlot == slot

                            OutlinedButton(
                                onClick = { selectedTimeSlot = slot },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) BrandColor else Color.White,
                                    contentColor = if (isSelected) Color.White else BrandColor
                                ),
                                border = BorderStroke(1.dp, BrandColor)
                            ) {
                                Text(slot)
                            }
                        }
                    }
                }
            }

            // Payment Options
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Payment Method",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = BrandColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedPaymentMethod = "cash" },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedPaymentMethod == "cash") BrandColor else Color.White,
                                contentColor = if (selectedPaymentMethod == "cash") Color.White else BrandColor
                            ),
                            border = BorderStroke(1.dp, BrandColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cash")
                        }

                        OutlinedButton(
                            onClick = { selectedPaymentMethod = "card" },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedPaymentMethod == "card") BrandColor else Color.White,
                                contentColor = if (selectedPaymentMethod == "card") Color.White else BrandColor
                            ),
                            border = BorderStroke(1.dp, BrandColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Card")
                        }
                    }
                }
            }

            // Confirm Button
            Button(
                onClick = {
                    if (selectedTimeSlot != null) {
                        Toast.makeText(
                            context,
                            "Appointment booked for ${SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(selectedDate.time)} at $selectedTimeSlot",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Please select a time slot", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirm Booking", fontSize = 16.sp, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

