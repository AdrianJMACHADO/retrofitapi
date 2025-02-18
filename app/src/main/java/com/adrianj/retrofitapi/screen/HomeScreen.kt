package com.adrianj.retrofitapi.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.adrianj.retrofitapi.R
import com.adrianj.retrofitapi.crud.AddPokemonDialog
import com.adrianj.retrofitapi.crud.DeletePokemonDialog
import com.adrianj.retrofitapi.crud.HomeViewModel
import com.adrianj.retrofitapi.crud.HomeViewModelFactory
import com.adrianj.retrofitapi.crud.UpdatePokemonDialog
import com.adrianj.retrofitapi.data.AuthManager
import com.adrianj.retrofitapi.data.FirestoreManager
import com.adrianj.retrofitapi.model.Pokemon
import com.adrianj.retrofitapi.viewmodel.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    auth: AuthManager,
    firestore: FirestoreManager,
    navigateToLogin: () -> Unit,
    navigateToDetalle: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val user = auth.getCurrentUser()
    val viewModel = PokemonViewModel()
    val pokemonList by viewModel.pokemonList.collectAsState()

    // Lógica de ScreenInicio
    val factory = HomeViewModelFactory(firestore)
    val inicioViewModel = viewModel(HomeViewModel::class.java, factory = factory)
    val uiState by inicioViewModel.uiState.collectAsState()

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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { inicioViewModel.onAddPokemonSelected() },
                containerColor = Color.Gray
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir pokemon")
            }
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

            if (uiState.showLogoutDialog) {
                LogoutDialog(
                    onDismiss = { inicioViewModel.dismisShowLogoutDialog() },
                    onConfirm = {
                        auth.signOut()
                        navigateToLogin()
                        inicioViewModel.dismisShowLogoutDialog()
                    }
                )
            }

            if (uiState.showAddPokemonDialog) {
                AddPokemonDialog(
                    onPokemonAdded = { pokemon ->
                        inicioViewModel.addPokemon(
                            Pokemon(
                                id = "",
                                userId = auth.getCurrentUser()?.uid,
                                pokemon.name ?: "",
                                pokemon.tipo1 ?: "",
                                pokemon.tipo2 ?: "",
                            )
                        )
                        inicioViewModel.dismisShowAddPokemonDialog()
                    },
                    onDialogDismissed = { inicioViewModel.dismisShowAddPokemonDialog() },
                    auth
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
//                items(pokemonList) { pokemon ->
//                    PokemonItem(pokemon, viewModel)
//                }

                if (!uiState.pokemons.isNullOrEmpty()) {
                    items(uiState.pokemons) { pokemon ->
                        PokemonItem(
                            pokemon = pokemon,
                            deletePokemon = {
                                inicioViewModel.deletePokemonById(pokemon.id ?: "")
                            },
                            updatePokemon = {
                                inicioViewModel.updatePokemon(it)
                            },
                            navigateToDetalle = { pokemon.id?.let { it1 -> navigateToDetalle(it1) } }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    item {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay datos")
                        }
                    }
                }
            }
        }
    }
}
//@Composable
//fun PokemonItem(pokemon: Pokemon, viewModel: PokemonViewModel) {
//    val types by viewModel.pokemonTypes.collectAsState()
//    val pokemonTypes = types[pokemon.id] ?: emptyList()
//
//    LaunchedEffect(pokemon.id) {
//        if (pokemon.id !in types) {
//            viewModel.fetchPokemonTypes(pokemon.id)
//        }
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f) // Fondo translúcido
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(16.dp)
//        ) {
//            // Imagen del Pokémon
//            Image(
//                painter = rememberAsyncImagePainter(model = pokemon.imageUrl),
//                contentDescription = "${pokemon.name} image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .size(64.dp)
//                    .padding(end = 16.dp)
//            )
//
//            // Nombre y Tipos
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = pokemon.name.replaceFirstChar { it.uppercase() },
//                    fontSize = 18.sp
//                )
//
//                // Tipos
//                if (pokemonTypes.isNotEmpty()) {
//                    Row {
//                        pokemonTypes.forEach { type ->
//                            Text(
//                                text = type.replaceFirstChar { it.uppercase() },
//                                fontSize = 14.sp,
//                                modifier = Modifier
//                                    .padding(end = 8.dp)
//                                    .background(
//                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
//                                        shape = MaterialTheme.shapes.small
//                                    )
//                                    .padding(horizontal = 8.dp, vertical = 4.dp)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun PokemonItem(
    pokemon: Pokemon,
    deletePokemon: () -> Unit,
    updatePokemon: (Pokemon) -> Unit,
    navigateToDetalle: (String) -> Unit
) {
    var showDeletePokemonDialog by remember { mutableStateOf(false) }
    var showUpdatePokemonDialog by remember { mutableStateOf(false) }

    if (showDeletePokemonDialog) {
        DeletePokemonDialog(
            onConfirmDelete = {
                deletePokemon()
                showDeletePokemonDialog = false
            },
            onDismiss = { showDeletePokemonDialog = false }
        )
    }

    if (showUpdatePokemonDialog) {
        UpdatePokemonDialog(
            pokemon = pokemon,
            onPokemonUpdated = { pokemon ->
                updatePokemon(pokemon)
                showUpdatePokemonDialog = false
            },
            onDialogDismissed = { showUpdatePokemonDialog = false }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { pokemon.id?.let { navigateToDetalle(it) } },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            pokemon.name?.let { Text(text = it, style = MaterialTheme.typography.titleMedium) }
            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Text(text = "Tipo: ${pokemon.tipo1}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.width(8.dp))
                if (pokemon.tipo2?.isNotEmpty() == true) {
                    Text(text = "/ ${pokemon.tipo2}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Función para determinar el color de la barra según el valor
            fun getStatColor(value: Int): Color {
                return when {
                    value <= 70 -> Color.Red
                    value in 71..80 -> Color(0xFFFF6600) // Naranja oscuro
                    value in 81..90 -> Color(0xFFFFA500) // Naranja estándar
                    value in 91..100 -> Color.Yellow
                    value in 101..110 -> Color(0xFFBFFF00) // Amarillo verdoso
                    value in 111..120 -> Color.Green
                    value in 121..130 -> Color(0xFF00BFA5) // Verde azulado
                    value in 131..150 -> Color.Cyan
                    value in 151..170 -> Color(0xFF00AAFF) // Azul celeste
                    value in 171..200 -> Color(0xFF80D8FF) // Celeste claro
                    else -> Color(0xFFE0F7FA) // Azul muy claro para valores mayores a 200
                }
            }

            // Función para mostrar barra de progreso con color dinámico
            @Composable
            fun StatBar(label: String, value: Int, maxValue: Int = 255) {
                Column {
                    Text(text = "$label: $value", style = MaterialTheme.typography.bodySmall)
                    LinearProgressIndicator(
                        progress = { value / maxValue.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(MaterialTheme.shapes.small),
                        color = getStatColor(value), // Color dinámico según el valor
                        trackColor = Color(0xFFBDBDBD),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showUpdatePokemonDialog = true }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { showDeletePokemonDialog = true }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
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