package com.example.travelproject.screens

import android.app.DatePickerDialog
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    var selectedScreen by remember { mutableStateOf("home") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Travel") }) },
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
                "home" -> ListTripsScreen(navController)
                "new_travel" -> NewTravelScreen(navController)
                "about" -> AboutScreen()
            }
        }
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
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                            IconButton(onClick = { navController.navigate("editTrip/${trip.id}") }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { tripToDelete = trip }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

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
                            val updatedTrips = tripDao.getAllTrips().sortedByDescending { it.id }
                            withContext(Dispatchers.Main) {
                                trips = updatedTrips
                                tripToDelete = null
                            }
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
    var tipo by remember { mutableStateOf("") }

    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val tipos = listOf("Negócio", "Lazer")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Tipo de viagem", style = MaterialTheme.typography.titleMedium)
        tipos.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                RadioButton(
                    selected = tipo == item,
                    onClick = { tipo = item }
                )
                Text(text = item)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        DatePickerField(label = "Data de Início", date = startDate, dateFormatter = dateFormatter) { startDate = it }
        Spacer(modifier = Modifier.height(8.dp))
        DatePickerField(label = "Data Final", date = endDate, dateFormatter = dateFormatter) { endDate = it }
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
                if (destination.isBlank() || startDate.isBlank() || endDate.isBlank() || tipo.isBlank()) {
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
                                orcamento = budgetValue,
                                tipo = tipo
                            )
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Viagem salva com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.navigate("dashboard")
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
fun DatePickerField(label: String, date: String, dateFormatter: SimpleDateFormat, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDateSelected(dateFormatter.format(calendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        value = date,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
    )
}

@Composable
fun AboutScreen() {
    Text("O New Travel é um aplicativo desenvolvido para ajudar você a planejar e organizar suas viagens de forma prática e eficiente. Com ele, você pode registrar todas as suas viagens, escolhendo o tipo (Negócio ou Lazer), além de definir o local, a data de início e término, e o orçamento destinado à viagem.\n" +
            "\n" +
            "Nosso objetivo é oferecer uma experiência simples e funcional, permitindo que você mantenha o controle das suas viagens passadas e futuras, independentemente do motivo ou destino.\n" +
            "\n" +
            "Viajar é mais fácil com o New Travel. Organize, planeje e aproveite cada momento!")
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

fun formatCurrency(digits: String): String {
    if (digits.isEmpty()) return ""
    val parsed = digits.toBigDecimalOrNull() ?: return ""
    val formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        .format(parsed.divide(100.toBigDecimal()))
    return formatted
}
