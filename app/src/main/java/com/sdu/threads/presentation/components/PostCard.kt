package com.sdu.threads.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sdu.threads.domain.model.Post
import com.sdu.threads.presentation.components.Avatar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.scale

@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onAuthorClick: () -> Unit = {},
    authorActionContent: @Composable (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    showDeleteButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onAuthorClick),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(
                        avatarUrl = post.author.avatarUrl,
                        initials = post.author.nickname,
                        modifier = Modifier.size(36.dp)
                    )
                    Column {
                        Text(
                            text = post.author.nickname,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                    }
                }
                authorActionContent?.invoke()
                if (showDeleteButton && onDeleteClick != null) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete post",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = onDeleteClick),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (post.description.isNotBlank()) {
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 4.dp),
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (post.attachments.isNotEmpty()) {
                PostAttachments(attachments = post.attachments)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onLikeClick)
                ) {
                    val scale by animateFloatAsState(
                        targetValue = if (post.liked) 1.1f else 1f,
                        animationSpec = tween(durationMillis = 200),
                        label = "likeScale"
                    )
                    Icon(
                        imageVector = if (post.liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        modifier = Modifier
                            .size(20.dp)
                            .scale(scale),
                        tint = if (post.liked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (post.likesCount > 0) {
                        Text(
                            text = "${post.likesCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onCommentClick)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${post.commentsCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun PostAttachments(
    attachments: List<com.sdu.threads.domain.model.PostAttachment>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        when {
            attachments.size == 1 -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = attachments.first().url,
                        contentDescription = "Post attachment",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            attachments.size == 2 -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    attachments.take(2).forEach { attachment ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = attachment.url,
                                contentDescription = "Post attachment",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            else -> {
                attachments.take(4).chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        row.forEach { attachment ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = attachment.url,
                                    contentDescription = "Post attachment",
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
                if (attachments.size > 4) {
                    Text(
                        text = "+${attachments.size - 4} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}












