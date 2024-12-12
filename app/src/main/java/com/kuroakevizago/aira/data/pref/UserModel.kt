package com.kuroakevizago.aira.data.pref

data class UserModel(
    val userId: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)