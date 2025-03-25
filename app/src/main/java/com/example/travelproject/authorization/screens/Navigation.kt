package com.example.travelproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.travelproject.authorization.screens.LoginScreen
import com.example.navigation2025.screen.RegisterScreen
import com.example.travelproject.screens.DashboardScreen

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onRegisterClick = { navController.navigate("register") },
                onLoginClick = { username, password ->
                    println("Tentando login com usuário: $username e senha: $password") // Para depuração

                    if (username == "user" && password == "password") {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack("login", inclusive = false)
                }
            )
        }

        composable("dashboard") {
            DashboardScreen()
        }

        composable("login") {
            LoginScreen(
                onRegisterClick = { navController.navigate("register") },
                onLoginClick = { username, password ->
                    println("Tentando login com usuário: $username e senha: $password") // Para depuração

                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }

    }
}
