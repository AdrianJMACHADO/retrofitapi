package com.adrianj.retrofitapi.model

// Modelo de datos para Pokémon en la aplicación
data class Pokemon(
    val id: String? = null,
    val userId: String?,
    val idpersonaje: String?,
    val name: String?,
    val tipo1: String?,
    val tipo2: String?
)