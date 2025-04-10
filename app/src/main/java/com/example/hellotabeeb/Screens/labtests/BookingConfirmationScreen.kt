package com.example.hellotabeeb.Screens.labtests

    import android.app.Activity
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowBack
    import androidx.compose.material.icons.filled.CheckCircle
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.toArgb
    import androidx.compose.ui.platform.LocalView
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.core.view.WindowCompat
    import androidx.lifecycle.viewmodel.compose.viewModel

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ConfirmationScreen(
        testName: String,
        testFee: String,
        labName: String,
        onConfirmationComplete: () -> Unit,
        onBackPressed: () -> Unit = {},
        viewModel: ConfirmationViewModel = viewModel()
    ) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        val brandColor = Color(0xFF193F6C)
        val bookingState by viewModel.bookingState.collectAsState()

        // Set status bar color
        val view = LocalView.current
        val window = (view.context as? Activity)?.window
        window?.statusBarColor = brandColor.toArgb()
        WindowCompat.setDecorFitsSystemWindows(window ?: return, false)

        // Split test names and fees
        val testNames = testName.split(",")
        val testFees = testFee.split(",").mapNotNull { it.toDoubleOrNull() }
        val totalFee = testFees.sum()
        val scrollState = rememberScrollState()

        var showSuccessDialog by remember { mutableStateOf(false) }
        var discountCode by remember { mutableStateOf("") }

        LaunchedEffect(bookingState) {
            if (bookingState is ConfirmationViewModel.BookingState.Success) {
                discountCode = (bookingState as ConfirmationViewModel.BookingState.Success).discountCode
                showSuccessDialog = true
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                containerColor = Color.White,
                icon = {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2ECC71),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        "Booking Confirmed!",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black  // Ensure solid black color
                    )
                },
                text = {
                    Column {
                        Text(
                            "Your lab test has been successfully booked with $labName.",
                            color = Color.Black  // Changed from faded to solid black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Please check your email inbox at $email for the discount code.",
                            color = Color.Black  // Changed from faded to solid black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE0F2F1),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Your Discount Code:",
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black  // Changed from Gray to Black
                                )
                                Text(
                                    discountCode,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD32F2F),  // Bright red for discount code
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = onConfirmationComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = brandColor)
                    ) {
                        Text("Done", color = Color.White)
                    }
                }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Booking Confirmation",
                                modifier = Modifier.offset(x = (-16).dp),
                                color = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = brandColor
                    )
                )
            },
            containerColor = Color.White
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lab name header
                Text(
                    text = labName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = brandColor,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                // Test details card with shadow effect
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Test Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = brandColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // List of selected tests with fees
                        for (i in testNames.indices) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = testNames[i],
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black.copy(alpha = 0.8f),
                                    modifier = Modifier.weight(0.7f)
                                )

                                if (i < testFees.size) {
                                    Text(
                                        text = "Rs. ${String.format("%.2f", testFees[i])}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            if (i < testNames.size - 1) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color.LightGray
                                )
                            }
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.Gray,
                            thickness = 1.dp
                        )

                        // Total fee
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Amount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = brandColor
                            )

                            Text(
                                text = "Rs. ${String.format("%.2f", totalFee)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = brandColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User details form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Personal Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = brandColor,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedLabelColor = brandColor,
                                unfocusedLabelColor = Color.Gray
                            )
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedLabelColor = brandColor,
                                unfocusedLabelColor = Color.Gray
                            )
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedLabelColor = brandColor,
                                unfocusedLabelColor = Color.Gray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Booking button with light gray instead of dark tone
                Button(
                    onClick = {
                        viewModel.confirmBooking(
                            BookingDetails(
                                name = name,
                                email = email,
                                phone = phone,
                                testName = testName,
                                testFee = testFee,
                                totalFee = totalFee.toString(),
                                labName = labName
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() &&
                            bookingState !is ConfirmationViewModel.BookingState.Loading,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4782DE), // Light blue tone that fits with brand color
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    if (bookingState is ConfirmationViewModel.BookingState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Confirm Booking",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Error message
                if (bookingState is ConfirmationViewModel.BookingState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (bookingState as ConfirmationViewModel.BookingState.Error).message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ConfirmationScreenPreview() {
        ConfirmationScreen(
            testName = "Complete Blood Count,Liver Function Test",
            testFee = "1200.0,1500.0",
            labName = "Chughtai Lab",
            onConfirmationComplete = {},
            onBackPressed = {}
        )
    }