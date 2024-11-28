package com.kuroakevizago.aira.data.remote.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)
