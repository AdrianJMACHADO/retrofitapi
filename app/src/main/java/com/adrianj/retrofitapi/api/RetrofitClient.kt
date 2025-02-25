package com.adrianj.retrofitapi.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URL base de la PokeAPI
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Servicio para hacer las llamadas a la API
    val pokeApiService: PokeApiService = retrofit.create(PokeApiService::class.java)
} 