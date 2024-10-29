package com.example.hellotabeeb.Screens.labtests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    LaunchedEffect(bookingState) {
        if (bookingState is ConfirmationViewModel.BookingState.Success) {
            onConfirmationComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Booking Confirmation",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Test: $testName")
                Text("Fee: $testFee")
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.confirmBooking(
                    BookingDetails(
                        name = name,
                        email = email,
                        phone = phone,
                        testName = testName,
                        testFee = testFee
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() &&
                    bookingState !is ConfirmationViewModel.BookingState.Loading
        ) {
            Text("Confirm Booking")
        }

        when (bookingState) {
            is ConfirmationViewModel.BookingState.Loading -> {
                CircularProgressIndicator()
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