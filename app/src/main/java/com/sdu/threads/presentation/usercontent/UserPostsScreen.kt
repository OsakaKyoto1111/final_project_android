@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.threads.presentation.usercontent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdu.threads.presentation.components.EmptyState
import com.sdu.threads.presentation.components.ErrorState
import com.sdu.threads.presentation.components.PostCard
import com.sdu.threads.presentation.components.PostSkeleton
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.input.nestedscroll.nestedScroll

@Composable
fun UserPostsScreen(
    state: UserPostsUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onToggleLike: (Long, Boolean) -> Unit,
    onAuthorClick: (Long) -> Unit = {}
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
                        text = state.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
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
                        repeat(3) { PostSkeleton() }
                    }
                }

                state.error != null && state.posts.isEmpty() -> {
                    ErrorState(
                        message = state.error,
                        onRetry = onRefresh,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                state.posts.isEmpty() -> {
                    EmptyState(
                        title = "No posts yet",
                        message = "This user has not posted anything.",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                    ) {
                        items(
                            items = state.posts,
                            key = { it.id }
                        ) { post ->
                            PostCard(
                                post = post,
                                onLikeClick = { onToggleLike(post.id, post.liked) },
                                onCommentClick = {},
                                onAuthorClick = { onAuthorClick(post.author.id) },
                                modifier = Modifier.fillMaxWidth()
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
