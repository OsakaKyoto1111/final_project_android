package com.sdu.threads.data.repository

import com.sdu.threads.data.local.dao.UserDao
import com.sdu.threads.data.local.datastore.TokenStorage
import com.sdu.threads.data.local.entity.toEntity
import com.sdu.threads.data.remote.api.ThreadsApi
import com.sdu.threads.data.remote.dto.LoginRequestDto
import com.sdu.threads.data.remote.dto.RegisterRequestDto
import com.sdu.threads.data.remote.dto.requireData
import com.sdu.threads.data.remote.dto.toDomain
import com.sdu.threads.domain.model.AuthResult
import com.sdu.threads.domain.repository.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl @Inject constructor(
    private val api: ThreadsApi,
    private val tokenStorage: TokenStorage,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(emailOrUsername: String, password: String): AuthResult {
        val response = api.login(LoginRequestDto(emailOrUsername, password)).requireData().toDomain()
        tokenStorage.saveToken(response.accessToken)
        userDao.upsertUser(response.user.toEntity())
        return response
    }

    override suspend fun register(
        email: String,
        nickname: String,
        password: String,
        firstName: String?,
        lastName: String?,
        grade: String?,
        major: String?,
        city: String?
    ): AuthResult {
        val response = api.register(
            RegisterRequestDto(
                email = email,
                nickname = nickname,
                password = password,
                firstName = firstName,
                lastName = lastName,
                grade = grade,
                major = major,
                city = city
            )
        ).requireData().toDomain()
        tokenStorage.saveToken(response.accessToken)
        userDao.upsertUser(response.user.toEntity())
        return response
    }

    override fun observeToken(): Flow<String> = tokenStorage.tokenFlow

    override suspend fun saveToken(token: String) {
        tokenStorage.saveToken(token)
    }

    override suspend fun clearToken() {
        tokenStorage.clear()
        userDao.clearUsers()
    }
}
