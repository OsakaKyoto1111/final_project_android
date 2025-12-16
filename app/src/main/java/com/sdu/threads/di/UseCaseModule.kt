package com.sdu.threads.di

import com.sdu.threads.domain.repository.AuthRepository
import com.sdu.threads.domain.repository.PostRepository
import com.sdu.threads.domain.repository.UserRepository
import com.sdu.threads.domain.usecase.AddRecentSearchUseCase
import com.sdu.threads.domain.usecase.AddCommentUseCase
import com.sdu.threads.domain.usecase.ClearTokenUseCase
import com.sdu.threads.domain.usecase.CreatePostUseCase
import com.sdu.threads.domain.usecase.DeletePostUseCase
import com.sdu.threads.domain.usecase.GetCachedUserUseCase
import com.sdu.threads.domain.usecase.GetFeedUseCase
import com.sdu.threads.domain.usecase.GetMyProfileUseCase
import com.sdu.threads.domain.usecase.GetUserByIdUseCase
import com.sdu.threads.domain.usecase.LikePostUseCase
import com.sdu.threads.domain.usecase.LoginUseCase
import com.sdu.threads.domain.usecase.ObserveTokenUseCase
import com.sdu.threads.domain.usecase.ObserveRecentSearchUseCase
import com.sdu.threads.domain.usecase.RegisterUseCase
import com.sdu.threads.domain.usecase.SaveTokenUseCase
import com.sdu.threads.domain.usecase.SearchUsersUseCase
import com.sdu.threads.domain.usecase.UpdateProfileUseCase
import com.sdu.threads.domain.usecase.UploadAvatarUseCase
import com.sdu.threads.domain.usecase.GetFollowersUseCase
import com.sdu.threads.domain.usecase.GetFollowingUseCase
import com.sdu.threads.domain.usecase.UnlikePostUseCase
import com.sdu.threads.domain.usecase.UpdatePostUseCase
import com.sdu.threads.domain.usecase.FollowUserUseCase
import com.sdu.threads.domain.usecase.UnfollowUserUseCase
import com.sdu.threads.domain.usecase.GetCommentsUseCase
import com.sdu.threads.domain.usecase.LikeCommentUseCase
import com.sdu.threads.domain.usecase.UnlikeCommentUseCase
import com.sdu.threads.domain.usecase.GetPostUseCase
import com.sdu.threads.domain.usecase.GetMyPostsUseCase
import com.sdu.threads.domain.usecase.GetUserPostsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideLoginUseCase(authRepository: AuthRepository) = LoginUseCase(authRepository)

    @Provides
    fun provideRegisterUseCase(authRepository: AuthRepository) = RegisterUseCase(authRepository)

    @Provides
    fun provideObserveTokenUseCase(authRepository: AuthRepository) = ObserveTokenUseCase(authRepository)

    @Provides
    fun provideSaveTokenUseCase(authRepository: AuthRepository) = SaveTokenUseCase(authRepository)

    @Provides
    fun provideClearTokenUseCase(authRepository: AuthRepository) = ClearTokenUseCase(authRepository)

    @Provides
    fun provideMyProfileUseCase(userRepository: UserRepository) = GetMyProfileUseCase(userRepository)

    @Provides
    fun provideUpdateProfileUseCase(userRepository: UserRepository) = UpdateProfileUseCase(userRepository)

    @Provides
    fun provideSearchUsersUseCase(userRepository: UserRepository) = SearchUsersUseCase(userRepository)

    @Provides
    fun provideGetUserByIdUseCase(userRepository: UserRepository) = GetUserByIdUseCase(userRepository)

    @Provides
    fun provideGetCachedUserUseCase(userRepository: UserRepository) = GetCachedUserUseCase(userRepository)

    @Provides
    fun provideAddRecentSearchUseCase(userRepository: UserRepository) = AddRecentSearchUseCase(userRepository)

    @Provides
    fun provideObserveRecentSearchUseCase(userRepository: UserRepository) = ObserveRecentSearchUseCase(userRepository)

    @Provides
    fun provideUploadAvatarUseCase(userRepository: UserRepository) = UploadAvatarUseCase(userRepository)

    @Provides
    fun provideFollowUserUseCase(userRepository: UserRepository) = FollowUserUseCase(userRepository)

    @Provides
    fun provideUnfollowUserUseCase(userRepository: UserRepository) = UnfollowUserUseCase(userRepository)

    @Provides
    fun provideGetFollowersUseCase(userRepository: UserRepository) = GetFollowersUseCase(userRepository)

    @Provides
    fun provideGetFollowingUseCase(userRepository: UserRepository) = GetFollowingUseCase(userRepository)

    @Provides
    fun provideGetFeedUseCase(postRepository: PostRepository) = GetFeedUseCase(postRepository)

    @Provides
    fun provideGetPostUseCase(postRepository: PostRepository) = GetPostUseCase(postRepository)

    @Provides
    fun provideGetMyPostsUseCase(postRepository: PostRepository) = GetMyPostsUseCase(postRepository)

    @Provides
    fun provideGetUserPostsUseCase(postRepository: PostRepository) = GetUserPostsUseCase(postRepository)

    @Provides
    fun provideCreatePostUseCase(postRepository: PostRepository) = CreatePostUseCase(postRepository)

    @Provides
    fun provideUpdatePostUseCase(postRepository: PostRepository) = UpdatePostUseCase(postRepository)

    @Provides
    fun provideDeletePostUseCase(postRepository: PostRepository) = DeletePostUseCase(postRepository)

    @Provides
    fun provideLikePostUseCase(postRepository: PostRepository) = LikePostUseCase(postRepository)

    @Provides
    fun provideUnlikePostUseCase(postRepository: PostRepository) = UnlikePostUseCase(postRepository)

    @Provides
    fun provideGetCommentsUseCase(postRepository: PostRepository) = GetCommentsUseCase(postRepository)

    @Provides
    fun provideAddCommentUseCase(postRepository: PostRepository) = AddCommentUseCase(postRepository)

    @Provides
    fun provideLikeCommentUseCase(postRepository: PostRepository) = LikeCommentUseCase(postRepository)

    @Provides
    fun provideUnlikeCommentUseCase(postRepository: PostRepository) = UnlikeCommentUseCase(postRepository)
}
