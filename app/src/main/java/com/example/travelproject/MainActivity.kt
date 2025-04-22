package com.example.travelproject

import LoginScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation2025.screen.RegisterScreen
import com.example.travelproject.authorization.screens.*
import com.example.travelproject.navigation.AuthNavigation
import com.example.travelproject.screens.ListTripsScreen
import com.example.travelproject.ui.theme.TravelProjectTheme



class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("register") { RegisterScreen(navController) }
                composable("menu") { ListTripsScreen(navController) }
                composable("editTrip/{tripId}") { backStackEntry ->
                    val tripId = backStackEntry.arguments?.getString("tripId")?.toInt() ?: 0
                    EditTripScreen(navController = navController, tripId = tripId)
                }
            }

            TravelProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthNavigation()
                }
            }
        }
    }
}
