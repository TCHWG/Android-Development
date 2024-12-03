package com.kuroakevizago.dicodingstoryapp.data.pref

data class UserModel(
    val userId: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)