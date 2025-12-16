package com.sdu.threads.presentation.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.model.User
import com.sdu.threads.domain.usecase.GetUserByIdUseCase
import com.sdu.threads.domain.usecase.GetMyProfileUseCase
import com.sdu.threads.domain.usecase.GetFollowingUseCase
import com.sdu.threads.domain.usecase.FollowUserUseCase
import com.sdu.threads.domain.usecase.UnfollowUserUseCase
import com.sdu.threads.util.readableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserDetailUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null,
    val isFollowing: Boolean = false,
    val isProcessing: Boolean = false
)

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getFollowingUseCase: GetFollowingUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: Long = checkNotNull(savedStateHandle["userId"])

    private val _state = MutableStateFlow(UserDetailUiState())
    val state: StateFlow<UserDetailUiState> = _state

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            _state.value = UserDetailUiState(isLoading = true)
            runCatching { getUserByIdUseCase(userId) }
                .onSuccess { user -> 
                    val isFollowing = runCatching {
                        val currentUser = getMyProfileUseCase()
                        val following = getFollowingUseCase(currentUser.id)
                        following.any { it.id == userId }
                    }.getOrNull() ?: false
                    
                    _state.value = _state.value.copy(
                        isLoading = false, 
                        user = user,
                        isFollowing = isFollowing
                    )
                }
                .onFailure { throwable ->
                    _state.value = UserDetailUiState(
                        isLoading = false,
                        error = throwable.readableMessage()
                    )
                }
        }
    }

    fun toggleFollow() {
        val current = _state.value
        val isFollowing = current.isFollowing
        viewModelScope.launch {
            _state.value = current.copy(isProcessing = true, error = null)
            runCatching {
                if (isFollowing) unfollowUserUseCase(userId) else followUserUseCase(userId)
            }.onSuccess {
                fetchUser()
                _state.value = _state.value.copy(
                    isProcessing = false,
                    isFollowing = !isFollowing
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isProcessing = false,
                    error = throwable.readableMessage()
                )
            }
        }
    }
}
