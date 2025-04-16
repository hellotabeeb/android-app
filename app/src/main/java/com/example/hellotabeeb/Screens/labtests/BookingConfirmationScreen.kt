package com.example.hellotabeeb.Screens.labtests

        import android.app.Activity
        import android.app.Application
        import androidx.compose.foundation.background
        import androidx.compose.foundation.border
        import androidx.compose.foundation.clickable
        import androidx.compose.foundation.layout.*
        import androidx.compose.foundation.rememberScrollState
        import androidx.compose.foundation.shape.RoundedCornerShape
        import androidx.compose.foundation.text.KeyboardOptions
        import androidx.compose.foundation.verticalScroll
        import androidx.compose.material.icons.Icons
        import androidx.compose.material.icons.filled.ArrowBack
        import androidx.compose.material.icons.filled.CheckCircle
        import androidx.compose.material.icons.filled.Error
        import androidx.compose.material.icons.filled.Upload
        import androidx.compose.material3.*
        import androidx.compose.runtime.*
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.graphics.Color
        import androidx.compose.ui.graphics.toArgb
        import androidx.compose.ui.platform.LocalView
        import androidx.compose.ui.text.SpanStyle
        import androidx.compose.ui.text.buildAnnotatedString
        import androidx.compose.ui.text.font.FontWeight
        import androidx.compose.ui.text.input.KeyboardType
        import androidx.compose.ui.text.style.TextDecoration
        import androidx.compose.ui.text.withStyle
        import androidx.compose.ui.tooling.preview.Preview
        import androidx.compose.ui.unit.dp
        import androidx.compose.ui.unit.sp
        import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hellotabeeb.Screen
import android.net.Uri
        import android.provider.OpenableColumns
        import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
        import androidx.compose.ui.platform.LocalContext
        import androidx.lifecycle.viewmodel.initializer
        import androidx.lifecycle.viewmodel.viewModelFactory
        import androidx.room.util.getColumnIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    testName: String,
    testFee: String,
    labName: String,
    onConfirmationComplete: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    navController: NavController? = null
) {
    val context = LocalContext.current
    val viewModel: ConfirmationViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ConfirmationViewModel(context.applicationContext as Application)
            }
        }
    )

    var name by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var phone by remember { mutableStateOf("") }
            var age by remember { mutableStateOf("") }
            var hasPrescription by remember { mutableStateOf(false) }
            // Add state for selected file URI
            var prescriptionUri by remember { mutableStateOf<Uri?>(null) }
            var prescriptionFileName by remember { mutableStateOf("") }
            var isFileSizeValid by remember { mutableStateOf(true) }


            // Validation states
            var isEmailValid by remember { mutableStateOf(true) }
            var isPhoneValid by remember { mutableStateOf(true) }
            var isAgeValid by remember { mutableStateOf(true) }

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

            // Email validation function
            fun validateEmail(email: String): Boolean {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }

            // Phone validation function
            fun validatePhone(phone: String): Boolean {
                return phone.matches(Regex("^[0-9]{10,11}$"))
            }

            // Age validation function
            fun validateAge(age: String): Boolean {
                return age.isNotEmpty() && age.toIntOrNull() != null && age.toInt() in 1..120
            }

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
                            color = Color.Black
                        )
                    },
                    text = {
                        Column {
                            Text(
                                "Your lab test has been successfully booked with $labName.",
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Please check your email inbox at $email for the discount code.",
                                color = Color.Black
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
                                        color = Color.Black
                                    )
                                    Text(
                                        discountCode,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F),
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Navigate to AllLabsScreen
                                navController?.navigate(Screen.AllLabs.route) {
                                    // Pop up to the home screen to avoid a large backstack
                                    popUpTo(Screen.Home.route) { inclusive = false }
                                }
                                // Call the callback
                                onConfirmationComplete()
                            },
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

                            // Full name field
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

                            // Email field with validation
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        isEmailValid = validateEmail(it) || it.isEmpty()
                                    },
                                    label = { Text("Email Address") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    isError = !isEmailValid,
                                    trailingIcon = {
                                        if (!isEmailValid) {
                                            Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        focusedLabelColor = brandColor,
                                        unfocusedLabelColor = Color.Gray,
                                        errorBorderColor = MaterialTheme.colorScheme.error
                                    )
                                )

                                if (!isEmailValid) {
                                    Text(
                                        text = "Please enter a valid email address (e.g., example@gmail.com)",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Enter a valid email format (e.g., example@gmail.com)",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )
                                }
                            }

                            // Phone number field with validation
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = {
                                        if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                            phone = it
                                        }
                                        isPhoneValid = validatePhone(it) || it.isEmpty()
                                    },
                                    label = { Text("Phone Number") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    isError = !isPhoneValid,
                                    trailingIcon = {
                                        if (!isPhoneValid) {
                                            Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        focusedLabelColor = brandColor,
                                        unfocusedLabelColor = Color.Gray,
                                        errorBorderColor = MaterialTheme.colorScheme.error
                                    )
                                )

                                if (!isPhoneValid) {
                                    Text(
                                        text = "Please enter a valid phone number (10-11 digits)",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Enter 10-11 digit phone number",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )
                                }
                            }

                            // Age field with number validation
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = age,
                                    onValueChange = {
                                        if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                            age = it
                                        }
                                        isAgeValid = validateAge(it) || it.isEmpty()
                                    },
                                    label = { Text("Age") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    isError = !isAgeValid,
                                    trailingIcon = {
                                        if (!isAgeValid) {
                                            Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        focusedLabelColor = brandColor,
                                        unfocusedLabelColor = Color.Gray,
                                        errorBorderColor = MaterialTheme.colorScheme.error
                                    )
                                )

                                if (!isAgeValid && age.isNotEmpty()) {
                                    Text(
                                        text = "Please enter a valid age (1-120)",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                    )
                                }
                            }

                            // Prescription Upload Field
                            Spacer(modifier = Modifier.height(8.dp))

                            // Then update the file picker code to use this context:
                            val filePicker = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.GetContent()
                            ) { uri ->
                                uri?.let {
                                    // Check file size
                                    val fileSize = context.contentResolver.openFileDescriptor(uri, "r")?.statSize ?: 0
                                    if (fileSize > 3 * 1024 * 1024) { // 3MB
                                        isFileSizeValid = false
                                        return@let
                                    }

                                    // Get file name
                                    val cursor = context.contentResolver.query(uri, null, null, null, null)
                                    cursor?.use { cursor ->
                                        if (cursor.moveToFirst()) {
                                            val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                            if (displayNameIndex != -1) {
                                                prescriptionFileName = cursor.getString(displayNameIndex)
                                            }
                                        }
                                    }

                                    prescriptionUri = uri
                                    hasPrescription = true
                                    isFileSizeValid = true
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF5F5F5),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { filePicker.launch("*/*") }
                                    .border(
                                        width = 1.dp,
                                        color = if (!isFileSizeValid) MaterialTheme.colorScheme.error else Color.LightGray,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Upload,
                                        contentDescription = "Upload Prescription",
                                        tint = brandColor,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = if (hasPrescription && prescriptionUri != null)
                                                prescriptionFileName.ifEmpty { "File Selected" }
                                            else "Upload Prescription (Optional)",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Text(
                                            text = buildAnnotatedString {
                                                append("Maximum file size: ")
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("3MB")
                                                }
                                            },
                                            color = Color.Gray,
                                            fontSize = 12.sp
                                        )

                                        if (!isFileSizeValid) {
                                            Text(
                                                text = "File exceeds 3MB limit. Please choose a smaller file.",
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Booking button
                    Button(
                        onClick = {
                            // Validate all fields before proceeding
                            isEmailValid = validateEmail(email)
                            isPhoneValid = validatePhone(phone)
                            isAgeValid = validateAge(age)

                            if (isEmailValid && isPhoneValid && isAgeValid && name.isNotBlank()) {
                                viewModel.confirmBooking(
                                    BookingDetails(
                                        name = name,
                                        email = email,
                                        phone = phone,
                                        age = age,
                                        testName = testName,
                                        testFee = testFee,
                                        totalFee = totalFee.toString(),
                                        labName = labName,
                                        hasPrescription = hasPrescription,
                                        prescriptionUri = prescriptionUri  // Add this line
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && age.isNotBlank() &&
                                bookingState !is ConfirmationViewModel.BookingState.Loading,
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4782DE),
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