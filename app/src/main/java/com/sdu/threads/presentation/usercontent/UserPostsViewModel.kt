package com.sdu.threads.presentation.usercontent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.model.Post
import com.sdu.threads.domain.model.User
import com.sdu.threads.domain.usecase.GetMyProfileUseCase
import com.sdu.threads.domain.usecase.GetUserByIdUseCase
import com.sdu.threads.domain.usecase.GetUserPostsUseCase
import com.sdu.threads.domain.usecase.LikePostUseCase
import com.sdu.threads.domain.usecase.UnlikePostUseCase
import com.sdu.threads.util.ApiResult
import com.sdu.threads.util.getReadableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserPostsUiState(
    val title: String = "Posts",
    val isLoading: Boolean = true,
    val posts: List<Post> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class UserPostsViewModel @Inject constructor(
    private val getUserPostsUseCase: GetUserPostsUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: Long = checkNotNull(savedStateHandle["userId"])
    private val isSelf: Boolean = savedStateHandle["isSelf"] ?: false

    private val _state = MutableStateFlow(
        UserPostsUiState(
            title = if (isSelf) "My posts" else "Posts"
        )
    )
    val state: StateFlow<UserPostsUiState> = _state

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val currentUser = if (isSelf) {
                runCatching { getMyProfileUseCase() }.getOrNull()
            } else {
                null
            }

            val targetUserId = currentUser?.id ?: userId

            when (val result = getUserPostsUseCase(targetUserId)) {
                is ApiResult.Success -> {
                    var posts = result.data.distinctBy { it.id }

                    val userData = if (isSelf && currentUser != null) {
                        currentUser
                    } else if (!isSelf) {
                        runCatching { getUserByIdUseCase(userId) }.getOrNull()
                    } else {
                        null
                    }

                    if (userData != null) {
                        posts = posts.map { post ->
                            post.copy(
                                author = post.author.copy(
                                    id = userData.id,
                                    nickname = userData.nickname,
                                    avatarUrl = userData.avatarUrl,
                                    firstName = userData.firstName,
                                    lastName = userData.lastName
                                )
                            )
                        }
                    }

                    _state.value = _state.value.copy(
                        isLoading = false,
                        posts = posts
                    )
                }

                is ApiResult.Error -> _state.value = _state.value.copy(
                    isLoading = false,
                    error = result.message
                )

                is ApiResult.Loading -> {}
            }
        }
    }

    fun toggleLike(postId: Long, liked: Boolean) {
        viewModelScope.launch {
            val result = if (liked) unlikePostUseCase(postId) else likePostUseCase(postId)
            when (result) {
                is ApiResult.Error -> _state.value = _state.value.copy(
                    error = result.getReadableMessage()
                )

                is ApiResult.Success -> loadPosts()
                is ApiResult.Loading -> {}
            }
        }
    }
}
