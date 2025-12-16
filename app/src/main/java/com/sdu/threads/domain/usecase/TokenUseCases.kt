package com.sdu.threads.domain.usecase

import javax.inject.Inject
import com.sdu.threads.domain.repository.AuthRepository

class ObserveTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.observeToken()
}

class SaveTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(token: String) = repository.saveToken(token)
}

class ClearTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.clearToken()
}
