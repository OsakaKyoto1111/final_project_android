package com.sdu.threads.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sdu.threads.domain.model.User

@Entity(tableName = "cached_user")
data class CachedUserEntity(
    @PrimaryKey val id: Long,
    val email: String,
    val nickname: String,
    val firstName: String,
    val lastName: String,
    val grade: String,
    val major: String,
    val city: String,
    val avatarUrl: String,
    val description: String,
    val postsCount: Int,
    val followersCount: Int,
    val followingCount: Int
)

fun CachedUserEntity.toDomain(): User = User(
    id = id,
    email = email,
    nickname = nickname,
    firstName = firstName,
    lastName = lastName,
    grade = grade,
    major = major,
    city = city,
    avatarUrl = avatarUrl,
    description = description,
    postsCount = postsCount,
    followersCount = followersCount,
    followingCount = followingCount
)

fun User.toEntity(): CachedUserEntity = CachedUserEntity(
    id = id,
    email = email,
    nickname = nickname,
    firstName = firstName,
    lastName = lastName,
    grade = grade,
    major = major,
    city = city,
    avatarUrl = avatarUrl,
    description = description,
    postsCount = postsCount,
    followersCount = followersCount,
    followingCount = followingCount
)
