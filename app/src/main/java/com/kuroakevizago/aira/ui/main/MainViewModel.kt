package com.kuroakevizago.aira.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kuroakevizago.aira.data.UserRepository
import com.kuroakevizago.aira.data.local.entity.MusicEntity
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.data.pref.UserModel
import com.kuroakevizago.aira.data.remote.response.EvaluationsResponse
import com.kuroakevizago.aira.data.remote.response.MusicsResponse
import com.kuroakevizago.aira.data.remote.response.UserMusics
import com.kuroakevizago.aira.data.remote.response.user.UserProfileResponse
import com.kuroakevizago.aira.data.status.ResultStatus
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {


    private val _musics = MutableLiveData<ResultStatus<MusicsResponse>>()
    val musics: LiveData<ResultStatus<MusicsResponse>> = _musics

    private val _previouslyPlayed = MutableLiveData<ResultStatus<MusicsResponse>>()
    val previouslyPlayed: LiveData<ResultStatus<MusicsResponse>> = _previouslyPlayed

    private val _filteredMusics = MutableLiveData<List<MusicItem>>()
    val filteredMusics: LiveData<List<MusicItem>> = _filteredMusics

    private val _userEvaluations = MutableLiveData<ResultStatus<EvaluationsResponse>>()
    val userEvaluations: LiveData<ResultStatus<EvaluationsResponse>> = _userEvaluations

    private val _userData = MutableLiveData<ResultStatus<UserProfileResponse>>()
    val userData: LiveData<ResultStatus<UserProfileResponse>> = _userData

    var isEvaluationsFetched = false
    var isMusicsFetched = false
    var isPreviouslyPlayedFetched = false
    var isUserDataFetched = false
    var userId: String? = null

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun fetchUserProfile() {
        isUserDataFetched = true
        viewModelScope.launch {
            _userData.postValue(ResultStatus.Loading)
            val result = repository.getUserProfile()
            checkResultTokenStatus(result)
            _userData.postValue(result)
        }
    }

    fun fetchFeaturedMusics() {
        isMusicsFetched = true
        viewModelScope.launch {
            _musics.postValue(ResultStatus.Loading)
            val result = repository.getMusics()
            checkResultTokenStatus(result)
            _musics.postValue(result)
        }
    }

    fun fetchPreviouslyPlayedMusics() {
        isPreviouslyPlayedFetched = true
        viewModelScope.launch {
            _previouslyPlayed.postValue(ResultStatus.Loading)
            val result = repository.getUserMusics()
            checkResultTokenStatus(result)
            _previouslyPlayed.postValue(result)
        }
    }

    fun fetchUserEvaluations(userId: String) {
        isEvaluationsFetched = true
        viewModelScope.launch {
            _userEvaluations.postValue(ResultStatus.Loading)
            val result = repository.getUserEvaluations(userId)
            checkResultTokenStatus(result)
            _userEvaluations.postValue(result)
        }
    }

    fun searchMusics(query: String) {
        // Check if the current data in _musics is Success
        val currentMusics = (_musics.value as? ResultStatus.Success)?.data?.data
        if (currentMusics != null) {
            // Filter the list based on the query and remove null values
            val filteredList = currentMusics
                .filterNotNull() // Removes any null values from the list
                .filter { music ->
                    music.name?.contains(query, ignoreCase = true)!! // Match the query with the music name
                }

            // Update the filtered list if it is different from the current filtered list
            if (_filteredMusics.value != filteredList) {
                _filteredMusics.value = filteredList
            }
        }
    }

    private fun <T> checkResultTokenStatus(status: ResultStatus<T>) {
        val errorStatus = status as? ResultStatus.Error

        if (errorStatus != null && errorStatus.error.lowercase().contains("invalid token")) {
            logout()
        }
    }

    fun getBookmarkedMusics(userId: String): LiveData<List<MusicEntity>>? {
        return repository.getBookmarkedMusics(userId)
    }

}