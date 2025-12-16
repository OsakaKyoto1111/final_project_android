package com.sdu.threads.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.sdu.threads.domain.model.AuthResult

data class AuthResponseDto(
    @SerializedName("user") val user: UserDto,
    @SerializedName("access_token") val accessToken: String
)

data class AuthCheckResponseDto(
    @SerializedName("status") val status: String
)

fun AuthResponseDto.toDomain(): AuthResult = AuthResult(
    user = user.toDomain(),
    accessToken = accessToken
)
