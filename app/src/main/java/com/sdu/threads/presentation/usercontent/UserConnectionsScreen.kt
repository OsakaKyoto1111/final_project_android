@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.threads.presentation.usercontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import com.sdu.threads.presentation.components.EmptyState
import com.sdu.threads.presentation.components.ErrorView
import com.sdu.threads.presentation.components.LoadingView
import com.sdu.threads.presentation.components.UserCard
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.input.nestedscroll.nestedScroll

@Composable
fun UserConnectionsScreen(
    state: UserConnectionsUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onUserClick: (Long) -> Unit
) {
    val emptyMessage = if (state.title.contains("Follower", ignoreCase = true)) {
        "No followers yet"
    } else {
        "No subscriptions yet"
    }
    
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
                        style = MaterialTheme.typography.titleLarge
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
                state.isLoading && state.users.isEmpty() -> LoadingView()
                state.error != null && state.users.isEmpty() -> ErrorView(message = state.error)
                state.users.isEmpty() -> EmptyState(
                    title = state.title,
                    message = emptyMessage,
                    modifier = Modifier.fillMaxSize()
                )

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.users) { user ->
                            UserCard(
                                user = user,
                                onClick = { onUserClick(user.id) }
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
