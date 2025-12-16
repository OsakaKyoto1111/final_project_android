package com.sdu.threads.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.usecase.LoginUseCase
import com.sdu.threads.util.Resource
import com.sdu.threads.util.readableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val emailOrUsername: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LoginEvent {
    data object NavigateToHome : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    private val _events = MutableSharedFlow<LoginEvent>()
    val events = _events.asSharedFlow()

    fun onEmailChanged(value: String) {
        _state.value = _state.value.copy(emailOrUsername = value)
    }

    fun onPasswordChanged(value: String) {
        _state.value = _state.value.copy(password = value)
    }

    fun login() {
        val current = _state.value
        if (current.emailOrUsername.isBlank() || current.password.isBlank()) {
            _state.value = current.copy(error = "Email/username and password required")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching {
                loginUseCase(current.emailOrUsername, current.password)
            }.onSuccess {
                _state.value = _state.value.copy(isLoading = false)
                _events.emit(LoginEvent.NavigateToHome)
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = throwable.readableMessage()
                )
            }
        }
    }
}
