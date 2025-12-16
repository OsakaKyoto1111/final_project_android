package com.sdu.threads.util

import java.io.IOException
import com.sdu.threads.data.remote.dto.ApiStatusException
import org.json.JSONObject
import retrofit2.HttpException

fun Throwable.readableMessage(): String {
    return when (this) {
        is ApiStatusException -> {
            val msg = message ?: "Request failed"
            when {
                msg.contains("empty response", ignoreCase = true) -> "Результат не найден"
                msg.contains("not found", ignoreCase = true) -> "Результат не найден"
                else -> msg
            }
        }
        is HttpException -> {
            val parsed = parseHttpError(this)
            when {
                parsed != null -> parsed
                code() == 404 -> "Результат не найден"
                code() == 400 -> "Неверный запрос"
                code() == 500 -> "Ошибка сервера. Попробуйте позже"
                else -> message() ?: "Что-то пошло не так"
            }
        }
        is IOException -> "Проверьте подключение к интернету"
        else -> {
            val msg = message ?: "Что-то пошло не так"
            when {
                msg.contains("empty response", ignoreCase = true) -> "Результат не найден"
                else -> msg
            }
        }
    }
}

private fun parseHttpError(exception: HttpException): String? {
    val errorBody = exception.response()?.errorBody()?.string().orEmpty()
    if (errorBody.isBlank()) return null
    return runCatching {
        val json = JSONObject(errorBody)
        val message = json.optString("message").takeIf { it.isNotBlank() }
        val error = json.optString("error").takeIf { it.isNotBlank() }
        message ?: error
    }.getOrNull()
}
