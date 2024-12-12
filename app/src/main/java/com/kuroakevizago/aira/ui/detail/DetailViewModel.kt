package com.kuroakevizago.aira.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kuroakevizago.aira.data.UserRepository
import com.kuroakevizago.aira.data.local.entity.MusicEntity
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.data.remote.response.MusicResponse
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.data.pref.UserModel
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val _musicDetail = MutableLiveData<ResultStatus<MusicResponse>>()
    val musicDetail: LiveData<ResultStatus<MusicResponse>> = _musicDetail

    var userId: String? = null
    var currentData: MusicItem? = null


    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun initExistingData(musicItem: MusicItem) {
        _musicDetail.postValue(
            ResultStatus.Success(MusicResponse(data = musicItem))
        )
    }

    fun fetchDetailData(id: String) {
        viewModelScope.launch {
            _musicDetail.postValue(ResultStatus.Loading)
            val result = repository.getMusicDetail(id)
            _musicDetail.postValue(result)
        }
    }

    fun addBookmark(music: MusicEntity) {
        viewModelScope.launch {
            repository.insertBookmarkedMusic(music)
        }
    }

    fun removeBookmark(musicId: String) {
        viewModelScope.launch {
            repository.removeBookmarkedMusic(musicId)
        }
    }

    fun isBookmarked(musicId: String, userId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.value = repository.isBookmarked(musicId, userId)
        }
        return result
    }

     fun <T> checkResultTokenStatus(status: ResultStatus<T>) {
        val errorStatus = status as? ResultStatus.Error

        if (errorStatus != null && errorStatus.error.lowercase().contains("invalid token")) {
            logout()
        }
    }

}