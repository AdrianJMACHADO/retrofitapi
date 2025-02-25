package com.adrianj.retrofitapi.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adrianj.retrofitapi.data.AuthManager
import com.adrianj.retrofitapi.data.FirestoreManager
import com.adrianj.retrofitapi.screen.ForgotPasswordScreen
import com.adrianj.retrofitapi.screen.HomeScreen
import com.adrianj.retrofitapi.screen.LoginScreen
import com.adrianj.retrofitapi.screen.SignUpScreen
import com.adrianj.retrofitapi.screen.CharacterListScreen

@Composable
fun Navegacion(auth: AuthManager) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val firestoreManager = remember { FirestoreManager(auth, context) }

    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                auth,
                { navController.navigate(SignUp) },
                {
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                { navController.navigate(ForgotPassword) }
            )
        }

        composable<SignUp> {
            SignUpScreen(
                auth
            ) { navController.popBackStack() }
        }

        composable<Home> {
            HomeScreen(
                auth = auth,
                firestore = firestoreManager,
                navigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Home){ inclusive = true }
                    }
                },
                navigateToDetalle = { pokemonId ->
                    // Si tienes una ruta para detalles, aquí irá la navegación
                }
            )
        }

        composable<ForgotPassword> {
            ForgotPasswordScreen(
                auth
            ) { navController.navigate(Login) {
                popUpTo(Login){ inclusive = true }
            } }
        }

        composable<CharacterList> {
            CharacterListScreen(
                auth = auth,
                firestore = firestoreManager
            )
        }
    }
}