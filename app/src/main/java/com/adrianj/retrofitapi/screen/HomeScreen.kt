package com.adrianj.retrofitapi.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.adrianj.retrofitapi.R
import com.adrianj.retrofitapi.data.AuthManager
import com.adrianj.retrofitapi.model.Pokemon
import com.adrianj.retrofitapi.viewmodel.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(auth: AuthManager, navigateToLogin: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val user = auth.getCurrentUser()
    val viewModel = PokemonViewModel()
    val pokemonList by viewModel.pokemonList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (user?.photoUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user.photoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Imagen",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(40.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.profile),
                                contentDescription = "Foto de perfil por defecto",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = user?.displayName ?: "Anónimo",
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = user?.email ?: "Sin correo",
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Cyan),
                actions = {
                    IconButton(onClick = {
                        showDialog = true
                    }) {
                        Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (showDialog) {
                LogoutDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        auth.signOut()
                        navigateToLogin()
                        showDialog = false
                    }
                )
            }

            // Imagen de fondo
            Image(
                painter = rememberAsyncImagePainter(model = R.drawable.fondo),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Lista de Pokémon
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pokemonList) { pokemon ->
                    PokemonItem(pokemon, viewModel)
                }
            }
        }
    }
}

@Composable
fun LogoutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cerrar Sesión") },
        text = {
            Text("¿Estás seguro de que deseas cerrar sesión?")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun PokemonItem(pokemon: Pokemon, viewModel: PokemonViewModel) {
    val types by viewModel.pokemonTypes.collectAsState()
    val pokemonTypes = types[pokemon.id] ?: emptyList()

    LaunchedEffect(pokemon.id) {
        if (pokemon.id !in types) {
            viewModel.fetchPokemonTypes(pokemon.id)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f) // Fondo translúcido
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Imagen del Pokémon
            Image(
                painter = rememberAsyncImagePainter(model = pokemon.imageUrl),
                contentDescription = "${pokemon.name} image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 16.dp)
            )

            // Nombre y Tipos
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    fontSize = 18.sp
                )

                // Tipos
                if (pokemonTypes.isNotEmpty()) {
                    Row {
                        pokemonTypes.forEach { type ->
                            Text(
                                text = type.replaceFirstChar { it.uppercase() },
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}