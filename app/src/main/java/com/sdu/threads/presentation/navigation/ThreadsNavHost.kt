package com.sdu.threads.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sdu.threads.presentation.auth.login.LoginEvent
import com.sdu.threads.presentation.auth.login.LoginScreen
import com.sdu.threads.presentation.auth.login.LoginViewModel
import com.sdu.threads.presentation.auth.register.RegisterEvent
import com.sdu.threads.presentation.auth.register.RegisterScreen
import com.sdu.threads.presentation.auth.register.RegisterViewModel
import com.sdu.threads.presentation.editprofile.EditProfileScreen
import com.sdu.threads.presentation.editprofile.EditProfileEvent
import com.sdu.threads.presentation.editprofile.EditProfileViewModel
import com.sdu.threads.presentation.home.HomeScreen
import com.sdu.threads.presentation.home.HomeViewModel
import com.sdu.threads.presentation.home.CreatePostScreen
import com.sdu.threads.presentation.home.HomeEvent
import com.sdu.threads.presentation.profile.ProfileScreen
import com.sdu.threads.presentation.profile.ProfileEvent
import com.sdu.threads.presentation.profile.ProfileViewModel
import com.sdu.threads.presentation.search.SearchEvent
import com.sdu.threads.presentation.search.SearchScreen
import com.sdu.threads.presentation.search.SearchViewModel
import com.sdu.threads.presentation.splash.SplashEvent
import com.sdu.threads.presentation.splash.SplashScreen
import com.sdu.threads.presentation.splash.SplashViewModel
import com.sdu.threads.presentation.userdetail.UserDetailScreen
import com.sdu.threads.presentation.userdetail.UserDetailViewModel
import com.sdu.threads.presentation.usercontent.UserConnectionsScreen
import com.sdu.threads.presentation.usercontent.UserConnectionsViewModel
import com.sdu.threads.presentation.usercontent.UserPostsScreen
import com.sdu.threads.presentation.usercontent.UserPostsViewModel
import com.sdu.threads.presentation.comments.CommentsScreen
import com.sdu.threads.presentation.comments.CommentsViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import com.sdu.threads.presentation.navigation.components.ThreadsBottomBar

private const val PROFILE_UPDATED_RESULT = "profile_updated"

@Composable
fun ThreadsNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarRoutes = setOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.CreatePost.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (currentDestination?.route in bottomBarRoutes) {
                ThreadsBottomBar(
                    currentDestination = currentDestination,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Splash.route) {
                val viewModel: SplashViewModel = hiltViewModel()
                LaunchedEffect(Unit) {
                    viewModel.events.collectLatest { event ->
                        when (event) {
                            SplashEvent.NavigateToAuth -> navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                            SplashEvent.NavigateToHome -> navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                }
                SplashScreen()
            }
            composable(Screen.Login.route) {
                val viewModel: LoginViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle().value
                LaunchedEffect(Unit) {
                    viewModel.events.collectLatest { event ->
                        when (event) {
                            LoginEvent.NavigateToHome -> navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
                LoginScreen(
                    state = state,
                    onEmailChange = viewModel::onEmailChanged,
                    onPasswordChange = viewModel::onPasswordChanged,
                    onLogin = viewModel::login,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            composable(Screen.Register.route) {
                val viewModel: RegisterViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle().value
                LaunchedEffect(Unit) {
                    viewModel.events.collectLatest { event ->
                        when (event) {
                            RegisterEvent.NavigateHome -> navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    }
                }
                RegisterScreen(
                    state = state,
                    onValueChange = viewModel::onValueChange,
                    onSubmit = viewModel::register
                )
            }
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle().value
                HomeScreen(
                    state = state,
                    onOpenComposer = { navController.navigate(Screen.CreatePost.route) },
                    onRefresh = {
                        viewModel.loadProfile()
                        viewModel.refreshFeed()
                    },
                    onToggleLike = viewModel::toggleLike,
                    onPostClick = { postId ->
                        navController.navigate(Screen.Comments.createRoute(postId))
                    },
                    onAuthorClick = { userId ->
                        navController.navigate(Screen.UserDetail.createRoute(userId))
                    },
                    onToggleFollowUser = viewModel::toggleFollow
                )
            }
            composable(Screen.Profile.route) { entry ->
                val viewModel: ProfileViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle().value
                LaunchedEffect(Unit) {
                    viewModel.events.collectLatest { event ->
                        when (event) {
                            ProfileEvent.LoggedOut -> navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    }
                }
                LaunchedEffect(entry) {
                    entry.savedStateHandle
                        .getStateFlow(PROFILE_UPDATED_RESULT, false)
                        .collectLatest { updated ->
                            if (updated) {
                                viewModel.loadProfile()
                                entry.savedStateHandle[PROFILE_UPDATED_RESULT] = false
                            }
                        }
                }
                ProfileScreen(
                    state = state,
                    onEditClick = { navController.navigate(Screen.EditProfile.route) },
                    onLogout = viewModel::logout,
                    onUploadAvatar = viewModel::uploadAvatar,
                    onViewFollowers = { userId ->
                        navController.navigate(
                            Screen.UserConnections.createRoute(userId, ConnectionType.Followers)
                        )
                    },
                    onViewFollowing = { userId ->
                        navController.navigate(
                            Screen.UserConnections.createRoute(userId, ConnectionType.Following)
                        )
                    },
                    onDeletePost = viewModel::deletePost,
                    onToggleLike = viewModel::toggleLike,
                    onAuthorClick = { userId ->
                        navController.navigate(Screen.UserDetail.createRoute(userId))
                    },
                    onCommentClick = { postId ->
                        navController.navigate(Screen.Comments.createRoute(postId))
                    }
                )
            }
            composable(Screen.EditProfile.route) {
                val viewModel: EditProfileViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle().value
                LaunchedEffect(Unit) {
                    viewModel.events.collectLatest { event ->
                        when (event) {
                            is EditProfileEvent.OnSaved -> {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set(PROFILE_UPDATED_RESULT, true)
                                navController.popBackStack()
                            }
                            else -> Unit
                        }
                    }
                }
                EditProfileScreen(
                    state = state,
                    events = viewModel.events,
                    onFieldChange = viewModel::onFieldChange,
                    onSave = viewModel::saveProfile,
                    onUploadAvatar = viewModel::uploadAvatar
                )
            }
            composable(Screen.Search.route) {
                val viewModel: SearchViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle().value
                LaunchedEffect(Unit) {
                    viewModel.events.collectLatest { event ->
                        when (event) {
                            is SearchEvent.NavigateToDetail -> navController.navigate(
                                Screen.UserDetail.createRoute(event.userId)
                            )
                        }
                    }
                }
                SearchScreen(
                    state = state,
                    onQueryChange = viewModel::onQueryChange,
                    onUserSelected = viewModel::onUserSelected
                )
            }
            composable(Screen.CreatePost.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.Home.route)
                }
                val viewModel: HomeViewModel = hiltViewModel(parentEntry)
                val state = viewModel.state.collectAsStateWithLifecycle().value
                LaunchedEffect(Unit) {
                    viewModel.events.collectLatest { event ->
                        when (event) {
                            HomeEvent.PostCreated -> navController.popBackStack()
                        }
                    }
                }
                CreatePostScreen(
                    state = state,
                    onBack = { navController.popBackStack() },
                    onValueChange = viewModel::onNewPostChanged,
                    onAddAttachments = viewModel::addNewPostAttachments,
                    onClearAttachments = viewModel::clearNewPostAttachments,
                    onRemoveAttachment = viewModel::removeNewPostAttachment,
                    onSubmit = viewModel::createPost
                )
            }
            composable(
                route = Screen.UserDetail.route,
                arguments = listOf(navArgument("userId") { type = NavType.LongType })
            ) {
                val viewModel: UserDetailViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle().value
                UserDetailScreen(
                    state = state,
                    onToggleFollow = viewModel::toggleFollow,
                    onBack = { navController.popBackStack() },
                    onViewFollowers = { userId ->
                        navController.navigate(
                            Screen.UserConnections.createRoute(userId, ConnectionType.Followers)
                        )
                    },
                    onViewFollowing = { userId ->
                        navController.navigate(
                            Screen.UserConnections.createRoute(userId, ConnectionType.Following)
                        )
                    },
                    onViewPosts = { userId ->
                        navController.navigate(Screen.UserPosts.createRoute(userId))
                    }
                )
            }
            composable(
                route = Screen.UserPosts.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.LongType },
                    navArgument("isSelf") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                val viewModel: UserPostsViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle().value
                UserPostsScreen(
                    state = state,
                    onBack = { navController.popBackStack() },
                    onRefresh = viewModel::loadPosts,
                    onToggleLike = viewModel::toggleLike,
                    onAuthorClick = { userId ->
                        navController.navigate(Screen.UserDetail.createRoute(userId))
                    }
                )
            }
            composable(
                route = Screen.UserConnections.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.LongType },
                    navArgument("type") { type = NavType.StringType }
                )
            ) {
                val viewModel: UserConnectionsViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle().value
                UserConnectionsScreen(
                    state = state,
                    onBack = { navController.popBackStack() },
                    onRefresh = viewModel::load,
                    onUserClick = { userId ->
                        navController.navigate(Screen.UserDetail.createRoute(userId))
                    }
                )
            }
            composable(
                route = Screen.Comments.route,
                arguments = listOf(navArgument("postId") { type = NavType.LongType })
            ) {
                val viewModel: CommentsViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle().value
                CommentsScreen(
                    state = state,
                    onBack = { navController.popBackStack() },
                    onCommentTextChange = viewModel::onCommentTextChange,
                    onAddComment = viewModel::addComment,
                    onReplyToComment = viewModel::setReplyToComment,
                    onLikeComment = viewModel::toggleLikeComment,
                    onAuthorClick = { userId ->
                        navController.navigate(Screen.UserDetail.createRoute(userId))
                    },
                    onToggleFollow = viewModel::toggleFollow,
                    onRefresh = viewModel::loadComments
                )
            }
        }
    }
}
