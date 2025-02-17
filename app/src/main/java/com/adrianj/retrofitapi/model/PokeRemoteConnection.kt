package com.adrianj.retrofitapi.model

import com.adrianj.retrofitapi.model.fuerauso.PokeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PokeRemoteConnection {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: PokeApiService = retrofit.create(PokeApiService::class.java)
}
