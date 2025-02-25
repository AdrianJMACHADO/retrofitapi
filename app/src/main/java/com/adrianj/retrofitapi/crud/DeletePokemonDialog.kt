package com.adrianj.retrofitapi.crud

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DeletePokemonDialog(onConfirmDelete: () -> Unit, onDismiss: () -> Unit){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Borrar pokemon") },
        text = { Text("¿Quieres borrar este pokemon?") },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}