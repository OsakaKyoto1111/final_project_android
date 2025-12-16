package com.sdu.threads.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.model.Post
import com.sdu.threads.domain.model.User
import com.sdu.threads.domain.usecase.ClearTokenUseCase
import com.sdu.threads.domain.usecase.DeletePostUseCase
import com.sdu.threads.domain.usecase.GetMyProfileUseCase
import com.sdu.threads.domain.usecase.GetUserPostsUseCase
import com.sdu.threads.domain.usecase.LikePostUseCase
import com.sdu.threads.domain.usecase.UploadAvatarUseCase
import com.sdu.threads.domain.usecase.UnlikePostUseCase
import com.sdu.threads.util.ApiResult
import com.sdu.threads.util.readableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val isLoadingPosts: Boolean = false,
    val error: String? = null,
    val isUploadingAvatar: Boolean = false,
    val info: String? = null
)

sealed class ProfileEvent {
    data object LoggedOut : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val clearTokenUseCase: ClearTokenUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getUserPostsUseCase: GetUserPostsUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState(isLoading = true))
    val state: StateFlow<ProfileUiState> = _state

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events = _events.asSharedFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, info = null)
            runCatching { getMyProfileUseCase() }
                .onSuccess { user -> 
                    _state.value = ProfileUiState(user = user)
                    loadPosts(user.id)
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = throwable.readableMessage()
                    )
                }
        }
    }

    fun loadPosts(userId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingPosts = true, error = null)
            when (val result = getUserPostsUseCase(userId)) {
                is ApiResult.Success -> {
                    val currentUser = _state.value.user
                    val posts = if (currentUser != null && currentUser.id == userId) {
                        result.data.map { post ->
                            post.copy(
                                author = post.author.copy(
                                    id = currentUser.id,
                                    nickname = currentUser.nickname,
                                    avatarUrl = currentUser.avatarUrl,
                                    firstName = currentUser.firstName,
                                    lastName = currentUser.lastName
                                )
                            )
                        }
                    } else {
                        result.data
                    }
                    _state.value = _state.value.copy(
                        posts = posts.distinctBy { it.id },
                        isLoadingPosts = false
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoadingPosts = false,
                        error = result.message
                    )
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            when (val result = deletePostUseCase(postId)) {
                is ApiResult.Success -> {
                    val user = _state.value.user
                    if (user != null) {
                        loadPosts(user.id)
                        loadProfile()
                    }
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(error = result.message)
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun toggleLike(postId: Long, liked: Boolean) {
        viewModelScope.launch {
            val result = if (liked) unlikePostUseCase(postId) else likePostUseCase(postId)
            when (result) {
                is ApiResult.Success -> {
                    val user = _state.value.user
                    if (user != null) {
                        loadPosts(user.id)
                    }
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(error = result.message)
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun uploadAvatar(bytes: ByteArray, fileName: String, mimeType: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUploadingAvatar = true, error = null, info = null)
            runCatching { uploadAvatarUseCase(bytes, fileName, mimeType) }
                .onSuccess { user ->
                    _state.value = _state.value.copy(
                        user = user,
                        isUploadingAvatar = false,
                        info = "Avatar updated"
                    )
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isUploadingAvatar = false,
                        error = throwable.readableMessage()
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching { clearTokenUseCase() }
                .onSuccess { _events.emit(ProfileEvent.LoggedOut) }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        error = throwable.readableMessage()
                    )
                }
        }
    }
}
