package com.example.travelproject.screens

import android.app.DatePickerDialog
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    var selectedScreen by remember { mutableStateOf("home") }
    var savedDestination by remember { mutableStateOf("") }
    var savedStartDate by remember { mutableStateOf("") }
    var savedEndDate by remember { mutableStateOf("") }
    var savedBudget by remember { mutableStateOf("") }

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
                "home" -> HomeScreen(savedDestination, savedStartDate, savedEndDate, savedBudget) { selectedScreen = "new_travel" }
                "new_travel" -> NewTravelScreen { newDestination, newStartDate, newEndDate, newBudget ->
                    savedDestination = newDestination
                    savedStartDate = newStartDate
                    savedEndDate = newEndDate
                    savedBudget = newBudget
                }
                "about" -> AboutScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(savedDestination: String, savedStartDate: String, savedEndDate: String, savedBudget: String, onLongPress: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() }
                )
            }
    ) {
        Text(text = "Destino salvo: $savedDestination", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Data de Início: $savedStartDate", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Data Final: $savedEndDate", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Orçamento: $savedBudget", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun NewTravelScreen(onSaveData: (String, String, String, String) -> Unit) {
    TravelForm(onSaveData)
}

@Composable
fun TravelForm(onSaveData: (String, String, String, String) -> Unit) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    val context = LocalContext.current

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

        DatePickerField(label = "Data de Início", date = startDate) { startDate = it }
        Spacer(modifier = Modifier.height(8.dp))
        DatePickerField(label = "Data Final", date = endDate) { endDate = it }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = budget,
            onValueChange = { if (it.all { char -> char.isDigit() }) budget = it },
            label = { Text("Orçamento") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSaveData(destination, startDate, endDate, budget) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar")
        }
    }
}

@Composable
fun DatePickerField(label: String, date: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            onDateSelected("$selectedDay/${selectedMonth + 1}/$selectedYear")
        }, year, month, day
    )

    OutlinedTextField(
        value = date,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            Icon(
                Icons.Default.DateRange,
                contentDescription = "Select Date",
                modifier = Modifier.clickable { datePickerDialog.show() }
            )
        }
    )
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
