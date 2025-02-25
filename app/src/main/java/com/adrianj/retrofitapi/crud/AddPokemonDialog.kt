package com.adrianj.retrofitapi.crud

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adrianj.retrofitapi.data.AuthManager
import com.adrianj.retrofitapi.data.FirestoreManager
import com.adrianj.retrofitapi.model.Character
import com.adrianj.retrofitapi.model.Pokemon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPokemonDialog(
    onPokemonAdded: (Pokemon) -> Unit,
    onDialogDismissed: () -> Unit,
    auth: AuthManager,
    firestoreManager: FirestoreManager
) {
    var name by remember { mutableStateOf("") }
    var tipo1 by remember { mutableStateOf("") }
    var tipo2 by remember { mutableStateOf("Nada") }
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }
    
    val characters by firestoreManager.getCharacters().collectAsState(initial = emptyList())

    val tiposPokemon = listOf(
        "Normal", "Fuego", "Agua", "Planta", "Eléctrico", "Hielo",
        "Lucha", "Veneno", "Tierra", "Volador", "Psíquico", "Bicho",
        "Roca", "Fantasma", "Dragón", "Siniestro", "Acero", "Hada", "Nada"
    )

    var expandedTipo1 by remember { mutableStateOf(false) }
    var expandedTipo2 by remember { mutableStateOf(false) }
    var expandedCharacter by remember { mutableStateOf(false) }

    AlertDialog(
        title = { Text("Añadir pokemon") },
        onDismissRequest = { onDialogDismissed() },
        confirmButton = {
            Button(
                onClick = {
                    selectedCharacter?.id?.let { characterId ->
                        val newPokemon = Pokemon(
                            userId = auth.getCurrentUser()?.uid,
                            name = name,
                            tipo1 = tipo1,
                            tipo2 = tipo2,
                            idpersonaje = characterId
                        )
                        onPokemonAdded(newPokemon)
                    }
                },
                enabled = selectedCharacter != null && name.isNotEmpty() && tipo1.isNotEmpty()
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(onClick = { onDialogDismissed() }) {
                Text("Cancelar")
            }
        },
        text = {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Selector de Personaje
                ExposedDropdownMenuBox(
                    expanded = expandedCharacter,
                    onExpandedChange = { expandedCharacter = !expandedCharacter }
                ) {
                    TextField(
                        value = selectedCharacter?.name ?: "Selecciona un entrenador",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                if (expandedCharacter) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                                "Expandir"
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCharacter,
                        onDismissRequest = { expandedCharacter = false }
                    ) {
                        characters.forEach { character ->
                            DropdownMenuItem(
                                text = { Text("${character.name} (${character.region})") },
                                onClick = {
                                    selectedCharacter = character
                                    expandedCharacter = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Selector de Tipo 1
                ExposedDropdownMenuBox(
                    expanded = expandedTipo1,
                    onExpandedChange = { expandedTipo1 = !expandedTipo1 }
                ) {
                    TextField(
                        value = tipo1,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo 1") },
                        trailingIcon = {
                            Icon(
                                if (expandedTipo1) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                                "Expandir"
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipo1,
                        onDismissRequest = { expandedTipo1 = false }
                    ) {
                        tiposPokemon.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipo1 = tipo
                                    expandedTipo1 = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selector de Tipo 2
                ExposedDropdownMenuBox(
                    expanded = expandedTipo2,
                    onExpandedChange = { expandedTipo2 = !expandedTipo2 }
                ) {
                    TextField(
                        value = tipo2,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo 2") },
                        trailingIcon = {
                            Icon(
                                if (expandedTipo2) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                                "Expandir"
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipo2,
                        onDismissRequest = { expandedTipo2 = false }
                    ) {
                        tiposPokemon.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipo2 = tipo
                                    expandedTipo2 = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}