package com.sdu.threads.domain.model

data class AuthResult(
    val user: User,
    val accessToken: String
)
