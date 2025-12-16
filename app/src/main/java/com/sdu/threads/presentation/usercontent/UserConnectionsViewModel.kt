package com.sdu.threads.presentation.usercontent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.model.User
import com.sdu.threads.domain.usecase.GetFollowersUseCase
import com.sdu.threads.domain.usecase.GetFollowingUseCase
import com.sdu.threads.presentation.navigation.ConnectionType
import com.sdu.threads.util.readableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserConnectionsUiState(
    val title: String = "Connections",
    val isLoading: Boolean = true,
    val users: List<User> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class UserConnectionsViewModel @Inject constructor(
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowingUseCase: GetFollowingUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: Long = checkNotNull(savedStateHandle["userId"])
    private val typeValue: String = savedStateHandle["type"] ?: ConnectionType.Followers.path
    private val connectionType: ConnectionType = ConnectionType.fromPath(typeValue)

    private val _state = MutableStateFlow(
        UserConnectionsUiState(
            title = if (connectionType == ConnectionType.Followers) "Followers" else "Following"
        )
    )
    val state: StateFlow<UserConnectionsUiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching {
                when (connectionType) {
                    ConnectionType.Followers -> getFollowersUseCase(userId)
                    ConnectionType.Following -> getFollowingUseCase(userId)
                }
            }.onSuccess { users ->
                _state.value = _state.value.copy(isLoading = false, users = users)
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = throwable.readableMessage()
                )
            }
        }
    }
}
