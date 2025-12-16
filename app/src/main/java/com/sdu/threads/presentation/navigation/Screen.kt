package com.sdu.threads.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit_profile")
    data object Search : Screen("search")
    data object CreatePost : Screen("create_post")
    data object UserDetail : Screen("user_detail/{userId}") {
        fun createRoute(userId: Long) = "user_detail/$userId"
    }
    data object UserPosts : Screen("user_posts/{userId}?isSelf={isSelf}") {
        fun createRoute(userId: Long, isSelf: Boolean = false) =
            "user_posts/$userId?isSelf=$isSelf"
    }
    data object UserConnections : Screen("user_connections/{userId}/{type}") {
        fun createRoute(userId: Long, type: ConnectionType) =
            "user_connections/$userId/${type.path}"
    }
    data object Comments : Screen("comments/{postId}") {
        fun createRoute(postId: Long) = "comments/$postId"
    }
}

enum class ConnectionType(val path: String) {
    Followers("followers"),
    Following("following");

    companion object {
        fun fromPath(value: String): ConnectionType =
            values().firstOrNull { it.path.equals(value, ignoreCase = true) } ?: Followers
    }
}
