package com.sdu.threads.data.remote.api

import com.sdu.threads.data.remote.dto.ApiResponseDto
import com.sdu.threads.data.remote.dto.AuthCheckResponseDto
import com.sdu.threads.data.remote.dto.AuthResponseDto
import com.sdu.threads.data.remote.dto.CommentLikeResponseDto
import com.sdu.threads.data.remote.dto.CommentListResponseDto
import com.sdu.threads.data.remote.dto.CommentRequestDto
import com.sdu.threads.data.remote.dto.CreatePostResponseDto
import com.sdu.threads.data.remote.dto.FeedResponseDto
import com.sdu.threads.data.remote.dto.LoginRequestDto
import com.sdu.threads.data.remote.dto.MessageResponseDto
import com.sdu.threads.data.remote.dto.PostResponseDto
import com.sdu.threads.data.remote.dto.PostWithCommentsResponseDto
import com.sdu.threads.data.remote.dto.RegisterRequestDto
import com.sdu.threads.data.remote.dto.SearchResponseDto
import com.sdu.threads.data.remote.dto.SimpleUserDto
import com.sdu.threads.data.remote.dto.UpdateProfileRequestDto
import com.sdu.threads.data.remote.dto.UserDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ThreadsApi {
    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): ApiResponseDto<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequestDto
    ): ApiResponseDto<AuthResponseDto>

    @GET("auth/check")
    suspend fun checkAuth(): AuthCheckResponseDto

    @GET("user/me")
    suspend fun getMyProfile(): ApiResponseDto<UserDto>

    @PATCH("user/me")
    suspend fun updateProfile(
        @Body body: UpdateProfileRequestDto
    ): ApiResponseDto<UserDto>

    @DELETE("user/me")
    suspend fun deleteMyProfile(): ApiResponseDto<MessageResponseDto>

    @GET("user/search")
    suspend fun searchUsers(
        @Query("q") query: String
    ): ApiResponseDto<List<UserDto>>

    @GET("user/{id}")
    suspend fun getUserById(
        @Path("id") id: Long
    ): ApiResponseDto<UserDto>

    @Multipart
    @POST("user/avatar")
    suspend fun uploadAvatar(
        @Part files: List<MultipartBody.Part>
    ): ApiResponseDto<List<String>>

    @POST("user/{id}/follow")
    suspend fun followUser(
        @Path("id") id: Long
    ): ApiResponseDto<MessageResponseDto>

    @DELETE("user/{id}/follow")
    suspend fun unfollowUser(
        @Path("id") id: Long
    ): ApiResponseDto<MessageResponseDto>

    @GET("user/{id}/followers")
    suspend fun getFollowers(
        @Path("id") id: Long
    ): ApiResponseDto<List<SimpleUserDto>>

    @GET("user/{id}/following")
    suspend fun getFollowing(
        @Path("id") id: Long
    ): ApiResponseDto<List<SimpleUserDto>>

    @Multipart
    @POST("posts")
    suspend fun createPost(
        @Part("description") description: RequestBody?,
        @Part files: List<MultipartBody.Part>
    ): ApiResponseDto<CreatePostResponseDto>

    @GET("posts/{id}")
    suspend fun getPost(
        @Path("id") id: Long
    ): ApiResponseDto<PostWithCommentsResponseDto>

    @PATCH("posts/{id}")
    suspend fun updatePost(
        @Path("id") id: Long,
        @Body body: com.sdu.threads.data.remote.dto.PostRequestDto
    ): ApiResponseDto<MessageResponseDto>

    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Path("id") id: Long
    ): ApiResponseDto<MessageResponseDto>

    @Multipart
    @POST("posts/{id}/files")
    suspend fun uploadPostFiles(
        @Path("id") id: Long,
        @Part files: List<MultipartBody.Part>
    ): ApiResponseDto<List<String>>

    @GET("posts/me")
    suspend fun getMyPosts(): ApiResponseDto<List<PostResponseDto>>

    @GET("posts/user/{id}")
    suspend fun getUserPosts(
        @Path("id") id: Long
    ): ApiResponseDto<List<PostResponseDto>>

    @GET("posts/{id}/comments")
    suspend fun getComments(
        @Path("id") id: Long
    ): ApiResponseDto<CommentListResponseDto>

    @POST("posts/{id}/comments")
    suspend fun addComment(
        @Path("id") id: Long,
        @Body body: CommentRequestDto
    ): ApiResponseDto<MessageResponseDto>

    @POST("posts/{id}/like")
    suspend fun likePost(
        @Path("id") id: Long
    ): ApiResponseDto<MessageResponseDto>

    @DELETE("posts/{id}/like")
    suspend fun unlikePost(
        @Path("id") id: Long
    ): ApiResponseDto<MessageResponseDto>

    @POST("posts/comments/{comment_id}/like")
    suspend fun likeComment(
        @Path("comment_id") commentId: Long
    ): ApiResponseDto<CommentLikeResponseDto>

    @DELETE("posts/comments/{comment_id}/like")
    suspend fun unlikeComment(
        @Path("comment_id") commentId: Long
    ): ApiResponseDto<CommentLikeResponseDto>

    @GET("feed")
    suspend fun getFeed(): ApiResponseDto<FeedResponseDto>
}
