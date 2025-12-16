package com.sdu.threads.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email_or_username") val emailOrUsername: String,
    @SerializedName("password") val password: String
)

data class RegisterRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("password") val password: String,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("grade") val grade: String? = null,
    @SerializedName("major") val major: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("description") val description: String? = null
)

data class UpdateProfileRequestDto(
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("grade") val grade: String? = null,
    @SerializedName("major") val major: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("description") val description: String? = null
)
