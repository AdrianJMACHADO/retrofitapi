package com.adrianj.retrofitapi.screen

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adrianj.retrofitapi.R
import com.adrianj.retrofitapi.crud.*
import com.adrianj.retrofitapi.data.AuthManager
import com.adrianj.retrofitapi.data.FirestoreManager
import com.adrianj.retrofitapi.model.Character
import coil.compose.rememberAsyncImagePainter

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


        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Imagen de fondo
            Image(
                painter = rememberAsyncImagePainter(model = R.drawable.fondo2),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Lista de entrenadores
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) { if (!uiState.characters.isNullOrEmpty()) {
                    items(uiState.characters) { character ->
                        CharacterItem(
                            character = character,
                            onDelete = { viewModel.deleteCharacter(character.id ?: "") },
                            onUpdate = { viewModel.updateCharacter(it) }
                        )
                    }
                } else {
                    item {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay datos")
                        }
                    }
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
            FloatingActionButton(
                onClick = { viewModel.onAddCharacterSelected() },
                containerColor = Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, "Añadir Entrenador")
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Región: ${character.region}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                IconButton(onClick = { showUpdateDialog = true }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
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

    // Dialogo de eliminación de entrenador sin crear Composable para llamar a la función
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
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