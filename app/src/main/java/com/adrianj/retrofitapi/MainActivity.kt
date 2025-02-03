package com.adrianj.retrofitapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.adrianj.retrofitapi.data.AuthManager
import com.adrianj.retrofitapi.navegacion.Navegacion
import com.adrianj.retrofitapi.ui.PokemonListScreen
import com.adrianj.retrofitapi.ui.theme.RetrofitapiTheme
import com.google.firebase.ktx.Firebase
import com.google.firebase.analytics.ktx.analytics


class MainActivity : ComponentActivity() {
    val auth = AuthManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Firebase.analytics
        setContent {
            RetrofitapiTheme {
                Navegacion(auth)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
    }
}
