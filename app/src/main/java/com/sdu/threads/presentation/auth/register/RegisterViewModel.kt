package com.sdu.threads.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.usecase.RegisterUseCase
import com.sdu.threads.util.readableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val nickname: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val grade: String = "",
    val major: String = "",
    val city: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class RegisterEvent {
    data object NavigateHome : RegisterEvent()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state

    private val _events = MutableSharedFlow<RegisterEvent>()
    val events = _events.asSharedFlow()

    fun onValueChange(field: (RegisterUiState) -> String, new: String) {
        val current = _state.value
        _state.value = when (field) {
            RegisterUiState::email -> current.copy(email = new)
            RegisterUiState::nickname -> current.copy(nickname = new)
            RegisterUiState::password -> current.copy(password = new)
            RegisterUiState::firstName -> current.copy(firstName = new)
            RegisterUiState::lastName -> current.copy(lastName = new)
            RegisterUiState::grade -> current.copy(grade = new)
            RegisterUiState::major -> current.copy(major = new)
            RegisterUiState::city -> current.copy(city = new)
            else -> current
        }
    }

    fun register() {
        val current = _state.value
        if (current.email.isBlank() || current.nickname.isBlank() || current.password.isBlank()) {
            _state.value = current.copy(error = "Email, nickname, and password are required")
            return
        }
        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, error = null)
            runCatching {
                registerUseCase(
                    email = current.email,
                    nickname = current.nickname,
                    password = current.password,
                    firstName = current.firstName.ifBlank { null },
                    lastName = current.lastName.ifBlank { null },
                    grade = current.grade.ifBlank { null },
                    major = current.major.ifBlank { null },
                    city = current.city.ifBlank { null }
                )
            }.onSuccess {
                _state.value = _state.value.copy(isLoading = false)
                _events.emit(RegisterEvent.NavigateHome)
            }.onFailure { throwable ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = throwable.readableMessage()
                )
            }
        }
    }
}
