package com.adrianj.retrofitapi.crud

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adrianj.retrofitapi.model.Pokemon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePokemonDialog(
    pokemon: Pokemon,
    onPokemonUpdated: (Pokemon) -> Unit,
    onDialogDismissed: () -> Unit
) {
    var name by remember { mutableStateOf(pokemon.name!!) }
    var tipo1 by remember { mutableStateOf(pokemon.tipo1!!) }
    var tipo2 by remember { mutableStateOf(pokemon.tipo2!!) }

    val tiposPokemon = listOf(
        "Normal", "Fuego", "Agua", "Planta", "Eléctrico", "Hielo",
        "Lucha", "Veneno", "Tierra", "Volador", "Psíquico", "Bicho",
        "Roca", "Fantasma", "Dragón", "Siniestro", "Acero", "Hada", "Nada"
    )

    var expandedTipo1 by remember { mutableStateOf(false) }
    var expandedTipo2 by remember { mutableStateOf(false) }

    AlertDialog(
        title = { Text(text = "Actualizar pokemon") },
        onDismissRequest = {onDialogDismissed()},
        confirmButton = {
            Button(
                onClick = {
                    val newPokemon = Pokemon(
                        id = pokemon.id,
                        userId = pokemon.userId,
                        name = name,
                        tipo1 = tipo1,
                        tipo2 = tipo2,
                    )
                    onPokemonUpdated(newPokemon)
                    onDialogDismissed()
                    name = ""
                    tipo1 = ""
                    tipo2 = ""
                }
            ) {
                Text(text = "Actualizar")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDialogDismissed() }
            ) {
                Text(text = "Cancelar")
            }
        },
        text = {
            Column() {
                TextField(
                    value = name ?: "",
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Selector de Tipo 1
                ExposedDropdownMenuBox(
                    expanded = expandedTipo1,
                    onExpandedChange = { expandedTipo1 = it }
                ) {
                    TextField(
                        value = tipo1,
                        onValueChange = {},
                        label = { Text("Tipo 1") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedTipo1) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                                contentDescription = "Desplegar lista"
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    DropdownMenu(
                        expanded = expandedTipo1,
                        onDismissRequest = { expandedTipo1 = false },
                        modifier = Modifier.height(250.dp)
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
                    onExpandedChange = { expandedTipo2 = it }
                ) {
                    TextField(
                        value = tipo2,
                        onValueChange = {},
                        label = { Text("Tipo 2") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedTipo2) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                                contentDescription = "Desplegar lista"
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    DropdownMenu(
                        expanded = expandedTipo2,
                        onDismissRequest = { expandedTipo2 = false },
                        modifier = Modifier.height(250.dp)
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