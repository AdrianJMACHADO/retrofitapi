package com.adrianj.retrofitapi

import com.adrianj.retrofitapi.model.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int = 20): PokemonResponse
}
