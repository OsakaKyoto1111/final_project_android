package com.sdu.threads.presentation.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.sdu.threads.presentation.components.EmptyState
import com.sdu.threads.presentation.components.ErrorState
import com.sdu.threads.presentation.components.PostCard
import com.sdu.threads.presentation.components.PostSkeleton

@Composable
fun FeedScreen(
    onPostClick: (Long) -> Unit = {},
    onAuthorClick: (Long) -> Unit = {},
    viewModel: FeedViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                        onRetry = viewModel::retry,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                state.posts.isEmpty() -> {
                    EmptyState(
                        title = "No posts yet",
                        message = "Be the first to share something with the community!",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyColumn(
                        state = rememberLazyListState(),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 0.dp)
                    ) {
                        items(
                            items = state.posts,
                            key = { it.id }
                        ) { post ->
                            PostCard(
                                post = post,
                                onLikeClick = { viewModel.toggleLike(post.id, post.liked) },
                                onCommentClick = { onPostClick(post.id) },
                                onAuthorClick = { onAuthorClick(post.author.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                    }
                }
            }
        }
    }
}
