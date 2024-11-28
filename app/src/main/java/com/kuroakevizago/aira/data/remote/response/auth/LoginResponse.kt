package com.kuroakevizago.aira.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class User(

	@field:SerializedName("uid")
	val uid: String? = null,

	@field:SerializedName("last_login_at")
	val lastLoginAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("provider_id")
	val providerId: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("photo_url")
	val photoUrl: Any? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class Data(

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("token")
	val token: String? = null
)
