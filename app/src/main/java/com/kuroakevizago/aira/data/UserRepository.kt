package com.kuroakevizago.aira.data

import com.google.gson.Gson
import com.kuroakevizago.dicodingstoryapp.data.pref.UserModel
import com.kuroakevizago.dicodingstoryapp.data.pref.UserPreference
import com.kuroakevizago.aira.data.remote.api.ApiService
import com.kuroakevizago.aira.data.remote.request.LoginRequest
import com.kuroakevizago.aira.data.remote.request.RegisterRequest
import com.kuroakevizago.aira.data.remote.response.auth.LoginResponse
import com.kuroakevizago.aira.data.remote.response.auth.RegisterResponse
import com.kuroakevizago.aira.data.remote.response.ErrorResponse
import com.kuroakevizago.aira.data.remote.response.MusicsResponse
import com.kuroakevizago.aira.data.status.ResultStatus
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
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


    // Function to fetch a list of stories
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

//
//    // Function to fetch a specific story by ID
//    suspend fun getStoryById(id: String): ResultStatus<StoryResponse> {
//        return try {
//            val response = apiService.getStory(id)
//            if (response.error != false) {
//                ResultStatus.Success(response)
//            } else {
//                ResultStatus.Error("API call failed: ${response.message}")
//            }
//        } catch (e: HttpException) {
//            val jsonInString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
//            ResultStatus.Error(errorBody.message ?: "Unknown error")
//        }catch (e: Exception) {
//            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
//        }
//    }

//    // Function to add a story with authentication
//    suspend fun addStoryWithAuth(
//        description: String,
//        photo: File
//    ): ResultStatus<DefaultResponse> {
//        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
//        val photoPart = MultipartBody.Part.createFormData(
//            name = "photo",
//            filename = photo.name,
//            body = photo.asRequestBody("image/*".toMediaTypeOrNull())
//        )
//
//        return try {
//            val response = apiService.addStory(descriptionPart, photoPart)
//            if (response.success != false) {
//                ResultStatus.Success(response)
//            } else {
//                ResultStatus.Error("API call failed: ${response.message}")
//            }
//        } catch (e: HttpException) {
//            val jsonInString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
//            ResultStatus.Error(errorBody.message ?: "Unknown error")
//        }catch (e: Exception) {
//            ResultStatus.Error("Exception occurred: ${e.localizedMessage ?: "Unknown error"}")
//        }
//    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            resetInstance: Boolean = false
        ): UserRepository {
            return if (instance == null || resetInstance) {
                synchronized(this) {
                    if (instance == null || resetInstance) {
                        instance = UserRepository(apiService, userPreference)
                    }
                    instance!!
                }
            } else {
                instance!!
            }
        }
    }
}