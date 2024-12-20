package com.kuroakevizago.aira.data.remote.api

import com.kuroakevizago.aira.data.remote.request.LoginRequest
import com.kuroakevizago.aira.data.remote.request.RegisterRequest
import com.kuroakevizago.aira.data.remote.request.UpdateUsersRequest
import com.kuroakevizago.aira.data.remote.response.MusicsResponse
import com.kuroakevizago.aira.data.remote.response.DefaultResponse
import com.kuroakevizago.aira.data.remote.response.EvaluationResponse
import com.kuroakevizago.aira.data.remote.response.EvaluationsResponse
import com.kuroakevizago.aira.data.remote.response.MusicResponse
import com.kuroakevizago.aira.data.remote.response.auth.LoginResponse
import com.kuroakevizago.aira.data.remote.response.auth.RegisterResponse
import com.kuroakevizago.aira.data.remote.response.user.UserProfileResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
//    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Body requestBody: RegisterRequest
        ): RegisterResponse

    @POST("auth/signin")
    suspend fun login(
        @Body requestBody: LoginRequest
    ): LoginResponse

    @FormUrlEncoded
    @POST("auth/request-password-reset")
    suspend fun requestPasswordReset(
        @Field("email") email: String,
    ): DefaultResponse

    @FormUrlEncoded
    @POST("auth/verify-reset-token")
    suspend fun verifyResetToken(
        @Field("email") email: String,
        @Field("token") token: String,
    ): DefaultResponse

    @FormUrlEncoded
    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Field("email") email: String,
        @Field("token") token: String,
        @Field("newPassword") newPassword: String,
    ): DefaultResponse

    @FormUrlEncoded
    @PUT("users/name")
    suspend fun updateUserName(
        @Body requestBody: UpdateUsersRequest
    ): RegisterResponse

    @Multipart
    @POST("users/photo")
    suspend fun updateUserProfilePhoto(
        @Part photo: MultipartBody.Part, // Use MultipartBody.Part for file uploads
    ): RegisterResponse

    @Multipart
    @POST("evaluations/{userId}/{musicId}")
    suspend fun postEvaluation(
        @Path("userId") userId: String,
        @Path("musicId") musicId: String,
        @Part music: MultipartBody.Part
    ): EvaluationResponse

    @GET("evaluations/{userId}/{musicId}")
    suspend fun getEvaluations(
        @Path("userId") userId: String,
        @Path("musicId") musicId: String
    ): EvaluationsResponse

    @GET("evaluations/{userId}")
    suspend fun getEvaluations(
        @Path("userId") userId: String,
    ): EvaluationsResponse

    @GET("users/profile")
    suspend fun getUserProfile(): UserProfileResponse

    @GET("musics")
    suspend fun getMusics(): MusicsResponse

    @GET("user/musics")
    suspend fun getUserMusics(): MusicsResponse

    @GET("musics/{musicId}")
    suspend fun getMusicDetail(
        @Path("musicId") musicId: String
    ): MusicResponse

}

