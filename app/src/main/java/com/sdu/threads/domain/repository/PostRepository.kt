package com.sdu.threads.domain.repository

import com.sdu.threads.domain.model.Comment
import com.sdu.threads.domain.model.Post
import com.sdu.threads.domain.model.UploadFile
import com.sdu.threads.util.ApiResult

interface PostRepository {
    suspend fun getFeed(): ApiResult<List<Post>>
    suspend fun getPost(postId: Long): ApiResult<Post>
    suspend fun getMyPosts(): ApiResult<List<Post>>
    suspend fun getUserPosts(userId: Long): ApiResult<List<Post>>
    suspend fun createPost(description: String?, attachments: List<UploadFile> = emptyList()): ApiResult<Post>
    suspend fun updatePost(postId: Long, description: String?): ApiResult<Unit>
    suspend fun deletePost(postId: Long): ApiResult<Unit>
    suspend fun likePost(postId: Long): ApiResult<Unit>
    suspend fun unlikePost(postId: Long): ApiResult<Unit>
    suspend fun getComments(postId: Long): ApiResult<CommentList>
    suspend fun addComment(postId: Long, text: String, parentId: Long? = null): ApiResult<Comment>
    suspend fun likeComment(commentId: Long): ApiResult<Unit>
    suspend fun unlikeComment(commentId: Long): ApiResult<Unit>
}

data class CommentList(
    val comments: List<Comment>,
    val page: Int,
    val pageSize: Int,
    val total: Int
)
