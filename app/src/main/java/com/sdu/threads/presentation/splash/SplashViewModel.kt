package com.sdu.threads.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.usecase.ObserveTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

data class SplashUiState(
    val isLoading: Boolean = true
)

sealed class SplashEvent {
    data object NavigateToHome : SplashEvent()
    data object NavigateToAuth : SplashEvent()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val observeTokenUseCase: ObserveTokenUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state

    private val _events = MutableSharedFlow<SplashEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val token = observeTokenUseCase().first()
            if (token.isNotBlank()) {
                _events.emit(SplashEvent.NavigateToHome)
            } else {
                _events.emit(SplashEvent.NavigateToAuth)
            }
            _state.value = SplashUiState(isLoading = false)
        }
    }
}
