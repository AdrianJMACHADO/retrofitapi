package com.adrianj.retrofitapi.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adrianj.retrofitapi.crud.*
import com.adrianj.retrofitapi.data.AuthManager
import com.adrianj.retrofitapi.data.FirestoreManager
import com.adrianj.retrofitapi.model.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    auth: AuthManager,
    firestore: FirestoreManager
) {
    val viewModel: CharacterViewModel = viewModel(
        factory = CharacterViewModelFactory(firestore)
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddCharacterSelected() }
            ) {
                Icon(Icons.Default.Add, "Añadir Entrenador")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.characters) { character ->
                    CharacterItem(
                        character = character,
                        onDelete = { viewModel.deleteCharacter(character.id ?: "") },
                        onUpdate = { viewModel.updateCharacter(it) }
                    )
                }
            }

            if (uiState.showAddCharacterDialog) {
                AddCharacterDialog(
                    onCharacterAdded = { character ->
                        viewModel.addCharacter(character)
                        viewModel.dismissAddCharacterDialog()
                    },
                    onDialogDismissed = { viewModel.dismissAddCharacterDialog() },
                    auth = auth
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterItem(
    character: Character,
    onDelete: () -> Unit,
    onUpdate: (Character) -> Unit
) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = character.name ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = character.region ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row {
                IconButton(onClick = { showUpdateDialog = true }) {
                    Icon(Icons.Default.Edit, "Editar")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, "Eliminar")
                }
            }
        }
    }

    if (showUpdateDialog) {
        UpdateCharacterDialog(
            character = character,
            onCharacterUpdated = {
                onUpdate(it)
                showUpdateDialog = false
            },
            onDialogDismissed = { showUpdateDialog = false }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Entrenador") },
            text = { Text("¿Estás seguro de que quieres eliminar este entrenador?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
} 