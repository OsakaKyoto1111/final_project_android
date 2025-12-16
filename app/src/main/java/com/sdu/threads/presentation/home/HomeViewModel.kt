package com.sdu.threads.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.model.UploadFile
import com.sdu.threads.domain.model.User
import com.sdu.threads.domain.usecase.AddCommentUseCase
import com.sdu.threads.domain.usecase.CreatePostUseCase
import com.sdu.threads.domain.usecase.DeletePostUseCase
import com.sdu.threads.domain.usecase.GetFeedUseCase
import com.sdu.threads.domain.usecase.GetFollowingUseCase
import com.sdu.threads.domain.usecase.GetMyProfileUseCase
import com.sdu.threads.domain.usecase.LikePostUseCase
import com.sdu.threads.domain.usecase.FollowUserUseCase
import com.sdu.threads.domain.usecase.UnlikePostUseCase
import com.sdu.threads.domain.usecase.UpdatePostUseCase
import com.sdu.threads.domain.usecase.UnfollowUserUseCase
import com.sdu.threads.util.ApiResult
import com.sdu.threads.util.getReadableMessage
import com.sdu.threads.util.readableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: User? = null,
    val posts: List<com.sdu.threads.domain.model.Post> = emptyList(),
    val newPostText: String = "",
    val newPostAttachments: List<UploadFile> = emptyList(),
    val commentDrafts: Map<Long, String> = emptyMap(),
    val isLoading: Boolean = false,
    val isPosting: Boolean = false,
    val followingIds: Set<Long> = emptySet(),
    val pendingFollowIds: Set<Long> = emptySet(),
    val processingPostIds: Set<Long> = emptySet(),
    val error: String? = null
)

sealed interface HomeEvent {
    data object PostCreated : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getFeedUseCase: GetFeedUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    private val getFollowingUseCase: GetFollowingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(isLoading = true))
    val state: StateFlow<HomeUiState> = _state
    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events

    init {
        loadProfile()
        loadFeed()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching {
                getMyProfileUseCase()
            }.onSuccess { user ->
                _state.update { it.copy(user = user, isLoading = false, error = null) }
                loadFollowing(user.id)
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }

    private fun loadFollowing(userId: Long) {
        viewModelScope.launch {
            runCatching { getFollowingUseCase(userId) }
                .onSuccess { users ->
                    _state.update { it.copy(followingIds = users.map { it.id }.toSet()) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = throwable.readableMessage()) }
                }
        }
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
                            error = result.message
                        )
                    }
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun refreshFeed() {
        loadFeed()
    }

    fun onNewPostChanged(value: String) {
        _state.update { it.copy(newPostText = value) }
    }

    fun createPost() {
        val text = _state.value.newPostText.takeIf { it.isNotBlank() }
        val attachments = _state.value.newPostAttachments
        if (text == null && attachments.isEmpty()) return
        
        viewModelScope.launch {
            _state.update { it.copy(isPosting = true, error = null) }
            when (val result = createPostUseCase(text, attachments)) {
                is ApiResult.Success -> {
                    _state.update {
                        it.copy(
                            isPosting = false,
                            newPostText = "",
                            newPostAttachments = emptyList()
                        )
                    }
                    _events.emit(HomeEvent.PostCreated)
                    loadFeed()
                }
                is ApiResult.Error -> {
                    _state.update {
                        it.copy(isPosting = false, error = result.getReadableMessage())
                    }
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun addNewPostAttachments(files: List<UploadFile>) {
        if (files.isEmpty()) return
        _state.update { state ->
            val merged = (state.newPostAttachments + files).distinctBy { it.name }
            state.copy(newPostAttachments = merged)
        }
    }

    fun removeNewPostAttachment(name: String) {
        _state.update { state ->
            state.copy(newPostAttachments = state.newPostAttachments.filterNot { it.name == name })
        }
    }

    fun clearNewPostAttachments() {
        _state.update { it.copy(newPostAttachments = emptyList()) }
    }

    fun updatePost(postId: Long, description: String) {
        viewModelScope.launch {
            _state.update { it.copy(processingPostIds = it.processingPostIds + postId, error = null) }
            when (val result = updatePostUseCase(postId, description.takeIf { it.isNotBlank() })) {
                is ApiResult.Success -> {
                    _state.update { it.copy(processingPostIds = it.processingPostIds - postId) }
                    loadFeed()
                }
                is ApiResult.Error -> {
                    _state.update {
                        it.copy(
                            error = result.message,
                            processingPostIds = it.processingPostIds - postId
                        )
                    }
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(processingPostIds = it.processingPostIds + postId, error = null) }
            when (val result = deletePostUseCase(postId)) {
                is ApiResult.Success -> {
                    _state.update { it.copy(processingPostIds = it.processingPostIds - postId) }
                    loadFeed()
                }
                is ApiResult.Error -> {
                    _state.update {
                        it.copy(
                            error = result.message,
                            processingPostIds = it.processingPostIds - postId
                        )
                    }
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun toggleLike(postId: Long, currentlyLiked: Boolean) {
        viewModelScope.launch {
            val result = if (currentlyLiked) {
                unlikePostUseCase(postId)
            } else {
                likePostUseCase(postId)
            }
            when (result) {
                is ApiResult.Error -> {
                    _state.update { it.copy(error = result.getReadableMessage()) }
                }
                is ApiResult.Success -> {
                    loadFeed()
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun onCommentDraftChange(postId: Long, text: String) {
        _state.update { state ->
            state.copy(commentDrafts = state.commentDrafts + (postId to text))
        }
    }

    fun addComment(postId: Long) {
        val draft = _state.value.commentDrafts[postId].orEmpty()
        if (draft.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(processingPostIds = it.processingPostIds + postId, error = null) }
            when (val result = addCommentUseCase(postId, draft)) {
                is ApiResult.Success -> {
                    _state.update {
                        it.copy(
                            processingPostIds = it.processingPostIds - postId,
                            commentDrafts = it.commentDrafts - postId
                        )
                    }
                    loadFeed()
                }
                is ApiResult.Error -> {
                    _state.update {
                        it.copy(
                            error = result.message,
                            processingPostIds = it.processingPostIds - postId
                        )
                    }
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun toggleFollow(userId: Long) {
        val currentUserId = _state.value.user?.id ?: return
        if (currentUserId == userId) return

        viewModelScope.launch {
            val isFollowing = _state.value.followingIds.contains(userId)
            _state.update {
                it.copy(
                    pendingFollowIds = it.pendingFollowIds + userId,
                    error = null
                )
            }
            runCatching {
                if (isFollowing) unfollowUserUseCase(userId) else followUserUseCase(userId)
            }.onSuccess {
                _state.update {
                    it.copy(
                        followingIds = if (isFollowing) {
                            it.followingIds - userId
                        } else {
                            it.followingIds + userId
                        }
                    )
                }
                loadFeed()
            }.onFailure { throwable ->
                _state.update { it.copy(error = throwable.readableMessage()) }
            }
            _state.update { it.copy(pendingFollowIds = it.pendingFollowIds - userId) }
        }
    }
}
