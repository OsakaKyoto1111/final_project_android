package com.sdu.threads.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.sdu.threads.domain.model.Comment
import com.sdu.threads.domain.model.Post
import com.sdu.threads.domain.model.PostAttachment
import com.sdu.threads.domain.model.User
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class PostRequestDto(
    @SerializedName("description") val description: String?
)

data class CommentRequestDto(
    @SerializedName("text") val text: String,
    @SerializedName("parent_id") val parentId: Long? = null
)

data class MessageResponseDto(
    @SerializedName("message") val message: String
)

data class CreatePostResponseDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("message") val message: String? = null
)

data class PostUserDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName(value = "avatar", alternate = ["avatar_url"]) val avatar: String? = null
)

data class PostFileDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("url") val url: String
)

data class PostResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long? = null,
    @SerializedName("user") val user: PostUserDto? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("files") val files: List<PostFileDto>? = emptyList(),
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("comments") val commentsCount: Int = 0,
    @SerializedName("is_liked") val isLiked: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null
)

data class FeedResponseDto(
    @SerializedName("posts") val posts: List<PostResponseDto?> = emptyList(),
    @SerializedName("next_cursor") val nextCursor: String? = null,
    @SerializedName("has_more") val hasMore: Boolean = false
)


data class CommentDto(
    @SerializedName("id") val id: Long,
    @SerializedName("post_id") val postId: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("parent_id") val parentId: Long?,
    @SerializedName("text") val text: String,
    @SerializedName(value = "likes_count", alternate = ["likes"]) val likesCount: Int = 0,
    @SerializedName("is_liked") val isLiked: Boolean = false,
    @SerializedName("user") val user: PostUserDto? = null,
    @SerializedName("replies") val replies: List<CommentDto>? = emptyList(),
    @SerializedName("created_at") val createdAt: String? = null
) {
    val likes: Int get() = likesCount
}

data class CommentListResponseDto(
    @SerializedName(value = "items", alternate = ["comments"]) val items: List<CommentDto>,
    @SerializedName("page") val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 20,
    @SerializedName("total") val total: Int = 0
) {
    val comments: List<CommentDto> get() = items
}

data class CommentLikeResponseDto(
    @SerializedName("comment_id") val commentId: Long,
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("is_liked") val isLiked: Boolean = false
) {
    val liked: Boolean get() = isLiked
}

data class PostWithCommentsResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("user_id") val userId: Long? = null,
    @SerializedName("user") val user: PostUserDto? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("files") val files: List<PostFileDto>? = emptyList(),
    @SerializedName("likes_count") val likesCount: Int = 0,
    @SerializedName("is_liked") val isLiked: Boolean = false,
    @SerializedName("comments") val comments: List<CommentDto>? = emptyList()
)


fun PostResponseDto.toDomain(): Post = Post(
    id = id,
    description = description.orEmpty(),
    author = user?.toDomain() ?: User(
        id = userId ?: -1,
        email = "",
        nickname = "Unknown",
        firstName = "",
        lastName = "",
        grade = "",
        major = "",
        city = "",
        avatarUrl = "",
        description = "",
        postsCount = 0,
        followersCount = 0,
        followingCount = 0
    ),
    attachments = files.orEmpty().map {
        PostAttachment(url = fixUrl(it.url))
    },
    liked = isLiked,
    likesCount = likesCount,
    commentsCount = commentsCount,
    comments = emptyList(),
    createdAt = createdAt.toEpochMillis() ?: System.currentTimeMillis()
)


fun PostUserDto.toDomain(): User = User(
    id = id ?: -1,
    email = "",
    nickname = nickname.orEmpty(),
    firstName = "",
    lastName = "",
    grade = "",
    major = "",
    city = "",
    avatarUrl = avatar.orEmpty(),
    description = "",
    postsCount = 0,
    followersCount = 0,
    followingCount = 0
)

fun CommentDto.toDomain(): Comment = Comment(
    id = id,
    postId = postId,
    author = user.toDomainSafe(userId),
    text = text,
    parentId = parentId,
    likes = likesCount,
    isLiked = isLiked,
    createdAt = createdAt.toEpochMillis() ?: System.currentTimeMillis(),
    replies = replies.orEmpty().map { it.toDomain() }
)

fun List<CommentDto>?.flattenToDomain(): List<Comment> {
    if (this.isNullOrEmpty()) return emptyList()

    val result = mutableListOf<Comment>()

    fun traverse(nodes: List<CommentDto>) {
        nodes.forEach { node ->
            result += node.toDomain()
            node.replies?.let { traverse(it) }
        }
    }

    traverse(this)
    return result
}

private fun fixUrl(raw: String): String {
    return if (raw.startsWith("http")) raw
    else "http://13.62.55.188$raw"
}

fun PostWithCommentsResponseDto.toDomain(): Post = Post(
    id = id,
    description = description.orEmpty(),
    author = user.toDomainSafe(userId),
    attachments = files.orEmpty().map {
        PostAttachment(url = fixUrl(it.url))
    },
    liked = isLiked,
    likesCount = likesCount,
    commentsCount = comments?.size ?: 0,
    comments = comments.flattenToDomain(),
    createdAt = comments?.minOfOrNull { it.createdAt.toEpochMillis() ?: System.currentTimeMillis() }
        ?: System.currentTimeMillis()

)

private fun PostUserDto?.toDomainSafe(fallbackId: Long? = null): User =
    this?.toDomain() ?: User(
        id = fallbackId ?: -1,
        email = "",
        nickname = fallbackId?.let { "user #$it" } ?: "Unknown user",
        firstName = "",
        lastName = "",
        grade = "",
        major = "",
        city = "",
        avatarUrl = "",
        description = "",
        postsCount = 0,
        followersCount = 0,
        followingCount = 0
    )

private fun String?.toEpochMillis(): Long? {
    if (this == null) return null
    return runCatching {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        formatter.parse(this)?.time
    }.getOrNull()
}
