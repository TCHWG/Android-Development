package com.kuroakevizago.dicodingstoryapp.di

import android.content.Context
import com.kuroakevizago.aira.data.UserRepository
import com.kuroakevizago.dicodingstoryapp.data.pref.UserPreference
import com.kuroakevizago.dicodingstoryapp.data.pref.dataStore
import com.kuroakevizago.aira.data.remote.api.Api
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context, resetInstance: Boolean = false): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = Api.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref, resetInstance)
    }
}