package com.adrianj.retrofitapi.crud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adrianj.retrofitapi.data.FirestoreManager
import com.adrianj.retrofitapi.model.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(val firestoreManager: FirestoreManager) : ViewModel() {

    val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _pokemon = MutableStateFlow<Pokemon?>(null)
    val pokemon: StateFlow<Pokemon?> = _pokemon

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            firestoreManager.getPokemon().collect { pokemons ->
                _uiState.update { uiState ->
                    uiState.copy(
                        pokemons = pokemons,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun addPokemon(pokemon: Pokemon) {
        viewModelScope.launch {
            firestoreManager.addPokemon(pokemon)
        }
    }

    fun deletePokemonById(pokemonId: String) {
        if (pokemonId.isEmpty()) return
        viewModelScope.launch {
            firestoreManager.deletePokemonById(pokemonId)
        }
    }

    fun updatePokemon(asignaturaNew: Pokemon) {
        viewModelScope.launch {
            firestoreManager.updatePokemon(asignaturaNew)
        }
    }

    fun getPokemonById(pokemonId: String) {
        viewModelScope.launch {
            _pokemon.value = firestoreManager.getPokemonById(pokemonId)
        }
    }

    fun onAddPokemonSelected() {
        _uiState.update { it.copy(showAddPokemonDialog = true) }
    }

    fun dismisShowAddPokemonDialog() {
        _uiState.update { it.copy(showAddPokemonDialog = false) }
    }

    fun onLogoutSelected() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun dismisShowLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }
}

data class UiState(
    val pokemons: List<Pokemon> = emptyList(),
    val isLoading: Boolean = false,
    val showAddPokemonDialog: Boolean = false,
    val showLogoutDialog: Boolean = false
)

class HomeViewModelFactory(private val firestoreManager: FirestoreManager) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(firestoreManager) as T
    }
}