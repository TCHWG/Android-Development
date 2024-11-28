package com.kuroakevizago.aira.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kuroakevizago.aira.data.UserRepository
import com.kuroakevizago.dicodingstoryapp.data.pref.UserModel
import com.kuroakevizago.aira.data.remote.response.MusicsResponse
import com.kuroakevizago.aira.data.remote.response.auth.User
import com.kuroakevizago.aira.data.status.ResultStatus
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {


    private val _musics = MutableLiveData<ResultStatus<MusicsResponse>>()
    val musics: LiveData<ResultStatus<MusicsResponse>> = _musics

    private val _previouslyPlayed = MutableLiveData<ResultStatus<MusicsResponse>>()
    val previouslyPlayed: LiveData<ResultStatus<MusicsResponse>> = _previouslyPlayed

    private val _userData = MutableLiveData<ResultStatus<User>>()
    val userData: LiveData<ResultStatus<User>> = _userData


    var isMusicsFetched = false
    var isPreviouslyPlayedFetched = false
    var isUserDataFetched = false

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

//
//    fun fetchUserData(auth: FirebaseAuth) {
//        isUserDataFetched = true
//        viewModelScope.launch {
//            _userData.postValue(ResultStatus.Loading)
//
//            _userData.postValue(result)
//        }
//    }

    fun fetchFeaturedMusics() {
        isMusicsFetched = true
        viewModelScope.launch {
            _musics.postValue(ResultStatus.Loading)
            val result = repository.getMusics()
            _musics.postValue(result)
        }
    }

    fun fetchPreviouslyPlayedMusics() {
        isPreviouslyPlayedFetched = true
        viewModelScope.launch {
            _previouslyPlayed.postValue(ResultStatus.Loading)
            val result = repository.getMusics()
            _previouslyPlayed.postValue(result)
        }
    }


}