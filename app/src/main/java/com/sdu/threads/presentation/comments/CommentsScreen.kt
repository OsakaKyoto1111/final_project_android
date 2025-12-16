@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.threads.presentation.comments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdu.threads.domain.model.Comment
import com.sdu.threads.presentation.components.CommentTree
import com.sdu.threads.presentation.components.EmptyState
import com.sdu.threads.presentation.components.ErrorState
import com.sdu.threads.presentation.components.LoadingView
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha

@Composable
fun CommentsScreen(
    state: CommentsUiState,
    onBack: () -> Unit,
    onCommentTextChange: (String) -> Unit,
    onAddComment: () -> Unit,
    onReplyToComment: (Long) -> Unit,
    onLikeComment: (Long, Boolean) -> Unit,
    onAuthorClick: (Long) -> Unit,
    onToggleFollow: (Long) -> Unit,
    onRefresh: () -> Unit = {}
) {
    val pullToRefreshState = rememberPullToRefreshState()
    
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            onRefresh()
            pullToRefreshState.endRefresh()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Comments",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
            ) {
                when {
                    state.isLoading && state.comments.isEmpty() -> {
                        LoadingView(modifier = Modifier.fillMaxSize())
                    }
                    state.error != null && state.comments.isEmpty() -> {
                        ErrorState(
                            message = state.error,
                            onRetry = {  },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    state.comments.isEmpty() -> {
                        EmptyState(
                            title = "No comments yet",
                            message = "Be the first to comment!",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.comments,
                                key = { it.id }
                            ) { comment ->
                                val alpha by animateFloatAsState(
                                    targetValue = 1f,
                                    animationSpec = tween(durationMillis = 300),
                                    label = "commentAlpha"
                                )
                                CommentTree(
                                    comment = comment,
                                    onLikeClick = { commentId ->
                                        fun findComment(c: Comment): Comment? {
                                            if (c.id == commentId) return c
                                            return c.replies.firstOrNull { findComment(it) != null }
                                        }
                                        val targetComment = findComment(comment) ?: comment
                                        onLikeComment(targetComment.id, targetComment.isLiked)
                                    },
                                    onReplyClick = { commentId -> onReplyToComment(commentId) },
                                    onAuthorClick = { onAuthorClick(comment.author.id) },
                                    isFollowing = state.followingIds.contains(comment.author.id),
                                    isPendingFollow = state.pendingFollowIds.contains(comment.author.id),
                                    onToggleFollow = { onToggleFollow(comment.author.id) },
                                    isOwnComment = state.currentUserId == comment.author.id,
                                    getIsFollowingForUser = { userId -> state.followingIds.contains(userId) },
                                    getIsPendingForUser = { userId -> state.pendingFollowIds.contains(userId) },
                                    onToggleFollowForUser = { userId -> onToggleFollow(userId) },
                                    onAuthorClickForUser = { userId -> onAuthorClick(userId) },
                                    onLikeClickForComment = { commentId, isLiked -> 
                                        onLikeComment(commentId, isLiked)
                                    },
                                    getIsOwnComment = { commentId ->
                                        fun findComment(c: Comment): Comment? {
                                            if (c.id == commentId) return c
                                            return c.replies.firstOrNull { findComment(it) != null }
                                        }
                                        val found = findComment(comment)
                                        found?.author?.id == state.currentUserId
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(alpha)
                                )
                            }
                        }
                    }
                }
                PullToRefreshContainer(
                    state = pullToRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.replyToCommentId != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Replying to comment",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextButton(onClick = { 
                            onReplyToComment(0)
                            onCommentTextChange("")
                        }) {
                            Text("Cancel")
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = state.commentText,
                        onValueChange = onCommentTextChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { 
                            Text(
                                if (state.replyToCommentId != null) "Reply..." else "Add a comment..."
                            ) 
                        },
                        singleLine = false,
                        maxLines = 4
                    )
                    IconButton(
                        onClick = onAddComment,
                        enabled = state.commentText.isNotBlank() && !state.isPosting
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Send comment"
                        )
                    }
                }
            }
        }
    }
}






