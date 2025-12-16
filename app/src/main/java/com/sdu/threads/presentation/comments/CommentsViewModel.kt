package com.sdu.threads.presentation.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.model.Comment
import com.sdu.threads.domain.usecase.AddCommentUseCase
import com.sdu.threads.domain.usecase.GetCommentsUseCase
import com.sdu.threads.domain.usecase.GetFollowingUseCase
import com.sdu.threads.domain.usecase.GetMyProfileUseCase
import com.sdu.threads.domain.usecase.LikeCommentUseCase
import com.sdu.threads.domain.usecase.UnlikeCommentUseCase
import com.sdu.threads.domain.usecase.FollowUserUseCase
import com.sdu.threads.domain.usecase.UnfollowUserUseCase
import com.sdu.threads.util.ApiResult
import com.sdu.threads.util.getReadableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CommentsUiState(
    val isLoading: Boolean = true,
    val comments: List<Comment> = emptyList(),
    val commentText: String = "",
    val replyToCommentId: Long? = null,
    val isPosting: Boolean = false,
    val followingIds: Set<Long> = emptySet(),
    val pendingFollowIds: Set<Long> = emptySet(),
    val currentUserId: Long? = null,
    val currentUser: com.sdu.threads.domain.model.User? = null,
    val error: String? = null
)

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val likeCommentUseCase: LikeCommentUseCase,
    private val unlikeCommentUseCase: UnlikeCommentUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getFollowingUseCase: GetFollowingUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Long = checkNotNull(savedStateHandle["postId"])

    private val _state = MutableStateFlow(CommentsUiState(isLoading = true))
    val state: StateFlow<CommentsUiState> = _state

    init {
        loadComments()
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            runCatching { getMyProfileUseCase() }
                .onSuccess { user ->
                    _state.value = _state.value.copy(
                        currentUserId = user.id,
                        currentUser = user
                    )
                    loadFollowing(user.id)
                }
        }
    }

    private fun loadFollowing(userId: Long) {
        viewModelScope.launch {
            runCatching { getFollowingUseCase(userId) }
                .onSuccess { users ->
                    _state.value = _state.value.copy(
                        followingIds = users.map { it.id }.toSet()
                    )
                }
        }
    }

    fun loadComments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = getCommentsUseCase(postId)) {
                is ApiResult.Success -> {
                    val flatComments = flattenComments(result.data.comments)
                    val treeComments = buildCommentTree(flatComments)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        comments = treeComments
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is ApiResult.Loading -> {}
            }
        }
    }


    private fun flattenComments(comments: List<Comment>): List<Comment> {
        val result = mutableListOf<Comment>()
        fun traverse(commentList: List<Comment>) {
            commentList.forEach { comment ->
                result.add(comment.copy(replies = emptyList()))
                if (comment.replies.isNotEmpty()) {
                    traverse(comment.replies)
                }
            }
        }
        traverse(comments)
        return result
    }


    private fun buildCommentTree(flatComments: List<Comment>): List<Comment> {
        if (flatComments.isEmpty()) return emptyList()

        val commentMap = flatComments.associateBy { it.id }
        
        val rootComments = mutableListOf<Comment>()
        val repliesMap = mutableMapOf<Long, MutableList<Comment>>()

        flatComments.forEach { comment ->
            if (comment.parentId == null) {
                rootComments.add(comment)
            } else {
                val parentId = comment.parentId
                repliesMap.getOrPut(parentId) { mutableListOf() }.add(comment)
            }
        }

        fun buildReplies(comment: Comment, depth: Int = 0): Comment {
            if (depth >= 3) {
                return comment.copy(replies = emptyList())
            }

            val replies = repliesMap[comment.id]?.map { reply ->
                buildReplies(reply, depth + 1)
            } ?: emptyList()

            return comment.copy(replies = replies)
        }

        return rootComments.map { buildReplies(it) }
    }

    fun onCommentTextChange(text: String) {
        _state.value = _state.value.copy(commentText = text)
    }

    fun setReplyToComment(commentId: Long) {
        if (commentId == 0L) {
            _state.value = _state.value.copy(replyToCommentId = null, commentText = "")
        } else {
            _state.value = _state.value.copy(replyToCommentId = commentId)
        }
    }

    fun addComment() {
        val text = _state.value.commentText.trim()
        if (text.isBlank()) return

        val parentId = _state.value.replyToCommentId
        val currentUser = _state.value.currentUser
        val currentUserId = _state.value.currentUserId

        val optimisticComment = if (currentUser != null && currentUserId != null) {
            com.sdu.threads.domain.model.Comment(
                id = -System.currentTimeMillis(),
                postId = postId,
                author = currentUser,
                text = text,
                parentId = parentId,
                likes = 0,
                isLiked = false,
                createdAt = System.currentTimeMillis(),
                replies = emptyList()
            )
        } else null

        val updatedComments = if (optimisticComment != null) {
            if (parentId == null) {
                listOf(optimisticComment) + _state.value.comments
            } else {
                addReplyToCommentTree(_state.value.comments, parentId, optimisticComment)
            }
        } else {
            _state.value.comments
        }

        _state.value = _state.value.copy(
            isPosting = true,
            error = null,
            commentText = "",
            replyToCommentId = null,
            comments = updatedComments
        )

        viewModelScope.launch {
            when (val result = addCommentUseCase(postId, text, parentId)) {
                is ApiResult.Success -> {
                    val realComment = result.data
                    val updatedComments = if (optimisticComment != null) {
                        replaceCommentById(_state.value.comments, optimisticComment.id, realComment)
                    } else {
                        _state.value.comments
                    }
                    _state.value = _state.value.copy(
                        isPosting = false,
                        comments = updatedComments
                    )
                }
                is ApiResult.Error -> {
                    if (optimisticComment != null) {
                        val commentsAfterError = removeCommentById(_state.value.comments, optimisticComment.id)
                        _state.value = _state.value.copy(
                            isPosting = false,
                            comments = commentsAfterError,
                            error = result.getReadableMessage()
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isPosting = false,
                            error = result.getReadableMessage()
                        )
                    }
                }
                is ApiResult.Loading -> {}
            }
        }
    }
    
    private fun addReplyToCommentTree(comments: List<Comment>, parentId: Long, reply: Comment): List<Comment> {
        return comments.map { comment ->
            if (comment.id == parentId) {
                comment.copy(replies = comment.replies + reply)
            } else {
                comment.copy(replies = addReplyToCommentTree(comment.replies, parentId, reply))
            }
        }
    }
    
    private fun removeCommentById(comments: List<Comment>, commentId: Long): List<Comment> {
        return comments.mapNotNull { comment ->
            if (comment.id == commentId) {
                null
            } else {
                comment.copy(replies = removeCommentById(comment.replies, commentId))
            }
        }
    }
    
    private fun replaceCommentById(comments: List<Comment>, oldId: Long, newComment: Comment): List<Comment> {
        return comments.map { comment ->
            if (comment.id == oldId) {
                newComment.copy(replies = comment.replies)
            } else {
                comment.copy(replies = replaceCommentById(comment.replies, oldId, newComment))
            }
        }
    }

    fun toggleLikeComment(commentId: Long, currentIsLiked: Boolean) {
        viewModelScope.launch {
            fun findComment(comments: List<Comment>): Comment? {
                for (comment in comments) {
                    if (comment.id == commentId) return comment
                    val found = findComment(comment.replies)
                    if (found != null) return found
                }
                return null
            }
            
            val comment = findComment(_state.value.comments)
            val isLiked = comment?.isLiked ?: currentIsLiked
            
            val result = if (isLiked) {
                unlikeCommentUseCase(commentId)
            } else {
                likeCommentUseCase(commentId)
            }
            when (result) {
                is ApiResult.Success -> {
                    loadComments()
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(error = result.getReadableMessage())
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun toggleFollow(userId: Long) {
        val currentUserId = _state.value.currentUserId
        if (currentUserId == null || currentUserId == userId) return

        viewModelScope.launch {
            val isFollowing = _state.value.followingIds.contains(userId)

            _state.value = _state.value.copy(
                pendingFollowIds = _state.value.pendingFollowIds + userId,
                error = null
            )

            runCatching {
                if (isFollowing) unfollowUserUseCase(userId)
                else followUserUseCase(userId)
            }.onSuccess {
                _state.value = _state.value.copy(
                    followingIds = if (isFollowing) {
                        _state.value.followingIds - userId
                    } else {
                        _state.value.followingIds + userId
                    }
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(error = e.message ?: "Failed to follow/unfollow")
            }

            _state.value = _state.value.copy(
                pendingFollowIds = _state.value.pendingFollowIds - userId
            )
        }
    }
}






