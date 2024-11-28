package com.kuroakevizago.aira.data.status

sealed class ResultStatus<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultStatus<T>()
    data class Error(val error: String) : ResultStatus<Nothing>()
    data object Loading : ResultStatus<Nothing>()
}