package com.adrianj.retrofitapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adrianj.retrofitapi.ui.theme.RetrofitapiTheme
import com.adrianj.retrofitapi.viewmodel.PokemonViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RetrofitapiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PokemonListScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PokemonListScreen(modifier: Modifier = Modifier, viewModel: PokemonViewModel = viewModel()) {
    val pokemons = viewModel.pokemons.collectAsState().value

    androidx.compose.foundation.lazy.LazyColumn(modifier = modifier) {
        items(pokemons) { pokemon ->
            Text(text = pokemon.name)
        }
    }
}
