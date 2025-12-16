package com.sdu.threads.data.repository

import com.sdu.threads.data.remote.api.ThreadsApi
import com.sdu.threads.data.remote.dto.CommentRequestDto
import com.sdu.threads.data.remote.dto.requireData
import com.sdu.threads.data.remote.dto.toDomain
import com.sdu.threads.domain.model.Comment
import com.sdu.threads.domain.model.Post
import com.sdu.threads.domain.model.UploadFile
import com.sdu.threads.domain.repository.CommentList
import com.sdu.threads.domain.repository.PostRepository
import com.sdu.threads.util.ApiResult
import com.sdu.threads.util.toApiResult
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostRepositoryImpl @Inject constructor(
    private val api: ThreadsApi
) : PostRepository {

    override suspend fun getFeed(): ApiResult<List<Post>> {
        return runCatching {
            val response = api.getFeed().requireData()
            response.posts
                .mapNotNull { it?.toDomain() }
                .distinctBy { it.id }
        }.toApiResult()
    }

    override suspend fun getPost(postId: Long): ApiResult<Post> {
        return runCatching {
            api.getPost(postId).requireData().toDomain()
        }.toApiResult()
    }

    override suspend fun getMyPosts(): ApiResult<List<Post>> {
        return runCatching {
            api.getMyPosts()
                .requireData()
                .map { it.toDomain() }
                .distinctBy { it.id }
        }.toApiResult()
    }

    override suspend fun getUserPosts(userId: Long): ApiResult<List<Post>> {
        return runCatching {
            api.getUserPosts(userId)
                .requireData()
                .map { it.toDomain() }
                .distinctBy { it.id }
        }.toApiResult()
    }

    override suspend fun createPost(
        description: String?,
        attachments: List<UploadFile>
    ): ApiResult<Post> {
        return runCatching {
            val descriptionPart = description
                ?.takeIf { it.isNotBlank() }
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

            val fileParts = attachments.map { file ->

                val safeMime = file.mimeType?.lowercase() ?: "image/jpeg"

                val extension = when {
                    safeMime.contains("jpeg") || safeMime.contains("jpg") -> ".jpg"
                    safeMime.contains("png") -> ".png"
                    safeMime.contains("gif") -> ".gif"
                    safeMime.contains("mp4") -> ".mp4"

                    safeMime.contains("heic")
                            || safeMime.contains("heif")
                            || safeMime.contains("webp") -> ".jpg"

                    else -> ".jpg"
                }

                val safeName = if (file.name.contains(".")) {
                    file.name.substringBeforeLast(".") + extension
                } else {
                    file.name + extension
                }

                val mimeTypeFinal = when (extension) {
                    ".jpg" -> "image/jpeg"
                    ".png" -> "image/png"
                    ".gif" -> "image/gif"
                    ".mp4" -> "video/mp4"
                    else -> "image/jpeg"
                }

                val body = file.bytes.toRequestBody(mimeTypeFinal.toMediaTypeOrNull())

                MultipartBody.Part.createFormData("files", safeName, body)
            }

            val response = api.createPost(descriptionPart, fileParts).requireData()
            val postId = response.id ?: error("Post ID missing")

            api.getPost(postId).requireData().toDomain()
        }.toApiResult()
    }


    override suspend fun updatePost(postId: Long, description: String?): ApiResult<Unit> {
        return runCatching<Unit> {
            val body = com.sdu.threads.data.remote.dto.PostRequestDto(description = description)
            api.updatePost(postId, body).requireData()
        }.toApiResult()
    }

    override suspend fun deletePost(postId: Long): ApiResult<Unit> {
        return runCatching<Unit> {
            api.deletePost(postId).requireData()
        }.toApiResult()
    }

    override suspend fun likePost(postId: Long): ApiResult<Unit> {
        return runCatching<Unit> {
            api.likePost(postId).requireData()
        }.toApiResult()
    }

    override suspend fun unlikePost(postId: Long): ApiResult<Unit> {
        return runCatching<Unit> {
            api.unlikePost(postId).requireData()
        }.toApiResult()
    }

    override suspend fun getComments(postId: Long): ApiResult<CommentList> {
        return runCatching {
            val response = api.getComments(postId).requireData()
            CommentList(
                comments = response.items.map { it.toDomain() },
                page = response.page,
                pageSize = response.pageSize,
                total = response.total
            )
        }.toApiResult()
    }

    override suspend fun addComment(
        postId: Long,
        text: String,
        parentId: Long?
    ): ApiResult<Comment> {
        return runCatching {
            api.addComment(postId, CommentRequestDto(text = text, parentId = parentId))
                .requireData()
            val comments = api.getComments(postId).requireData()
            comments.items.firstOrNull { it.text == text && it.parentId == parentId }
                ?.toDomain()
                ?: throw IllegalStateException("Comment not found after creation")
        }.toApiResult()
    }

    override suspend fun likeComment(commentId: Long): ApiResult<Unit> {
        return runCatching<Unit> {
            api.likeComment(commentId).requireData()
        }.toApiResult()
    }

    override suspend fun unlikeComment(commentId: Long): ApiResult<Unit> {
        return runCatching<Unit> {
            api.unlikeComment(commentId).requireData()
        }.toApiResult()
    }
}
