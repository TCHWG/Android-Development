package com.kuroakevizago.aira.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("code")
	val code: Int? = null,

    @field:SerializedName("data")
	val data: User? = null,

    @field:SerializedName("success")
	val success: Boolean? = null,

    @field:SerializedName("message")
	val message: String? = null,

    @field:SerializedName("status")
	val status: String? = null
)