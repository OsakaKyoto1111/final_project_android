package com.sdu.threads.domain.usecase

import com.sdu.threads.domain.repository.UserRepository
import javax.inject.Inject

class ObserveRecentSearchUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(limit: Int = 10) = repository.observeRecentSearches(limit)
}
