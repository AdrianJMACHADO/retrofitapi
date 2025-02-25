package com.adrianj.retrofitapi.crud

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adrianj.retrofitapi.data.AuthManager
import com.adrianj.retrofitapi.model.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCharacterDialog(
    onCharacterAdded: (Character) -> Unit,
    onDialogDismissed: () -> Unit,
    auth: AuthManager
) {
    var name by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDialogDismissed,
        title = { Text("Añadir Entrenador") },
        confirmButton = {
            Button(
                onClick = {
                    val newCharacter = Character(
                        userId = auth.getCurrentUser()?.uid,
                        name = name,
                        region = region
                    )
                    onCharacterAdded(newCharacter)
                },
                enabled = name.isNotEmpty() && region.isNotEmpty()
            ) {
                Text("Añadir")
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