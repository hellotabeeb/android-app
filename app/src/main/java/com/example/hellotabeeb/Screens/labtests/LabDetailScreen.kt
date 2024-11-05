package com.example.hellotabeeb.Screens.labtests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hellotabeeb.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabDetailScreen(navController: NavController, viewModel: LabDetailViewModel = viewModel()) {
    val testDetails by viewModel.testDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val selectedTests = remember { mutableStateListOf<TestDetail>() }

    // Custom colors for dark mode while keeping functionality
    val backgroundColor = Color.White
    val textColor = Color.Black
    val primaryColor = MaterialTheme.colorScheme.primary

    val filteredTests = remember(testDetails, searchQuery) {
        if (searchQuery.isEmpty()) {
            testDetails
        } else {
            testDetails.filter {
                it.name.lowercase().contains(searchQuery.lowercase())
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Lab Tests",
                                modifier = Modifier.offset(x = -16.dp),
                                color = textColor
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = textColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor
                    )
                )

                Divider(
                    color = Color.Gray,
                    thickness = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search tests...", color = textColor.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = textColor
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = backgroundColor,
                        focusedTextColor = textColor,          // Use this instead of textColor
                        unfocusedTextColor = textColor,        // Add this to ensure text color in both states
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = "Error: ${error ?: "Unknown error occurred"}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                filteredTests.isEmpty() -> {
                    Text(
                        text = if (searchQuery.isEmpty()) "No tests found" else "No matching tests found",
                        color = textColor,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    Column {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .weight(1f)
                        ) {
                            items(filteredTests) { test ->
                                val discountedPrice = test.fee.toDoubleOrNull() ?: 0.0
                                val originalPrice = discountedPrice / 0.8

                                Card(
                                    shape = RoundedCornerShape(15.dp),
                                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .height(120.dp)
                                        .clickable {
                                            if (selectedTests.contains(test)) {
                                                selectedTests.remove(test)
                                            } else {
                                                selectedTests.add(test)
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = test.name,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontSize = 18.sp,
                                                    color = textColor
                                                )
                                            )

                                            Text(
                                                text = "20% OFF",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 14.sp,
                                                    color = primaryColor
                                                )
                                            )

                                            Row {
                                                Text(
                                                    text = "RS. ${"%.2f".format(originalPrice)}",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontSize = 14.sp,
                                                        color = textColor.copy(alpha = 0.6f),
                                                        textDecoration = TextDecoration.LineThrough
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = " Now: RS. ${"%.2f".format(discountedPrice)}",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontSize = 14.sp,
                                                        color = primaryColor
                                                    )
                                                )
                                            }
                                        }

                                        if (selectedTests.contains(test)) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = primaryColor,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val testName = selectedTests.joinToString(", ") { it.name }
                                val testFee = selectedTests.joinToString(", ") { it.fee }
                                navController.navigate(Screen.Confirmation.createRoute(testName, testFee))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            enabled = selectedTests.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Proceed")
                        }
                    }
                }
            }
        }
    }
}