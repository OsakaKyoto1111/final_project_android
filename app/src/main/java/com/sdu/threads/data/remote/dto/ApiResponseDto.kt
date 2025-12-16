package com.sdu.threads.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiResponseDto<T>(
    @SerializedName("status") val status: String? = null,
    @SerializedName("data") val data: T? = null,
    @SerializedName("message") val message: String? = null
)

class ApiStatusException(message: String) : RuntimeException(message)

fun <T> ApiResponseDto<T>.requireData(): T {
    if (status.equals("error", ignoreCase = true)) {
        throw ApiStatusException(message ?: "Request failed")
    }
    return data ?: throw ApiStatusException(message ?: "Результат не найден")
}
