package com.kuroakevizago.aira.ui.evaluation

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
import com.kuroakevizago.aira.data.remote.response.EvaluationsItem
import com.kuroakevizago.aira.data.remote.response.UserMusics
import kotlinx.coroutines.launch
import java.io.File

class EvaluationViewModel(private val repository: UserRepository) : ViewModel() {

    var userAudioFile: File? = null
    var musicItem: MusicItem? = null
    var userEvaluation: UserMusics? = null

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}