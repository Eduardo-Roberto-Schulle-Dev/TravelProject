package com.example.travelproject.screens

import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.travelproject.TopAppBar.MyDatePicker
import com.example.travelproject.TopAppBar.generateSuggestionFromGemini
import com.example.travelproject.database.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.typeOf

@RequiresApi(Build.VERSION_CODES.O)
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
    var isDialogOpen by remember { mutableStateOf(false) }
    var aiSuggestion by remember { mutableStateOf("") }

    var trips by remember { mutableStateOf(emptyList<Trip>()) }
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }
    var travelPlan by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        trips = tripDao.getAllTrips().sortedByDescending { it.id }
    }


    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
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

                            Button(
                                onClick = {
                                    isDialogOpen = true

                                    if (trip.sugestao == ""){
                                        isLoading = true

                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                val sugestao = generateSuggestionFromGemini(trip.destino, trip.dataInicio, trip.dataFinal, trip.orcamento, trip.tipo)

                                                val updatedTrip = trip.copy(sugestao = sugestao)
                                                tripDao.updateTrip(updatedTrip)

                                                trips = tripDao.getAllTrips().sortedByDescending { it.id }

                                                withContext(Dispatchers.Main) {
                                                    aiSuggestion = sugestao
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    aiSuggestion = "Erro ao gerar sugestão: ${e.message}"
                                                }
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    } else {
                                        aiSuggestion = trip.sugestao
                                    }
                                },
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                Text("Sugestão de Roteiro")
                            }

                        }

                    }
                }
            }
        }
    }

    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            confirmButton = {
                TextButton(onClick = { isDialogOpen = false }) {
                    Text("Fechar")
                }
            },
            title = {
                Text("Sugestões de Destinos")
            },
            text = {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Gerando sugestões...")
                    }
                } else {
                    // Conteúdo rolável para sugestões longas
                    Box(
                        modifier = Modifier
                            .heightIn(max = 200.dp) // Limita a altura do conteúdo para habilitar scroll
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(aiSuggestion)
                    }
                }
            }
        )
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



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewTravelScreen(navController: NavController) {
    TravelForm(navController)
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TravelForm(navController: NavController) {
    var startDate by remember { mutableStateOf(LocalDate.MIN) }
    var endDate by remember { mutableStateOf(LocalDate.MIN) }
    var budget by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var travelPlan by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val tipos = listOf("Negócio", "Lazer")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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

        MyDatePicker(
            label = "Data inicial",
            value = if (startDate != LocalDate.MIN) startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) else "",
            onValueChange = { selectedDate: LocalDate ->
                startDate = selectedDate
            }
        )

        Spacer(modifier = Modifier.height(8.dp))


        MyDatePicker(
            label = "Data final",
            value = if (endDate != LocalDate.MIN) endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) else "",
            onValueChange = { selectedDate: LocalDate ->
                endDate = selectedDate
            }
        )
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
                if (destination.isBlank() || startDate == LocalDate.MIN || endDate == LocalDate.MIN) {
                    Toast.makeText(context, "Preencha destino, data de início e data final", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                travelPlan = ""
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val budgetClean = budget.replace(Regex("[R$\\s.]"), "").replace(",", ".")
                        val budgetValue = budgetClean.toDoubleOrNull() ?: 0.0

                        val sugestao = generateSuggestionFromGemini(destination, endDate.toString(), startDate.toString(), budgetValue, tipo)
                        withContext(Dispatchers.Main) {
                            travelPlan = sugestao
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            travelPlan = "Erro ao gerar sugestão: ${e.message}"
                        }
                    } finally {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gerar Roteiro")
        }

        Text(
            text = "Sugestão de Roteiro:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        when {
            isLoading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            travelPlan.isNotBlank() -> {
                Text(
                    text = travelPlan,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (destination.isBlank() || startDate == LocalDate.MIN || endDate == LocalDate.MIN) {
                    Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                } else {
                    val budgetClean = budget.replace(Regex("[R$\\s.]"), "").replace(",", ".")
                    val budgetValue = budgetClean.toDoubleOrNull() ?: 0.0

                    CoroutineScope(Dispatchers.IO).launch {
                        tripDao.insertTrip(
                            Trip(
                                destino = destination,
                                dataInicio = startDate.format(formatter),
                                dataFinal = endDate.format(formatter),
                                orcamento = budgetValue,
                                tipo = tipo,
                                sugestao = ""
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
fun DatePickerField(
    label: String,
    date: String,
    dateFormatter: SimpleDateFormat,
    onDateSelected: (String) -> Unit
) {
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
    ) {
        OutlinedTextField(
            value = date,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
    }
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