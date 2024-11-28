package com.kuroakevizago.aira.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kuroakevizago.aira.data.UserRepository
import com.kuroakevizago.dicodingstoryapp.data.pref.UserModel

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private var currentId: String = ""
    var isFetched = false

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun isIdValid(newId: String): Boolean {
        if (currentId != newId)
        {
            isFetched = false
            return false
        }

        return true
    }

}