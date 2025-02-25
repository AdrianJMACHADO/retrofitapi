package com.adrianj.retrofitapi.crud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adrianj.retrofitapi.data.FirestoreManager
import com.adrianj.retrofitapi.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterViewModel(private val firestoreManager: FirestoreManager) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterUiState())
    val uiState: StateFlow<CharacterUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            firestoreManager.getCharacters().collect { characters ->
                _uiState.update { state ->
                    state.copy(
                        characters = characters,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun addCharacter(character: Character) {
        viewModelScope.launch {
            firestoreManager.addCharacter(character.name ?: "", character.region ?: "")
        }
    }

    fun deleteCharacter(characterId: String) {
        viewModelScope.launch {
            firestoreManager.deleteCharacter(characterId)
        }
    }

    fun updateCharacter(character: Character) {
        viewModelScope.launch {
            firestoreManager.updateCharacter(character)
        }
    }

    fun onAddCharacterSelected() {
        _uiState.update { it.copy(showAddCharacterDialog = true) }
    }

    fun dismissAddCharacterDialog() {
        _uiState.update { it.copy(showAddCharacterDialog = false) }
    }
}

data class CharacterUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val showAddCharacterDialog: Boolean = false
)

class CharacterViewModelFactory(private val firestoreManager: FirestoreManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharacterViewModel(firestoreManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 