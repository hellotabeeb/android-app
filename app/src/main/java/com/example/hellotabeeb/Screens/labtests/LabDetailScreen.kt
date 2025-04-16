package com.example.hellotabeeb.Screens.labtests

    import android.app.Activity
    import androidx.compose.animation.AnimatedVisibility
    import androidx.compose.animation.core.tween
    import androidx.compose.animation.fadeIn
    import androidx.compose.animation.fadeOut
    import androidx.compose.foundation.ExperimentalFoundationApi
    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.lazy.rememberLazyListState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.*
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.*
    import androidx.compose.material.icons.outlined.Search
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.toArgb
    import androidx.compose.ui.platform.LocalView
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextDecoration
    import androidx.compose.ui.text.style.TextOverflow
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.core.view.WindowCompat
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavController
    import com.example.hellotabeeb.Screen
    import kotlinx.coroutines.launch

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun LabDetailScreen(navController: NavController, labName: String, viewModel: LabDetailViewModel = viewModel()) {
        // Set status bar color
        val view = LocalView.current
        val window = (view.context as? Activity)?.window
        val primaryColor = Color(0xFF193F6C)
        window?.statusBarColor = primaryColor.toArgb()
        WindowCompat.setDecorFitsSystemWindows(window ?: return, false)

        // State variables
        val testDetails by viewModel.testDetails.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val error by viewModel.error.collectAsState()
        var searchQuery by remember { mutableStateOf("") }
        val selectedTests = remember { mutableStateListOf<TestDetail>() }
        var selectedDiscount by remember { mutableStateOf<Int?>(null) }
        val lazyListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        var isSearchVisible by remember { mutableStateOf(false) }

        // Theme colors
        val brandColor = Color(0xFF193F6C)
        val accentColor = Color(0xFF4782DE)
        val successGreen = Color(0xFF2ECC71)
        val backgroundColor = Color(0xFFF8F9FA)
        val textPrimary = Color(0xFF2D3748)
        val textSecondary = Color(0xFF718096)

        // Fetch tests when screen loads
        LaunchedEffect(labName) {
            viewModel.fetchTestDetails(labName)
        }

        // Filter tests based on search query and discount selection
        val filteredTests = remember(testDetails, searchQuery, selectedDiscount) {
            testDetails.filter { test ->
                val matchesSearch = if (searchQuery.isEmpty()) true
                    else test.name.lowercase().contains(searchQuery.lowercase())

                val matchesDiscount = if (labName == "Chughtai Lab" && selectedDiscount != null)
                    test.discountPercentage == selectedDiscount
                else true

                matchesSearch && matchesDiscount
            }
        }

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = labName,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                                Icon(
                                    imageVector = if (isSearchVisible) Icons.Default.Close else Icons.Outlined.Search,
                                    contentDescription = if (isSearchVisible) "Close Search" else "Search",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = brandColor
                        )
                    )

                    AnimatedVisibility(
                        visible = isSearchVisible,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Search tests...", color = Color.Gray) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = brandColor
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Color.Black  // Changed from Gray to Black
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color.White,
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = Color.LightGray,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }

                    // Discount filters for Chughtai Lab
                    if (labName == "Chughtai Lab") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(backgroundColor)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Text(
                                text = "Filter by discount:",
                                color = textSecondary,
                                modifier = Modifier
                                    .padding(end = 16.dp, top = 6.dp)
                            )

                            FilterChip(
                                selected = selectedDiscount == 20,
                                onClick = { selectedDiscount = if (selectedDiscount == 20) null else 20 },
                                label = { Text("20% OFF") },
                                leadingIcon = if (selectedDiscount == 20) {
                                    { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accentColor,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            FilterChip(
                                selected = selectedDiscount == 30,
                                onClick = { selectedDiscount = if (selectedDiscount == 30) null else 30 },
                                label = { Text("30% OFF") },
                                leadingIcon = if (selectedDiscount == 30) {
                                    { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accentColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Divider()
                }
            },
            bottomBar = {
                if (selectedTests.isNotEmpty()) {
                    Surface(
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Only show the test count, remove price information
                                Text(
                                    text = "${selectedTests.size} test${if (selectedTests.size > 1) "s" else ""} selected",
                                    color = textPrimary,
                                    fontWeight = FontWeight.Medium
                                )

                                Button(
                                    onClick = {
                                        val testNames = selectedTests.joinToString(",") { it.name }
                                        val testFees = selectedTests.joinToString(",") {
                                            val price = it.fee.toDoubleOrNull() ?: 0.0
                                            val discountedPrice = price * (1 - it.discountPercentage / 100.0)
                                            discountedPrice.toString()
                                        }
                                        val encodedTestNames = java.net.URLEncoder.encode(testNames, "UTF-8")
                                        val encodedTestFees = java.net.URLEncoder.encode(testFees, "UTF-8")
                                        val encodedLabName = java.net.URLEncoder.encode(labName, "UTF-8")
                                        navController.navigate(Screen.Confirmation.createRoute(encodedTestNames, encodedTestFees, encodedLabName))
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = brandColor
                                    ),
                                    modifier = Modifier.height(48.dp),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text(
                                        "Proceed to Booking",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            },
            containerColor = backgroundColor
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    isLoading -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = accentColor
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading tests...",
                                color = textSecondary
                            )
                        }
                    }
                    error != null -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Error: ${error ?: "Unknown error occurred"}",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.fetchTestDetails(labName) },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                    filteredTests.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = textSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isEmpty()) "No tests found for this lab"
                                else "No tests matching \"$searchQuery\"",
                                color = Color.Black  // Changed from textPrimary to solid black
                            )
                            if (searchQuery.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = { searchQuery = "" }
                                ) {
                                    Text("Clear Search")
                                }
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            state = lazyListState,
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = if (selectedTests.isNotEmpty()) 96.dp else 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (!isSearchVisible && searchQuery.isEmpty()) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Text(
                                            text = "${filteredTests.size} Tests Available",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = textPrimary
                                        )

                                        if (selectedDiscount != null) {
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = accentColor.copy(alpha = 0.15f),
                                                contentColor = brandColor,
                                                modifier = Modifier.clickable { selectedDiscount = null }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "${selectedDiscount}% OFF",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Clear filter",
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            items(filteredTests) { test ->
                                val actualPrice = test.fee.toDoubleOrNull() ?: 0.0
                                val discountedPrice = actualPrice * (1 - test.discountPercentage / 100.0)
                                val isSelected = selectedTests.contains(test)

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) accentColor.copy(alpha = 0.1f) else Color.White
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 2.dp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isSelected) {
                                                selectedTests.remove(test)
                                            } else {
                                                selectedTests.add(test)
                                            }
                                        }
                                        .animateItemPlacement()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) accentColor else Color.LightGray.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 16.dp)
                                        ) {

                                            Text(
                                                text = test.name,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color.Black  // Changed from textPrimary to pure black
                                                )
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Badge(
                                                containerColor = when (test.discountPercentage) {
                                                    30 -> successGreen.copy(alpha = 0.2f)
                                                    else -> accentColor.copy(alpha = 0.2f)
                                                },
                                                contentColor = when (test.discountPercentage) {
                                                    30 -> successGreen
                                                    else -> accentColor
                                                }
                                            ) {
                                                Text(
                                                    text = "${test.discountPercentage}% OFF",
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp)
                                                )
                                            }
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Text(
                                                text = "Rs. ${"%.0f".format(actualPrice)}",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = textSecondary,
                                                    textDecoration = TextDecoration.LineThrough
                                                )
                                            )

                                            Text(
                                                text = "Rs. ${"%.0f".format(discountedPrice)}",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    color = brandColor,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Scroll to top FAB
                        AnimatedVisibility(
                            visible = lazyListState.firstVisibleItemIndex > 5,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(
                                    end = 16.dp,
                                    bottom = if (selectedTests.isNotEmpty()) 96.dp else 16.dp
                                )
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    coroutineScope.launch {
                                        lazyListState.animateScrollToItem(0)
                                    }
                                },
                                containerColor = accentColor,
                                contentColor = Color.White
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
                            }
                        }
                    }
                }
            }
        }
    }