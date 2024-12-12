package com.kuroakevizago.aira.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.kuroakevizago.aira.data.local.database.AiraRoomDatabase
import com.kuroakevizago.aira.data.local.entity.MusicEntity
import com.kuroakevizago.aira.data.pref.UserModel
import com.kuroakevizago.dicodingstoryapp.data.pref.UserPreference
import com.kuroakevizago.aira.data.remote.api.ApiService
import com.kuroakevizago.aira.data.remote.request.LoginRequest
import com.kuroakevizago.aira.data.remote.request.RegisterRequest
import com.kuroakevizago.aira.data.remote.request.UpdateUsersRequest
import com.kuroakevizago.aira.data.remote.response.auth.LoginResponse
import com.kuroakevizago.aira.data.remote.response.auth.RegisterResponse
import com.kuroakevizago.aira.data.remote.response.ErrorResponse
import com.kuroakevizago.aira.data.remote.response.EvaluationResponse
import com.kuroakevizago.aira.data.remote.response.EvaluationsResponse
import com.kuroakevizago.aira.data.remote.response.MusicResponse
import com.kuroakevizago.aira.data.remote.response.MusicsResponse
import com.kuroakevizago.aira.data.remote.response.UserMusics
import com.kuroakevizago.aira.data.remote.response.user.UserProfileResponse
import com.kuroakevizago.aira.data.status.ResultStatus
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val apiService: ApiService,
    private val databaseService: AiraRoomDatabase,
    private val userPreference: UserPreference
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(email: String, password: String): ResultStatus<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))

            if (response.success != false) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message}")
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        }catch (e: Exception) {
            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun register(name: String, email: String, password: String): ResultStatus<RegisterResponse> {
        return try {
            val response = apiService.register(RegisterRequest(email, password, name))

            if (response.success != false) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message}")
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun getMusics(): ResultStatus<MusicsResponse> {
        return try {
            val response = apiService.getMusics()
            if (response.success == true) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message}")
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun getUserMusics(): ResultStatus<MusicsResponse> {
        return try {
            val response = apiService.getUserMusics()
            if (response.success == true) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message}")
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
    suspend fun getUserEvaluationsByMusic(userId: String, musicId: String): ResultStatus<EvaluationsResponse> {
        return try {
            val response = apiService.getEvaluations(userId, musicId)
            if (response.success == true) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message}")
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun getUserEvaluations(userId: String): ResultStatus<EvaluationsResponse> {
        return try {
            val response = apiService.getEvaluations(userId)
            if (response.success == true) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message}")
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun getMusicDetail(musicId: String): ResultStatus<MusicResponse> {
        return try {
            val response = apiService.getMusicDetail(musicId)
            if (response.success == true) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message}")
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun getUserProfile(): ResultStatus<UserProfileResponse> {
        return try {
            val response = apiService.getUserProfile()
            if (response.success == true) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message}")
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun updateUserProfilePhoto(photoFile: File): ResultStatus<RegisterResponse> {
        return try {
            // Convert the file to RequestBody
            val requestFile = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)

            val response = apiService.updateUserProfilePhoto(body)
            if (response.success == true) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message}")
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun updateUserName(name: String): ResultStatus<RegisterResponse> {
        return try {
            val response = apiService.updateUserName(UpdateUsersRequest(name))
            if (response.success == true && response.data != null) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message}")
            }
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun insertBookmarkedMusic(music: MusicEntity): ResultStatus<String> {
        return try {
            databaseService.bookmarkDao().insertMusic(music)
            ResultStatus.Success("Successfully Insert Data")
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Something went wrong: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun removeBookmarkedMusic(musicId: String): ResultStatus<String> {
        return try {
            databaseService.bookmarkDao().removeMusic(musicId)
            ResultStatus.Success("Successfully Insert Data")
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            ResultStatus.Error("Something went wrong: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    fun getBookmarkedMusics(userId: String): LiveData<List<MusicEntity>>? {
        return try {
            return databaseService.bookmarkDao().getBookmarksForUser(userId)
        } catch (e: Exception) {
            Log.e("User Bookmarked Musics", "Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
            null
        }
    }

    suspend fun isBookmarked(musicId: String, userId: String): Boolean {
        return databaseService.bookmarkDao().isMusicBookmarked(musicId, userId)
    }

    suspend fun postMusicPredictionModel(userId: String, musicId: String, musicFile: File): ResultStatus<EvaluationResponse> {
        return try {
            // Convert the file to RequestBody
            val requestFile = musicFile.asRequestBody("audio/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", musicFile.name, requestFile)

            // Call the API
            val response = apiService.postEvaluation(userId, musicId, body)
            if (response.success == true) {
                ResultStatus.Success(response)
            } else {
                ResultStatus.Error("API call failed: ${response.message ?: "No error message"}")
            }
        } catch (e: HttpException) {
            handleHttpException(e) // Delegate to a function that parses HTTP exceptions
        } catch (e: Exception) {
            ResultStatus.Error("Unexpected error: ${e.localizedMessage ?: "Unknown error occurred"}")
        }
    }

    private fun handleHttpException(e: HttpException): ResultStatus.Error{
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody: ErrorResponse? = try {
            // Attempt to parse as JSON
            Gson().fromJson(jsonInString, ErrorResponse::class.java)
        } catch (ex: Exception) {
            // If parsing fails, handle it as a generic error
            null
        }

        // Use the error message from the parsed response if available, else provide a default message
        val errorMessage = errorBody?.message ?: "An unexpected error occurred"
        return ResultStatus.Error(errorMessage)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            databaseService: AiraRoomDatabase,
            userPreference: UserPreference,
            resetInstance: Boolean = false
        ): UserRepository {
            return if (instance == null || resetInstance) {
                synchronized(this) {
                    if (instance == null || resetInstance) {
                        instance = UserRepository(apiService, databaseService, userPreference)
                    }
                    instance!!
                }
            } else {
                instance!!
            }
        }
    }
}