package com.kuroakevizago.aira.data.remote.response.user

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class UserProfileResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: UserProfileData? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class UserProfileData(

	@field:SerializedName("user_music_inprogress")
	val userMusicInProgress: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("photo_url")
	val photoUrl: String? = null,

	@field:SerializedName("user_music_finished")
	val userMusicFinished: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
) : Parcelable
