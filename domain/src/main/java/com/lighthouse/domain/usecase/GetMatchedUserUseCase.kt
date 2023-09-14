package com.lighthouse.domain.usecase

import com.lighthouse.domain.repository.HomeRepository
import javax.inject.Inject

class GetMatchedUserUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    fun invoke(
        next: Int?,
        pageSize: Int?,
    ) = repository.getMatchedUser(next, pageSize)
}