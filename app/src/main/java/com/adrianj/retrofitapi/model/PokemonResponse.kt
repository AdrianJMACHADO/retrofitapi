package com.adrianj.retrofitapi.model

import com.adrianj.retrofitapi.Result

data class PokemonResponse(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<Result>
)