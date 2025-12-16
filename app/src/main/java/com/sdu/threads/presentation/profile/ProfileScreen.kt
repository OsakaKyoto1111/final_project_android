@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.threads.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sdu.threads.domain.model.User
import com.sdu.threads.presentation.components.Avatar
import com.sdu.threads.presentation.components.ErrorView
import com.sdu.threads.presentation.components.LoadingView
import com.sdu.threads.presentation.components.PostCard
import com.sdu.threads.presentation.components.PostSkeleton
import com.sdu.threads.presentation.components.PrimaryButton
import com.sdu.threads.presentation.components.EmptyState
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onEditClick: () -> Unit,
    onLogout: () -> Unit,
    onUploadAvatar: (ByteArray, String, String) -> Unit,
    onViewFollowers: (Long) -> Unit,
    onViewFollowing: (Long) -> Unit,
    onDeletePost: (Long) -> Unit,
    onToggleLike: (Long, Boolean) -> Unit,
    onAuthorClick: (Long) -> Unit = {},
    onCommentClick: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                scope.launch {
                    val bytes = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    } ?: return@launch
                    val name = uri.lastPathSegment ?: "avatar.jpg"
                    val mime = context.contentResolver.getType(uri) ?: "image/*"
                    onUploadAvatar(bytes, name, mime)
                }
            }
        }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.user?.nickname ?: "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Edit profile")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(imageVector = Icons.Rounded.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingView(modifier = Modifier.padding(padding))
            state.error != null -> ErrorView(
                modifier = Modifier.padding(padding),
                message = state.error
            )
            state.user != null -> ProfileContent(
                user = state.user,
                posts = state.posts,
                isLoadingPosts = state.isLoadingPosts,
                onEditClick = onEditClick,
                onChangeAvatar = { avatarPicker.launch("image/*") },
                isUploadingAvatar = state.isUploadingAvatar,
                info = state.info,
                onViewFollowers = onViewFollowers,
                onViewFollowing = onViewFollowing,
                onDeletePost = onDeletePost,
                onToggleLike = onToggleLike,
                onAuthorClick = onAuthorClick,
                onCommentClick = onCommentClick,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    posts: List<com.sdu.threads.domain.model.Post>,
    isLoadingPosts: Boolean,
    onEditClick: () -> Unit,
    onChangeAvatar: () -> Unit,
    isUploadingAvatar: Boolean,
    info: String?,
    onViewFollowers: (Long) -> Unit,
    onViewFollowing: (Long) -> Unit,
    onDeletePost: (Long) -> Unit,
    onToggleLike: (Long, Boolean) -> Unit,
    onAuthorClick: (Long) -> Unit,
    onCommentClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Avatar(
                    avatarUrl = user.avatarUrl,
                    initials = user.nickname,
                    modifier = Modifier.size(100.dp)
                )

                if (user.fullName.isNotBlank()) {
                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "@${user.nickname}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        value = user.postsCount,
                        label = "Posts",
                        onClick = null
                    )
                    StatItem(
                        value = user.followersCount,
                        label = "Followers",
                        onClick = { onViewFollowers(user.id) }
                    )
                    StatItem(
                        value = user.followingCount,
                        label = "Following",
                        onClick = { onViewFollowing(user.id) }
                    )
                }

                if (user.city.isNotBlank() || user.grade.isNotBlank() || user.major.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (user.city.isNotBlank()) {
                            BioInfoRow(
                                icon = Icons.Filled.LocationOn,
                                iconColor = Color(0xFFE53935),
                                text = "City: ${user.city}"
                            )
                        }
                        if (user.grade.isNotBlank()) {
                            BioInfoRow(
                                icon = Icons.Filled.School,
                                iconColor = MaterialTheme.colorScheme.primary,
                                text = "GPA: ${user.grade}"
                            )
                        }
                        if (user.major.isNotBlank()) {
                            BioInfoRow(
                                icon = Icons.Filled.Book,
                                iconColor = Color(0xFF1976D2),
                                text = "Major: ${user.major}"
                            )
                        }
                    }
                }

                if (user.description.isNotBlank()) {
                    Text(
                        text = user.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (!info.isNullOrBlank()) {
                    Text(
                        text = info,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        item {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (isLoadingPosts && posts.isEmpty()) {
            items(3) {
                PostSkeleton()
            }
        } else if (posts.isEmpty()) {
            item {
                EmptyState(
                    title = "No posts yet",
                    message = "Share your first post!",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            items(
                items = posts,
                key = { it.id }
            ) { post ->
                PostCard(
                    post = post,
                    onLikeClick = { onToggleLike(post.id, post.liked) },
                    onCommentClick = { onCommentClick(post.id) },
                    onAuthorClick = { onAuthorClick(post.author.id) },
                    onDeleteClick = { onDeletePost(post.id) },
                    showDeleteButton = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StatItem(value: Int, label: String, onClick: (() -> Unit)? = null) {
    Column(
        modifier = if (onClick != null) {
            Modifier.clickable(onClick = onClick)
        } else {
            Modifier
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BioInfoRow(
    icon: ImageVector,
    iconColor: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
