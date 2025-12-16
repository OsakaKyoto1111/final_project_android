package com.sdu.threads.domain.usecase

import com.sdu.threads.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        nickname: String,
        password: String,
        firstName: String?,
        lastName: String?,
        grade: String?,
        major: String?,
        city: String?
    ) = authRepository.register(
        email = email,
        nickname = nickname,
        password = password,
        firstName = firstName,
        lastName = lastName,
        grade = grade,
        major = major,
        city = city
    )
}
