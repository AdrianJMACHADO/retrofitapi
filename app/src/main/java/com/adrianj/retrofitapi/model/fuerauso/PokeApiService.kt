package com.adrianj.retrofitapi.model.fuerauso

import retrofit2.http.GET

data class PokemonResponse(
    val results: List<Pokemon>
)

data class Pokemon(
    val name: String,
    val url: String
) {
    val id: String
        get() = url.split("/").dropLast(1).last()

    val imageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}

data class PokemonType(
    val type: Type
)

data class Type(
    val name: String
)

data class PokemonDetails(
    val types: List<PokemonType>
)


interface PokeApiService {
    @GET("pokemon?limit=151")
    suspend fun getPokemonList(): PokemonResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetails(@retrofit2.http.Path("id") id: String): PokemonDetails
}