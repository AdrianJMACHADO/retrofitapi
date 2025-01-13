package com.adrianj.retrofitapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.adrianj.retrofitapi.model.Pokemon
import com.adrianj.retrofitapi.model.PokeRemoteConnection

class PokemonViewModel : ViewModel() {
    // Lista de Pokémon
    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList

    // Mapa de tipos por ID de Pokémon
    private val _pokemonTypes = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val pokemonTypes: StateFlow<Map<String, List<String>>> = _pokemonTypes

    init {
        fetchPokemonList()
    }

    // Carga la lista de Pokémon desde la API
    private fun fetchPokemonList() {
        viewModelScope.launch {
            try {
                val response = PokeRemoteConnection.service.getPokemonList()
                _pokemonList.value = response.results // Asigna los resultados directamente
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Carga los tipos de un Pokémon específico por su ID
    fun fetchPokemonTypes(id: String) {
        viewModelScope.launch {
            try {
                if (_pokemonTypes.value.containsKey(id)) {
                    // Si los tipos ya fueron cargados, no los vuelve a cargar
                    return@launch
                }

                val details = PokeRemoteConnection.service.getPokemonDetails(id)
                val types = details.types.map { it.type.name } // Extrae los nombres de los tipos

                // Actualiza el mapa con los nuevos tipos
                _pokemonTypes.value = _pokemonTypes.value.toMutableMap().apply {
                    put(id, types)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
