package com.sdu.threads.domain.repository

import kotlinx.coroutines.flow.Flow
import com.sdu.threads.domain.model.AuthResult

interface AuthRepository {
    suspend fun login(emailOrUsername: String, password: String): AuthResult
    suspend fun register(
        email: String,
        nickname: String,
        password: String,
        firstName: String?,
        lastName: String?,
        grade: String?,
        major: String?,
        city: String?
    ): AuthResult

    fun observeToken(): Flow<String>
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}
