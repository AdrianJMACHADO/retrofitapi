package com.adrianj.retrofitapi.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.adrianj.retrofitapi.R
import com.adrianj.retrofitapi.data.AuthManager
import com.adrianj.retrofitapi.data.FirestoreManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    auth: AuthManager,      // Gestor de autenticación
    firestore: FirestoreManager,  // Gestor de Firestore
    navigateToLogin: () -> Unit   // Función de navegación a la pantalla de login
) {
    // Estado del drawer (menú lateral)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Scope para manejar corrutinas (usado para animaciones del drawer)
    val scope = rememberCoroutineScope()
    // Estado para controlar qué pantalla se muestra (Pokémon o Entrenadores)
    var currentScreen by remember { mutableStateOf(Screen.Pokemon) }
    // Estado para controlar la visibilidad del diálogo de logout
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Diálogo de confirmación para cerrar sesión
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                auth.signOut()
                navigateToLogin()
            }
        )
    }

    // Menú lateral (Drawer)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Espacio superior
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cabecera del menú con foto de perfil y email
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.profile),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(64.dp)
                                .padding(8.dp)
                        )
                        Text(
                            text = auth.getCurrentUser()?.email ?: "Usuario",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Divider()  // Línea separadora
                
                // Opciones de navegación del menú
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Pokémon") },
                    label = { Text("Pokémon") },
                    selected = currentScreen == Screen.Pokemon,
                    onClick = {
                        currentScreen = Screen.Pokemon
                        scope.launch { drawerState.close() }
                    }
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Entrenadores") },
                    label = { Text("Entrenadores") },
                    selected = currentScreen == Screen.Characters,
                    onClick = {
                        currentScreen = Screen.Characters
                        scope.launch { drawerState.close() }
                    }
                )

                // Espaciador que empuja el botón de logout al fondo
                Spacer(modifier = Modifier.weight(1f))
                Divider()
                
                // Botón de cerrar sesión
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión") },
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = { showLogoutDialog = true }
                )
            }
        }
    ) {
        // Estructura principal de la pantalla
        Scaffold(
            // Barra superior con título y botón de menú
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            when (currentScreen) {
                                Screen.Pokemon -> "Pokémon"
                                Screen.Characters -> "Entrenadores"
                            }
                        )
                    },
                    navigationIcon = {
                        // Botón para abrir el menú lateral
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    // Colores personalizados para la barra superior
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.DarkGray,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            // Contenedor principal que maneja el contenido según la pantalla seleccionada
            Box(modifier = Modifier.padding(padding)) {
                when (currentScreen) {
                    Screen.Pokemon -> HomeScreen(
                        auth = auth,
                        firestore = firestore,
                        navigateToLogin = navigateToLogin,
                        navigateToDetalle = { /* Implementar navegación a detalle */ }
                    )
                    Screen.Characters -> CharacterListScreen(
                        auth = auth,
                        firestore = firestore
                    )
                }
            }
        }
    }
}

// Componente para mostrar el diálogo de confirmación de cierre de sesión
@Composable
private fun LogoutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cerrar Sesión") },
        text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Aceptar") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// Enum que define las pantallas disponibles en la aplicación
private enum class Screen {
    Pokemon,
    Characters
} 