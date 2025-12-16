package com.sdu.threads.util

import retrofit2.HttpException
import java.io.IOException


sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}


fun <T> Result<T>.toApiResult(): ApiResult<T> {
    return when {
        isSuccess -> ApiResult.Success(getOrNull()!!)
        else -> {
            val exception = exceptionOrNull()!!
            when (exception) {
                is HttpException -> {
                    when (exception.code()) {
                        401 -> ApiResult.Error("Unauthorized. Please login again.", 401)
                        400 -> ApiResult.Error(
                            exception.message() ?: "Bad request. Please check your input.",
                            400
                        )
                        404 -> ApiResult.Error("Resource not found.", 404)
                        500 -> ApiResult.Error("Server error. Please try again later.", 500)
                        else -> ApiResult.Error(
                            exception.message() ?: "Request failed with code ${exception.code()}",
                            exception.code()
                        )
                    }
                }
                is IOException -> ApiResult.Error("Network error. Check your connection.", null)
                else -> ApiResult.Error(exception.message ?: "Unknown error occurred", null)
            }
        }
    }
}


fun ApiResult.Error.getReadableMessage(): String = message






















