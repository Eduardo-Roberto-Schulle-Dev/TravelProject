package com.example.navigation2025.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.atividadefinal.Database.AppDatabase
import com.example.atividadefinal.Database.User
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val viewModel = remember { RegisterViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registrar Usuário", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Nome Completo") },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Nome Completo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "E-mail") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newUsername,
            onValueChange = { newUsername = it },
            label = { Text("Nome de Usuário") },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Nome de Usuário") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Senha") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Senha") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Confirmar Senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = errorMessage, color = Color.Red)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (fullName.isBlank() || email.isBlank() || newUsername.isBlank() || newPassword.isBlank()) {
                errorMessage = "Preencha todos os campos"
            } else if (!Regex("^[A-Za-z0-9._%+-]+@gmail\\.com").matches(email)) {
                errorMessage = "Insira um E-mail válido!"
            } else if (newPassword != confirmPassword) {
                errorMessage = "As senhas não coincidem"
            } else {
                val user = User(
                    username = newUsername,
                    name = fullName,
                    email = email,
                    password = newPassword
                )

                viewModel.registerUser(context, user) { success, message ->
                    errorMessage = message
                    if (success) {
                        navController.navigate("login")
                    }
                }
            }
        }) {
            Text(text = "Registrar")
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()

    MaterialTheme {
        RegisterScreen(navController)
    }
}

class RegisterViewModel : ViewModel() {
    fun registerUser(context: Context, user: User, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context.applicationContext).userDao()

            val userByUsername = db.getByUsername(user.username)
            val userByEmail = db.getByEmail(user.email)

            if (userByUsername != null) {
                onResult(false, "Nome de usuário já cadastrado!")
            } else if (userByEmail != null) {
                onResult(false, "E-mail já cadastrado!")
            } else {
                db.insertUser(user)
                onResult(true, "Usuário cadastrado com sucesso!")
            }
        }
    }
}
