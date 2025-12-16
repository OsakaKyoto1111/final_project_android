package com.sdu.threads.di

import com.sdu.threads.data.repository.AuthRepositoryImpl
import com.sdu.threads.data.repository.PostRepositoryImpl
import com.sdu.threads.data.repository.UserRepositoryImpl
import com.sdu.threads.domain.repository.AuthRepository
import com.sdu.threads.domain.repository.PostRepository
import com.sdu.threads.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository
}
