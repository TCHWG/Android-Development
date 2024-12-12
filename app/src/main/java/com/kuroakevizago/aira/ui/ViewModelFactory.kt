package com.kuroakevizago.aira.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kuroakevizago.aira.data.UserRepository
import com.kuroakevizago.dicodingstoryapp.di.Injection
import com.kuroakevizago.aira.ui.detail.DetailViewModel
import com.kuroakevizago.aira.ui.auth.login.LoginViewModel
import com.kuroakevizago.aira.ui.evaluation.EvaluationViewModel
import com.kuroakevizago.aira.ui.main.MainViewModel
import com.kuroakevizago.aira.ui.music_player.MusicPlayerViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MusicPlayerViewModel::class.java) -> {
                MusicPlayerViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EvaluationViewModel::class.java) -> {
                EvaluationViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context, resetInstance:Boolean = false): ViewModelFactory {
            if (INSTANCE == null || resetInstance) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideUserRepository(context, resetInstance))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}