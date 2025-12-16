package com.sdu.threads.domain.model

data class User(
    val id: Long,
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
) {
    val fullName: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}
