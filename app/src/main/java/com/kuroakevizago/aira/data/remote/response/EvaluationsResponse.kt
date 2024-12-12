package com.kuroakevizago.aira.data.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class EvaluationsResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: List<UserMusics?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable

@Parcelize
data class UserMusics(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("music")
	val music: MusicItem? = null,

	@field:SerializedName("evaluations")
	val evaluations: List<EvaluationsItem?>? = null,

	@field:SerializedName("user_record_path")
	val userRecordPath: String? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("user_musics_id")
	val userMusicsId: String? = null,

	@field:SerializedName("user_midi_path")
	val userMidiPath: String? = null
) : Parcelable

@Parcelize
data class EvaluationsItem(

	@field:SerializedName("confidence")
	val confidence: Float? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("mistakes")
	val mistakes: List<MistakesItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user_musics_id")
	val userMusicsId: String? = null
) : Parcelable

@Parcelize
data class MistakesItem(

	@field:SerializedName("additional_description")
	val additionalDescription: String? = null,

	@field:SerializedName("note_index")
	val noteIndex: String? = null
) : Parcelable
