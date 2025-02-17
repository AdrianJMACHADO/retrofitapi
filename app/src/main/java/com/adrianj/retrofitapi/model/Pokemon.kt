package com.adrianj.retrofitapi.model

data class Pokemon(
    val id: String ?= null,
    val userId: String?,
    val name: String?,
    val tipo1: String?,
    val tipo2: String?,
)