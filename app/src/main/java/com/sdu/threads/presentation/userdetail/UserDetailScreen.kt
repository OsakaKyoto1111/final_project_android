@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.threads.presentation.userdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sdu.threads.domain.model.User
import com.sdu.threads.presentation.components.Avatar
import com.sdu.threads.presentation.components.ErrorView
import com.sdu.threads.presentation.components.LoadingView
import com.sdu.threads.presentation.components.PrimaryButton

@Composable
fun UserDetailScreen(
    state: UserDetailUiState,
    onToggleFollow: () -> Unit,
    onBack: () -> Unit,
    onViewFollowers: (Long) -> Unit,
    onViewFollowing: (Long) -> Unit,
    onViewPosts: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User profile") },
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
        when {
            state.isLoading -> LoadingView(modifier = Modifier.padding(padding))
            state.error != null -> ErrorView(modifier = Modifier.padding(padding), message = state.error)
            state.user != null -> UserDetailContent(
                user = state.user,
                isFollowing = state.isFollowing,
                isProcessing = state.isProcessing,
                onToggleFollow = onToggleFollow,
                onViewFollowers = onViewFollowers,
                onViewFollowing = onViewFollowing,
                onViewPosts = onViewPosts,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun UserDetailContent(
    user: User,
    isFollowing: Boolean,
    isProcessing: Boolean,
    onToggleFollow: () -> Unit,
    onViewFollowers: (Long) -> Unit,
    onViewFollowing: (Long) -> Unit,
    onViewPosts: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Avatar(avatarUrl = user.avatarUrl, initials = user.nickname)
        Text(text = user.nickname, style = MaterialTheme.typography.titleLarge)
        if (user.fullName.isNotBlank()) {
            Text(text = user.fullName, style = MaterialTheme.typography.bodyLarge)
        }
        if (user.city.isNotBlank()) Text(text = user.city)
        if (user.major.isNotBlank()) Text(text = "Major: ${user.major}")
        if (user.grade.isNotBlank()) Text(text = "Grade: ${user.grade}")
        if (user.description.isNotBlank()) Text(text = user.description)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatItem(label = "posts", value = user.postsCount) { onViewPosts(user.id) }
            StatItem(label = "followers", value = user.followersCount) { onViewFollowers(user.id) }
            StatItem(label = "following", value = user.followingCount) { onViewFollowing(user.id) }
        }
        PrimaryButton(
            text = if (isFollowing) "Unfollow" else "Follow",
            enabled = !isProcessing,
            onClick = onToggleFollow
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = "$value", style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}
