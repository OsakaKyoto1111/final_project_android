package com.sdu.threads.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.sdu.threads.presentation.components.Avatar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sdu.threads.domain.model.Comment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommentTree(
    comment: Comment,
    onLikeClick: (Long) -> Unit,
    onReplyClick: (Long) -> Unit = {},
    onAuthorClick: (() -> Unit)? = null,
    isFollowing: Boolean = false,
    isPendingFollow: Boolean = false,
    onToggleFollow: (() -> Unit)? = null,
    isOwnComment: Boolean = false,
    getIsFollowingForUser: ((Long) -> Boolean)? = null,
    getIsPendingForUser: ((Long) -> Boolean)? = null,
    onToggleFollowForUser: ((Long) -> Unit)? = null,
    onAuthorClickForUser: ((Long) -> Unit)? = null,
    onLikeClickForComment: ((Long, Boolean) -> Unit)? = null,
    getIsOwnComment: ((Long) -> Boolean)? = null,
    modifier: Modifier = Modifier,
    level: Int = 0
) {
    var isExpanded by remember { mutableStateOf(true) }
    val maxDepth = 3
    val canReply = level < maxDepth

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (level > 0) {
                Spacer(modifier = Modifier.width(16.dp))
            }

            Avatar(
                avatarUrl = comment.author.avatarUrl,
                initials = comment.author.nickname,
                modifier = Modifier
                    .size(32.dp)
                    .clickable(enabled = onAuthorClick != null) { onAuthorClick?.invoke() }
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comment.author.nickname,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.clickable(enabled = onAuthorClick != null) { onAuthorClick?.invoke() }
                        )
                        Text(
                            text = formatTimestamp(comment.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (!isOwnComment && onToggleFollow != null) {
                        IconButton(
                            onClick = onToggleFollow,
                            enabled = !isPendingFollow,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isFollowing) Icons.Filled.Close else Icons.Filled.Check,
                                contentDescription = if (isFollowing) "Unfollow" else "Follow",
                                modifier = Modifier.size(18.dp),
                                tint = if (isFollowing) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                    }
                }

                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = { onLikeClick(comment.id) })
                    ) {
                        Icon(
                            imageVector = if (comment.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            modifier = Modifier.size(16.dp),
                            tint = if (comment.isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (comment.likes > 0) {
                            Text(
                                text = "${comment.likes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (canReply) {
                        Text(
                            text = "Reply",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(onClick = { onReplyClick(comment.id) })
                        )
                    }
                }
            }
        }


        if (comment.replies.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (level > 0) {
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Spacer(modifier = Modifier.width(32.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${comment.replies.size} ${if (comment.replies.size == 1) "reply" else "replies"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            comment.replies.forEach { reply ->
                                CommentTree(
                                    comment = reply,
                                    onLikeClick = { replyId -> 
                                        if (onLikeClickForComment != null) {
                                            onLikeClickForComment(replyId, reply.isLiked)
                                        } else {
                                            onLikeClick(replyId)
                                        }
                                    },
                                    onReplyClick = { replyId -> onReplyClick(replyId) },
                                    onAuthorClick = onAuthorClickForUser?.let { { it(reply.author.id) } },
                                    isFollowing = getIsFollowingForUser?.invoke(reply.author.id) ?: isFollowing,
                                    isPendingFollow = getIsPendingForUser?.invoke(reply.author.id) ?: isPendingFollow,
                                    onToggleFollow = onToggleFollowForUser?.let { { it(reply.author.id) } },
                                    isOwnComment = getIsOwnComment?.invoke(reply.id) ?: false,
                                    getIsFollowingForUser = getIsFollowingForUser,
                                    getIsPendingForUser = getIsPendingForUser,
                                    onToggleFollowForUser = onToggleFollowForUser,
                                    onAuthorClickForUser = onAuthorClickForUser,
                                    onLikeClickForComment = onLikeClickForComment,
                                    getIsOwnComment = getIsOwnComment,
                                    level = 1,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
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






















