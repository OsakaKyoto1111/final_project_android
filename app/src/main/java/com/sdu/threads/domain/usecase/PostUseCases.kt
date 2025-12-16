package com.sdu.threads.domain.usecase

import com.sdu.threads.domain.model.Comment
import com.sdu.threads.domain.model.Post
import com.sdu.threads.domain.model.UploadFile
import com.sdu.threads.domain.repository.CommentList
import com.sdu.threads.domain.repository.PostRepository
import com.sdu.threads.util.ApiResult
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(): ApiResult<List<Post>> = repository.getFeed()
}

class GetPostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Long): ApiResult<Post> =
        repository.getPost(postId)
}

class GetMyPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(): ApiResult<List<Post>> = repository.getMyPosts()
}

class GetUserPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(userId: Long): ApiResult<List<Post>> =
        repository.getUserPosts(userId)
}

class CreatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(
        description: String?,
        attachments: List<UploadFile> = emptyList()
    ): ApiResult<Post> = repository.createPost(description, attachments)
}

class UpdatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Long, description: String?): ApiResult<Unit> =
        repository.updatePost(postId, description)
}

class DeletePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Long): ApiResult<Unit> = repository.deletePost(postId)
}

class LikePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Long): ApiResult<Unit> = repository.likePost(postId)
}

class UnlikePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Long): ApiResult<Unit> = repository.unlikePost(postId)
}

class GetCommentsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Long): ApiResult<CommentList> =
        repository.getComments(postId)
}

class AddCommentUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(
        postId: Long,
        text: String,
        parentId: Long? = null
    ): ApiResult<Comment> = repository.addComment(postId, text, parentId)
}

class LikeCommentUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(commentId: Long): ApiResult<Unit> = repository.likeComment(commentId)
}

class UnlikeCommentUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(commentId: Long): ApiResult<Unit> = repository.unlikeComment(commentId)
}
