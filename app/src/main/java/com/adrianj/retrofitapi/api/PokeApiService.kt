package com.adrianj.retrofitapi.api

import retrofit2.http.GET
import retrofit2.http.Path
import com.google.gson.annotations.SerializedName

interface PokeApiService {
    // Obtiene los datos de un Pokémon específico por su nombre
    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): PokemonResponse
}

data class PokemonResponse(
    val sprites: Sprites
)

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String
) 