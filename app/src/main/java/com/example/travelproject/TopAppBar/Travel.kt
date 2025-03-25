package com.example.travelproject.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    var selectedScreen by remember { mutableStateOf("home") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Travel") })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedScreen == "home",
                    onClick = { selectedScreen = "home" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "New Travel") },
                    label = { Text("New Travel") },
                    selected = selectedScreen == "new_travel",
                    onClick = { selectedScreen = "new_travel" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                    label = { Text("About") },
                    selected = selectedScreen == "about",
                    onClick = { selectedScreen = "about" }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (selectedScreen) {
                "home" -> HomeScreen()
                "new_travel" -> NewTravelScreen()
                "about" -> AboutScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Data de Início") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("Data Final") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            coroutineScope.launch {

                                println("Long Click Detectado!")
                            }
                        }
                    )
                }
        )
    }
}

@Composable
fun NewTravelScreen() {
    Text("New Travel Screen")
}

@Composable
fun AboutScreen() {
    Text("About Screen")
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}