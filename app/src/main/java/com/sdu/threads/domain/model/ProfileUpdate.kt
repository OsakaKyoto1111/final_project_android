package com.sdu.threads.domain.model

data class ProfileUpdate(
    val firstName: String? = null,
    val lastName: String? = null,
    val avatarUrl: String? = null,
    val grade: String? = null,
    val major: String? = null,
    val city: String? = null,
    val description: String? = null
)
