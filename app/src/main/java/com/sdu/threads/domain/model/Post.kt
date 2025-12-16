package com.sdu.threads.domain.model

data class PostAttachment(
    val url: String
)

data class Comment(
    val id: Long,
    val postId: Long,
    val author: User,
    val text: String,
    val parentId: Long? = null,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val replies: List<Comment> = emptyList()
)

data class Post(
    val id: Long,
    val description: String,
    val author: User,
    val attachments: List<PostAttachment> = emptyList(),
    val liked: Boolean = false,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val comments: List<Comment> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val isLocalOnly: Boolean = false
)

data class UploadFile(
    val name: String,
    val bytes: ByteArray,
    val mimeType: String
)
