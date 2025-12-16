package com.sdu.threads.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.model.User
import com.sdu.threads.domain.usecase.FollowUserUseCase
import com.sdu.threads.domain.usecase.ObserveRecentSearchUseCase
import com.sdu.threads.domain.usecase.SearchUsersUseCase
import com.sdu.threads.domain.usecase.UnfollowUserUseCase
import com.sdu.threads.util.readableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<User> = emptyList(),
    val recentQueries: List<String> = emptyList(),
    val followingIds: Set<Long> = emptySet(),
    val pendingFollowIds: Set<Long> = emptySet(),
    val error: String? = null
)

sealed class SearchEvent {
    data class NavigateToDetail(val userId: Long) : SearchEvent()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    observeRecentSearchUseCase: ObserveRecentSearchUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state

    private val _events = MutableSharedFlow<SearchEvent>()
    val events = _events.asSharedFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            observeRecentSearchUseCase().collectLatest { recent ->
                _state.value = _state.value.copy(recentQueries = recent)
            }
        }
    }

    fun onQueryChange(value: String) {
        _state.value = _state.value.copy(query = value)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            search()
        }
    }

    fun search() {
        val query = _state.value.query
        if (query.isBlank()) {
            _state.value = _state.value.copy(results = emptyList(), isLoading = false)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { searchUsersUseCase(query) }
                .onSuccess { users ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        results = users,
                        error = null
                    )
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = throwable.readableMessage()
                    )
                }
        }
    }

    fun onUserSelected(userId: Long) {
        viewModelScope.launch {
            _events.emit(SearchEvent.NavigateToDetail(userId))
        }
    }

    fun toggleFollow(userId: Long) {
        val current = _state.value
        val isFollowing = current.followingIds.contains(userId)
        viewModelScope.launch {
            _state.value = _state.value.copy(
                pendingFollowIds = current.pendingFollowIds + userId,
                error = null
            )
            val previous = _state.value.followingIds
            runCatching {
                if (isFollowing) unfollowUserUseCase(userId) else followUserUseCase(userId)
            }.onSuccess {
                _state.value = _state.value.copy(
                    followingIds = if (isFollowing) previous - userId else previous + userId
                )
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    followingIds = previous,
                    error = throwable.readableMessage()
                )
            }
            _state.value = _state.value.copy(
                pendingFollowIds = _state.value.pendingFollowIds - userId
            )
        }
    }
}
