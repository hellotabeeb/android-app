package com.example.hellotabeeb.Screens.labtests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ConfirmationScreen(
    testName: String,
    testFee: String,
    onConfirmationComplete: () -> Unit,
    viewModel: ConfirmationViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val bookingState by viewModel.bookingState.collectAsState()

    // Calculate total fee
    val testFees = testFee.split(",").mapNotNull { it.toDoubleOrNull() }
    val totalFee = testFees.sum()

    LaunchedEffect(bookingState) {
        if (bookingState is ConfirmationViewModel.BookingState.Success) {
            onConfirmationComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Booking Confirmation",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Test: $testName", color = Color.Black)
                Text("Fee: $testFee", color = Color.Black)
                Text("Total Fee: $totalFee", color = Color.Black) // Display total fee
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        Button(
            onClick = {
                viewModel.confirmBooking(
                    BookingDetails(
                        name = name,
                        email = email,
                        phone = phone,
                        testName = testName,
                        testFee = testFee,
                        totalFee = totalFee.toString() // Pass total fee
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() &&
                    bookingState !is ConfirmationViewModel.BookingState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4782DE),
                contentColor = Color.White
            )
        ) {
            Text("Confirm Booking")
        }

        when (bookingState) {
            is ConfirmationViewModel.BookingState.Loading -> {
                CircularProgressIndicator(
                    color = Color(0xFF4782DE)
                )
            }
            is ConfirmationViewModel.BookingState.Error -> {
                Text(
                    text = (bookingState as ConfirmationViewModel.BookingState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }
    }
}