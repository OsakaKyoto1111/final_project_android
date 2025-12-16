package com.sdu.threads.domain.usecase

import com.sdu.threads.domain.model.ProfileUpdate
import com.sdu.threads.domain.repository.UserRepository
import javax.inject.Inject

class GetMyProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = repository.getMyProfile()
}

class UpdateProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(update: ProfileUpdate) = repository.updateProfile(update)
}

class SearchUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(query: String, limit: Int = 25) = repository.searchUsers(query, limit)
}

class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Long) = repository.getUserById(id)
}

class AddRecentSearchUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(query: String) = repository.addRecentSearch(query)
}

class UploadAvatarUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(bytes: ByteArray, fileName: String, mimeType: String) =
        repository.uploadAvatar(bytes, fileName, mimeType)
}

class FollowUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Long) = repository.followUser(id)
}

class UnfollowUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Long) = repository.unfollowUser(id)
}

class GetFollowersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Long) = repository.getFollowers(id)
}

class GetFollowingUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Long) = repository.getFollowing(id)
}
