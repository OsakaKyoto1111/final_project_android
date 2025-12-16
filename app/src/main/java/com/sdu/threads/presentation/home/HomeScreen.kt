@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.threads.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdu.threads.presentation.components.EmptyState
import com.sdu.threads.presentation.components.ErrorState
import com.sdu.threads.presentation.components.PostCard
import com.sdu.threads.presentation.components.PostSkeleton
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.width
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha

@Composable
fun HomeScreen(
    state: HomeUiState,
    onOpenComposer: () -> Unit,
    onRefresh: () -> Unit,
    onToggleLike: (Long, Boolean) -> Unit,
    onPostClick: (Long) -> Unit = {},
    onAuthorClick: (Long) -> Unit = {},
    onToggleFollowUser: (Long) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            onRefresh()
            pullToRefreshState.endRefresh()
        }
    }

    LaunchedEffect(listState) {
        val layoutInfo = listState.layoutInfo
        val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
        if (lastVisibleItem != null && lastVisibleItem.index >= layoutInfo.totalItemsCount - 3) {
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SduThreads",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            when {
                state.isLoading && state.posts.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(3) {
                            PostSkeleton()
                        }
                    }
                }
                state.error != null && state.posts.isEmpty() -> {
                    val errorMessage = state.error ?: "Something went wrong"
                    ErrorState(
                        message = errorMessage,
                        onRetry = onRefresh,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                state.posts.isEmpty() -> {
                    EmptyState(
                        title = "No posts yet",
                        message = "Be the first to share something!",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 0.dp)
                    ) {
                        items(
                            items = state.posts,
                            key = { it.id }
                        ) { post ->
                            val isOwnPost = state.user?.id == post.author.id
                            val isFollowing = state.followingIds.contains(post.author.id)
                            val isPending = state.pendingFollowIds.contains(post.author.id)
                            val alpha by animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = 300),
                                label = "postAlpha"
                            )
                            PostCard(
                                post = post,
                                onLikeClick = { onToggleLike(post.id, post.liked) },
                                onCommentClick = { onPostClick(post.id) },
                                onAuthorClick = { onAuthorClick(post.author.id) },
                                onDeleteClick = null,
                                showDeleteButton = false,
                                authorActionContent = {
                                    if (!isOwnPost) {
                                        TextButton(
                                            onClick = { onToggleFollowUser(post.author.id) },
                                            enabled = !isPending,
                                            contentPadding = PaddingValues(
                                                horizontal = 12.dp,
                                                vertical = 6.dp
                                            ),
                                            modifier = Modifier.width(90.dp)
                                        ) {
                                            Text(text = if (isFollowing) "Unfollow" else "Follow")
                                        }
                                    }
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
    }
}
