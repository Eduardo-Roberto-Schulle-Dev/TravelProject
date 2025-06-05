package com.example.travelproject.authorization.screens


import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.atividadefinal.Database.AppDatabase
import com.example.travelproject.TopAppBar.MyDatePicker
import com.example.travelproject.database.Trip
import com.example.travelproject.screens.formatCurrency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Currency
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@Composable
fun EditTripScreen(navController: NavController, tripId: Int) {
    val context = LocalContext.current
    val tripDao = AppDatabase.getDatabase(context).tripDao()

    var trip by remember { mutableStateOf<Trip?>(null) }

    var destination by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Lazer") }
    var startDate by remember { mutableStateOf(LocalDate.MIN) }
    var endDate by remember { mutableStateOf(LocalDate.MIN) }
    var budget by remember { mutableStateOf("0") }


    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(tripId) {
        trip = tripDao.getTripById(tripId)
        trip?.let {
            destination = it.destino
            startDate = try {
                LocalDate.parse(it.dataInicio, formatter)
            } catch (e: Exception) {
                null
            }

            endDate = try {
                LocalDate.parse(it.dataFinal, formatter)
            } catch (e: Exception) {
                null
            }

            budget = it.orcamento.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Editar Viagem",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destino") },
            modifier = Modifier.fillMaxWidth()
        )


        MyDatePicker(
            label = "Data inicial",
            value = if (startDate != LocalDate.MIN) startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) else "",
            onValueChange = { selectedDate: LocalDate ->
                startDate = selectedDate
            }
        )


        MyDatePicker(
            label = "Data final",
            value = if (endDate != LocalDate.MIN) endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) else "",
            onValueChange = { selectedDate: LocalDate ->
                endDate = selectedDate
            }
        )


        OutlinedTextField(
            value = budget,
            onValueChange = { input ->
                budget = formatCurrency(input)
            },
            label = { Text("Orçamento") },
            placeholder = { Text("Ex: R$ 1.000,00") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    val updatedTrip = trip?.copy(
                        destino = destination,

                    dataInicio = startDate.format(formatter),
                    dataFinal = endDate.format(formatter),
                        orcamento = budget.toDoubleOrNull() ?: 0.0
                    )

                    if (updatedTrip != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            tripDao.updateTrip(updatedTrip)
                            launch(Dispatchers.Main) {
                                Toast.makeText(context, "Viagem atualizada!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Erro ao atualizar viagem.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Salvar")
            }
        }
    }
}
@Composable
fun DatePickerField(label: String, date: String, dateFormatter:SimpleDateFormat, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
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