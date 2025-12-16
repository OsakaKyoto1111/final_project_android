package com.sdu.threads.data.remote

import com.sdu.threads.data.local.datastore.TokenStorage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    private val publicPaths = setOf("/auth/login", "/auth/register")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        if (publicPaths.any { path.endsWith(it) || path.contains(it) }) {
            return chain.proceed(request)
        }

        val token = runBlocking(Dispatchers.IO) { tokenStorage.getToken() }.trim()
        if (token.isBlank()) {
            return chain.proceed(request)
        }

        val authedRequest = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authedRequest)
    }
}
