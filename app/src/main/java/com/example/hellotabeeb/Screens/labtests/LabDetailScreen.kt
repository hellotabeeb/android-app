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
    var selectedDiscount by remember { mutableStateOf<Int?>(null) }

    val backgroundColor = Color.White
    val textColor = Color.Black
    val primaryColor = MaterialTheme.colorScheme.primary
    val accentColor = Color(0xFF4782DE)

    val filteredTests = remember(testDetails, searchQuery, selectedDiscount) {
        testDetails.filter { test ->
            val matchesSearch = if (searchQuery.isEmpty()) {
                true
            } else {
                test.name.lowercase().contains(searchQuery.lowercase())
            }

            val matchesDiscount = if (selectedDiscount == null) {
                true
            } else {
                test.discountPercentage == selectedDiscount
            }

            matchesSearch && matchesDiscount
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
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            selectedDiscount = if (selectedDiscount == 20) null else 20
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDiscount == 20) accentColor else Color.Gray.copy(alpha = 0.2f),
                            contentColor = if (selectedDiscount == 20) Color.White else textColor
                        ),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text("20% OFF")
                    }

                    Button(
                        onClick = {
                            selectedDiscount = if (selectedDiscount == 30) null else 30
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDiscount == 30) accentColor else Color.Gray.copy(alpha = 0.2f),
                            contentColor = if (selectedDiscount == 30) Color.White else textColor
                        ),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("30% OFF")
                    }
                }
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
                                val actualPrice = test.fee.toDoubleOrNull() ?: 0.0
                                val discountedPrice = actualPrice * (1 - test.discountPercentage / 100.0)

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
                                                text = "${test.discountPercentage}% OFF",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 14.sp,
                                                    color = accentColor
                                                )
                                            )

                                            Row {
                                                Text(
                                                    text = "RS. ${"%.2f".format(actualPrice)}",
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
                                                        color = accentColor
                                                    )
                                                )
                                            }
                                        }

                                        if (selectedTests.contains(test)) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = accentColor,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val testNames = selectedTests.joinToString(",") { it.name }
                                val testFees = selectedTests.joinToString(",") {
                                    val actualPrice = it.fee.toDoubleOrNull() ?: 0.0
                                    val discountedPrice = actualPrice * (1 - it.discountPercentage / 100.0)
                                    discountedPrice.toString()
                                }
                                // Encode the strings to handle spaces and special characters
                                val encodedTestNames = java.net.URLEncoder.encode(testNames, "UTF-8")
                                val encodedTestFees = java.net.URLEncoder.encode(testFees, "UTF-8")
                                navController.navigate(Screen.Confirmation.createRoute(encodedTestNames, encodedTestFees))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            enabled = selectedTests.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
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