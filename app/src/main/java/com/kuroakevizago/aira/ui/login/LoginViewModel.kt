package com.kuroakevizago.aira.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kuroakevizago.aira.data.UserRepository
import com.kuroakevizago.aira.data.remote.response.auth.LoginResponse
import com.kuroakevizago.aira.data.remote.response.auth.RegisterResponse
import com.kuroakevizago.dicodingstoryapp.data.pref.UserModel
import com.kuroakevizago.aira.data.status.ResultStatus
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginStatus = MutableLiveData<ResultStatus<LoginResponse>>()
    val loginStatus: LiveData<ResultStatus<LoginResponse>> = _loginStatus

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun register(name: String, email: String, password: String): LiveData<ResultStatus<RegisterResponse>> {
        val liveData = MutableLiveData<ResultStatus<RegisterResponse>>()
        viewModelScope.launch {
            liveData.postValue(ResultStatus.Loading)
            val result = repository.register(name, email, password)
            liveData.postValue(result)
        }
        return liveData
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginStatus.postValue(ResultStatus.Loading)
            val result = repository.login(email, password)
            _loginStatus.postValue(result)
        }
    }

}