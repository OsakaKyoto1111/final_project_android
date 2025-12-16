package com.sdu.threads.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.model.Post
import com.sdu.threads.domain.usecase.GetFeedUseCase
import com.sdu.threads.domain.usecase.LikePostUseCase
import com.sdu.threads.domain.usecase.UnlikePostUseCase
import com.sdu.threads.util.ApiResult
import com.sdu.threads.util.getReadableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FeedUiState(isLoading = true))
    val state: StateFlow<FeedUiState> = _state.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getFeedUseCase()) {
                is ApiResult.Success -> {
                    _state.update {
                        it.copy(
                            posts = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.getReadableMessage()
                        )
                    }
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun toggleLike(postId: Long, currentlyLiked: Boolean) {
        viewModelScope.launch {
            _state.update { state ->
                state.copy(
                    posts = state.posts.map { post ->
                        if (post.id == postId) {
                            post.copy(
                                liked = !currentlyLiked,
                                likesCount = if (currentlyLiked) {
                                    (post.likesCount - 1).coerceAtLeast(0)
                                } else {
                                    post.likesCount + 1
                                }
                            )
                        } else {
                            post
                        }
                    }
                )
            }

            val result = if (currentlyLiked) {
                unlikePostUseCase(postId)
            } else {
                likePostUseCase(postId)
            }

            if (result is ApiResult.Error) {
                _state.update { state ->
                    state.copy(
                        posts = state.posts.map { post ->
                            if (post.id == postId) {
                                post.copy(
                                    liked = currentlyLiked,
                                    likesCount = if (currentlyLiked) {
                                        post.likesCount
                                    } else {
                                        (post.likesCount - 1).coerceAtLeast(0)
                                    }
                                )
                            } else {
                                post
                            }
                        },
                        error = result.getReadableMessage()
                    )
                }
            }
        }
    }

    fun retry() {
        loadFeed()
    }
}
