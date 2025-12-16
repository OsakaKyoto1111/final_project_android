package com.sdu.threads.domain.usecase

import com.sdu.threads.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(emailOrUsername: String, password: String) =
        authRepository.login(emailOrUsername, password)
}
