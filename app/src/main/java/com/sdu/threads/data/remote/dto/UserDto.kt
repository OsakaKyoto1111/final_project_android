package com.sdu.threads.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.sdu.threads.domain.model.User

data class UserDto(
    @SerializedName("id") val id: Long,
    @SerializedName("email") val email: String?,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("grade") val grade: String?,
    @SerializedName("major") val major: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("posts_count") val postsCount: Int,
    @SerializedName("followers_count") val followersCount: Int,
    @SerializedName("following_count") val followingCount: Int
)

fun UserDto.toDomain(
    fallbackEmail: String? = null,
    fallbackNickname: String? = null
): User {

    val resolvedEmail = email ?: fallbackEmail
    ?: error("User email missing in response (id=$id)")

    val resolvedNickname = nickname ?: fallbackNickname
    ?: error("User nickname missing in response (id=$id)")

    return User(
        id = id,
        email = resolvedEmail,
        nickname = resolvedNickname,
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        grade = grade.orEmpty(),
        major = major.orEmpty(),
        city = city.orEmpty(),
        avatarUrl = avatarUrl.orEmpty(),
        description = description.orEmpty(),
        postsCount = postsCount,
        followersCount = followersCount,
        followingCount = followingCount
    )
}

data class SimpleUserDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName(value = "avatar", alternate = ["avatar_url"]) val avatar: String? = null
)

fun SimpleUserDto.toDomain(): User = User(
    id = id,
    email = "",
    nickname = nickname.orEmpty(),
    firstName = "",
    lastName = "",
    grade = "",
    major = "",
    city = "",
    avatarUrl = avatar.orEmpty(),
    description = "",
    postsCount = 0,
    followersCount = 0,
    followingCount = 0
)
