package com.sdu.threads.data.repository

import com.sdu.threads.data.local.dao.RecentSearchDao
import com.sdu.threads.data.local.dao.UserDao
import com.sdu.threads.data.local.entity.RecentSearchEntity
import com.sdu.threads.data.local.entity.toDomain
import com.sdu.threads.data.local.entity.toEntity
import com.sdu.threads.data.remote.api.ThreadsApi
import com.sdu.threads.data.remote.dto.UpdateProfileRequestDto
import com.sdu.threads.data.remote.dto.requireData
import com.sdu.threads.data.remote.dto.toDomain
import com.sdu.threads.domain.model.ProfileUpdate
import com.sdu.threads.domain.model.User
import com.sdu.threads.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepositoryImpl @Inject constructor(
    private val api: ThreadsApi,
    private val userDao: UserDao,
    private val recentSearchDao: RecentSearchDao
) : UserRepository {

    override suspend fun getMyProfile(): User {
        val cached = userDao.getAnyUser()
        val user = api.getMyProfile().requireData().toDomain(
            fallbackEmail = cached?.email,
            fallbackNickname = cached?.nickname
        )
        cacheUser(user)
        return user
    }

    override suspend fun updateProfile(update: ProfileUpdate): User {
        val body = UpdateProfileRequestDto(
            firstName = update.firstName,
            lastName = update.lastName,
            avatarUrl = update.avatarUrl,
            grade = update.grade,
            major = update.major,
            city = update.city,
            description = update.description
        )
        val cached = userDao.getAnyUser()
        val user = api.updateProfile(body).requireData().toDomain(
            fallbackEmail = cached?.email,
            fallbackNickname = cached?.nickname
        )
        cacheUser(user)
        return user
    }


    override suspend fun searchUsers(query: String, limit: Int): List<User> {
        val response = api.searchUsers(query).requireData()
        addRecentSearch(query)
        return response.map { it.toDomain() }
    }

    override suspend fun getUserById(id: Long): User {
        getCachedUser(id)?.let { return it }

        val cached = userDao.getAnyUser()
        val user = api.getUserById(id).requireData().toDomain(
            fallbackEmail = cached?.email,
            fallbackNickname = cached?.nickname
        )

        cacheUser(user)
        return user
    }


    override suspend fun cacheUser(user: User) {
        userDao.upsertUser(user.toEntity())
    }

    override suspend fun getCachedUser(id: Long): User? {
        return userDao.getUser(id)?.toDomain()
    }

    override suspend fun uploadAvatar(bytes: ByteArray, fileName: String, mimeType: String): User {
        val cached = userDao.getAnyUser()

        val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("files", fileName, body)

        val uploadedPaths = api.uploadAvatar(listOf(part)).requireData()
        val avatarUrl = uploadedPaths.firstOrNull()
            ?: error("Avatar upload failed: empty response")

        val user = api.updateProfile(UpdateProfileRequestDto(avatarUrl = avatarUrl))
            .requireData()
            .toDomain(
                fallbackEmail = cached?.email,
                fallbackNickname = cached?.nickname
            )

        cacheUser(user)
        return user
    }

    override suspend fun followUser(id: Long) {
        api.followUser(id).requireData()
    }

    override suspend fun unfollowUser(id: Long) {
        api.unfollowUser(id).requireData()
    }

    override suspend fun getFollowers(id: Long): List<User> {
        return api.getFollowers(id).requireData().map { it.toDomain() }
    }

    override suspend fun getFollowing(id: Long): List<User> {
        return api.getFollowing(id).requireData().map { it.toDomain() }
    }

    override suspend fun addRecentSearch(query: String) {
        if (query.isBlank()) return
        recentSearchDao.upsertSearch(
            RecentSearchEntity(
                query = query,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override fun observeRecentSearches(limit: Int) =
        recentSearchDao.observeRecent(limit).map { list -> list.map { it.query } }
}
