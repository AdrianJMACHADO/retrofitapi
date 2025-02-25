package com.adrianj.retrofitapi.crud

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adrianj.retrofitapi.model.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateCharacterDialog(
    character: Character,
    onCharacterUpdated: (Character) -> Unit,
    onDialogDismissed: () -> Unit
) {
    var name by remember { mutableStateOf(character.name ?: "") }
    var region by remember { mutableStateOf(character.region ?: "") }

    AlertDialog(
        onDismissRequest = onDialogDismissed,
        title = { Text("Actualizar Entrenador") },
        confirmButton = {
            Button(
                onClick = {
                    val updatedCharacter = character.copy(
                        name = name,
                        region = region
                    )
                    onCharacterUpdated(updatedCharacter)
                },
                enabled = name.isNotEmpty() && region.isNotEmpty()
            ) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            Button(onClick = onDialogDismissed) {
                Text("Cancelar")
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                TextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Región") }
                )
            }
        }
    )
} 