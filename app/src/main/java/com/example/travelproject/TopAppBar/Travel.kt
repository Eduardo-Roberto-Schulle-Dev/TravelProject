package com.example.travelproject.screens

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.atividadefinal.Database.AppDatabase
import com.example.travelproject.database.Trip
import com.example.travelproject.database.TripDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
        val navController = rememberNavController()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (selectedScreen) {
                "home" -> ListTripsScreen(navController)
                "new_travel" -> NewTravelScreen (navController)
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
fun ListTripsScreen(navController: NavController) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()

    var trips by remember { mutableStateOf(emptyList<Trip>()) }
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }

    LaunchedEffect(Unit) {
        trips = tripDao.getAllTrips().sortedByDescending { it.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Minhas viagens",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (trips.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma viagem cadastrada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            trips.forEach { trip ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Destino: ${trip.destino}", style = MaterialTheme.typography.titleMedium)
                        Text("Início: ${formatDate(trip.dataInicio)}", style = MaterialTheme.typography.bodyMedium)
                        Text("Final: ${formatDate(trip.dataFinal)}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Orçamento: R$ ${"%.2f".format(trip.orcamento)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { navController.navigate("editTrip/${trip.id}") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { tripToDelete = trip }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Excluir",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmação de exclusão
    if (tripToDelete != null) {
        AlertDialog(
            onDismissRequest = { tripToDelete = null },
            title = { Text(text = "Excluir viagem") },
            text = { Text(text = "Tem certeza que deseja excluir esta viagem?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            tripToDelete?.let { tripDao.deleteTrip(it) }
                            trips = tripDao.getAllTrips().sortedByDescending { it.id }
                            tripToDelete = null
                        }
                    }
                ) {
                    Text("Sim", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { tripToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
fun formatDate(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = parser.parse(dateString)
        date?.let { formatter.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}




@Composable
fun NewTravelScreen(navController: NavController) {
    TravelForm(navController)
}

@Composable
fun TravelForm(navController: NavController) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()

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
            onClick = {
                if (destination.isBlank() || startDate.isBlank() || endDate.isBlank()) {
                    Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                } else {
                    val budgetClean = budget.replace(Regex("[R$\\s.]"), "").replace(",", ".")
                    val budgetValue = budgetClean.toDoubleOrNull() ?: 0.0

                    CoroutineScope(Dispatchers.IO).launch {
                        tripDao.insertTrip(
                            Trip(
                                destino = destination,
                                dataInicio = startDate,
                                dataFinal = endDate,
                                orcamento = budgetValue
                            )
                        )
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Viagem salva com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
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
