package com.sdu.threads.domain.repository

import com.sdu.threads.domain.model.ProfileUpdate
import com.sdu.threads.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getMyProfile(): User
    suspend fun updateProfile(update: ProfileUpdate): User
    suspend fun searchUsers(query: String, limit: Int = 25): List<User>
    suspend fun getUserById(id: Long): User
    suspend fun cacheUser(user: User)
    suspend fun getCachedUser(id: Long): User?
    suspend fun addRecentSearch(query: String)
    fun observeRecentSearches(limit: Int = 10): Flow<List<String>>
    suspend fun uploadAvatar(bytes: ByteArray, fileName: String, mimeType: String): User
    suspend fun followUser(id: Long)
    suspend fun unfollowUser(id: Long)
    suspend fun getFollowers(id: Long): List<User>
    suspend fun getFollowing(id: Long): List<User>
}
