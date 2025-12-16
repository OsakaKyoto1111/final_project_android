package com.sdu.threads.domain.usecase

import com.sdu.threads.domain.repository.UserRepository
import javax.inject.Inject

class GetCachedUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Long) = repository.getCachedUser(id)
}
