package com.example.travelproject.authorization.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(onRegisterClick: () -> Unit, onLoginClick: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    @Composable
    fun LoginScreen(navController: NavController) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Button(onClick = { navController.navigate("dashboard") }) {
                Text("Entrar")
            }
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Tela de Login", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuário") }
        )
        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") }
        )
        Spacer(modifier = Modifier.height(8.dp))


        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }


        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Usuário e senha são obrigatórios!"
                } else {
                    errorMessage = ""
                    onLoginClick(username, password) // Chama a função de login
                }
            }
        ) {
            Text(text = "Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(onClick = onRegisterClick) {
            Text(text = "Registrar Conta")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onRegisterClick = {},
        onLoginClick = { _, _ -> }
    )
}
