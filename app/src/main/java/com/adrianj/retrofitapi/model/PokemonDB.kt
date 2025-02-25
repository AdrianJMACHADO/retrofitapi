package com.adrianj.retrofitapi.model

data class PokemonDB(
    val name: String = "",
    val userId: String = "",
    val idpersonaje: String = "", // Campo para la relación
    val tipo1: String = "",
    val tipo2: String = ""
)