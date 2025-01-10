package com.adrianj.retrofitapi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrianj.retrofitapi.PokeRemoteConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonViewModel : ViewModel() {
    private val _pokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemons: StateFlow<List<Pokemon>> = _pokemons

    init {
        viewModelScope.launch {
            try {
                val response = PokeRemoteConnection.service.getPokemons()
                _pokemons.value = response.results
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error fetching pokemons", e)
            }
        }
    }
}
