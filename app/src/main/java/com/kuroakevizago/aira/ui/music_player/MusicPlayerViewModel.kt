package com.kuroakevizago.aira.ui.music_player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kuroakevizago.aira.data.UserRepository
import com.kuroakevizago.aira.data.remote.response.EvaluationResponse
import com.kuroakevizago.aira.data.remote.response.MusicItem
import com.kuroakevizago.aira.data.status.ResultStatus
import com.kuroakevizago.aira.data.pref.UserModel
import kotlinx.coroutines.launch
import java.io.File

class MusicPlayerViewModel(private val repository: UserRepository) : ViewModel() {

    private val _evaluationResponse = MutableLiveData<ResultStatus<EvaluationResponse>>()
    val evaluationResponse: LiveData<ResultStatus<EvaluationResponse>> = _evaluationResponse

    var tempAudioFile: File? = null
    var userId: String? = null
    var musicItem: MusicItem? = null
    var isRecording: Boolean = false
    var isEvaluateError: Boolean = false

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun fetchEvaluationResponse(userId: String, musicId: String, musicFile: File): LiveData<ResultStatus<EvaluationResponse>> {
        viewModelScope.launch {
            _evaluationResponse.postValue(ResultStatus.Loading)
            val result = repository.postMusicPredictionModel(userId, musicId, musicFile)
            checkResultTokenStatus(result)
            _evaluationResponse.postValue(result)
        }
        return evaluationResponse
    }

    private fun <T> checkResultTokenStatus(status: ResultStatus<T>) {
        val errorStatus = status as? ResultStatus.Error

        if (errorStatus != null && errorStatus.error.lowercase().contains("invalid token")) {
            logout()
        }
    }

}