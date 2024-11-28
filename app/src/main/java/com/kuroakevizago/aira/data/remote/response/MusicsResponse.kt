package com.kuroakevizago.aira.data.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class MusicsResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<MusicItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class MusicItem(

	@field:SerializedName("difficulty")
	val difficulty: String? = null,

	@field:SerializedName("music_description")
	val musicDescription: String? = null,

	@field:SerializedName("author")
	val author: String? = null,

	@field:SerializedName("name")
	val name: String? = null
) : Parcelable
