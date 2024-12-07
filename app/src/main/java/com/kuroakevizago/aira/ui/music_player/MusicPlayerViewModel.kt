package com.kuroakevizago.aira.ui.music_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kuroakevizago.aira.data.UserRepository
import com.kuroakevizago.dicodingstoryapp.data.pref.UserModel
import java.io.File

class MusicPlayerViewModel(private val repository: UserRepository) : ViewModel() {

    var tempAudioFile: File? = null
    var isRecording: Boolean = false


    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}