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
import com.adrianj.retrofitapi.api.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    auth: AuthManager,
    firestore: FirestoreManager,
    navigateToLogin: () -> Unit,
    navigateToDetalle: (String) -> Unit
) {
    val user = auth.getCurrentUser()
    val viewModel = PokemonViewModel()
    val pokemonList by viewModel.pokemonList.collectAsState()

    // Lógica de ScreenInicio
    val factory = HomeViewModelFactory(firestore)
    val inicioViewModel = viewModel(HomeViewModel::class.java, factory = factory)
    val uiState by inicioViewModel.uiState.collectAsState()

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Row(
//                        horizontalArrangement = Arrangement.Start,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        if (user?.photoUrl != null) {
//                            AsyncImage(
//                                model = ImageRequest.Builder(LocalContext.current)
//                                    .data(user.photoUrl)
//                                    .crossfade(true)
//                                    .build(),
//                                contentDescription = "Imagen",
//                                contentScale = ContentScale.Crop,
//                                modifier = Modifier
//                                    .clip(CircleShape)
//                                    .size(40.dp)
//                            )
//                        } else {
//                            Image(
//                                painter = painterResource(R.drawable.profile),
//                                contentDescription = "Foto de perfil por defecto",
//                                modifier = Modifier
//                                    .padding(end = 8.dp)
//                                    .size(40.dp)
//                                    .clip(CircleShape)
//                            )
//                        }
//                        Spacer(modifier = Modifier.width(10.dp))
//                        Column {
//                            Text(
//                                text = user?.displayName ?: "Anónimo",
//                                fontSize = 20.sp,
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                            Text(
//                                text = user?.email ?: "Sin correo",
//                                fontSize = 12.sp,
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Cyan)
//            )
//        },
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
            if (uiState.showAddPokemonDialog) {
                AddPokemonDialog(
                    onPokemonAdded = { pokemon ->
                        val currentUserId = auth.getCurrentUser()?.uid
                        if (currentUserId != null) {
                            inicioViewModel.addPokemon(
                                Pokemon(
                                    id = "",
                                    userId = currentUserId,
                                    idpersonaje = pokemon.idpersonaje,
                                    name = pokemon.name,
                                    tipo1 = pokemon.tipo1,
                                    tipo2 = pokemon.tipo2
                                )
                            )
                        }
                        inicioViewModel.dismisShowAddPokemonDialog()
                    },
                    onDialogDismissed = { inicioViewModel.dismisShowAddPokemonDialog() },
                    auth = auth,
                    firestoreManager = firestore
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
                            navigateToDetalle = { pokemon.id?.let { it1 -> navigateToDetalle(it1) } },
                            firestoreManager = firestore
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

@Composable
fun PokemonItem(
    pokemon: Pokemon,
    deletePokemon: () -> Unit,
    updatePokemon: (Pokemon) -> Unit,
    navigateToDetalle: () -> Unit,
    firestoreManager: FirestoreManager
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var characterName by remember { mutableStateOf<String?>(null) }
    var pokemonImageUrl by remember { mutableStateOf<String?>(null) }

    // Obtener el nombre del personaje y la imagen del Pokémon
    LaunchedEffect(pokemon.idpersonaje, pokemon.name) {
        pokemon.idpersonaje?.let { id ->
            val character = firestoreManager.getCharacterById(id)
            characterName = character?.name
        }
        
        // Obtener la imagen del Pokémon de la PokeAPI
        pokemon.name?.let { pokemonName ->
            try {
                val response = RetrofitClient.pokeApiService.getPokemonByName(pokemonName.lowercase())
                pokemonImageUrl = response.sprites.frontDefault
            } catch (e: Exception) {
                // Si hay error, dejamos la imagen como null
                pokemonImageUrl = null
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navigateToDetalle() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Imagen del Pokémon
                AsyncImage(
                    model = pokemonImageUrl ?: R.drawable.ic_launcher_foreground,
                    contentDescription = "Imagen de ${pokemon.name}",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Fit
                )

                Column {
                    Text(
                        text = pokemon.name ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Tipo: ${pokemon.tipo1}${if (pokemon.tipo2 != "Nada") " / ${pokemon.tipo2}" else ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    characterName?.let {
                        Text(
                            text = "Entrenador: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Row {
                IconButton(onClick = { showUpdateDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar")
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeletePokemonDialog(
            onConfirmDelete = {
                deletePokemon()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showUpdateDialog) {
        UpdatePokemonDialog(
            pokemon = pokemon,
            onPokemonUpdated = {
                updatePokemon(it)
                showUpdateDialog = false
            },
            onDialogDismissed = { showUpdateDialog = false }
        )
    }
}