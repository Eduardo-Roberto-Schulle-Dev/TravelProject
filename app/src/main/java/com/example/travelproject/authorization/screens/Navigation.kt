package com.example.travelproject.navigation

import LoginScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.navigation2025.screen.RegisterScreen
import com.example.travelproject.authorization.screens.EditTripScreen
import com.example.travelproject.screens.DashboardScreen

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(
                navController
            )
        }

        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }
        composable("editTrip/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")?.toInt() ?: 0
            EditTripScreen(navController = navController, tripId = tripId)
        }
    }
}