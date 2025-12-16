package com.sdu.threads.data.remote.dto

import com.sdu.threads.domain.model.User

typealias SearchResponseDto = List<UserDto>

fun ApiResponseDto<UserDto>.toUserDomain(): User = requireData().toDomain()

fun ApiResponseDto<List<UserDto>>.toUserListDomain(): List<User> =
    requireData().map { it.toDomain() }
