package com.sdu.threads.presentation.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdu.threads.domain.model.ProfileUpdate
import com.sdu.threads.domain.usecase.GetMyProfileUseCase
import com.sdu.threads.domain.usecase.UpdateProfileUseCase
import com.sdu.threads.util.readableMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val avatarUrl: String = "",
    val previewAvatarUri: String? = null,
    val isUploadingAvatar: Boolean = false,
    val grade: String = "",
    val major: String = "",
    val city: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

sealed class EditProfileEvent {
    data object OnSaved : EditProfileEvent()
    data object OnAvatarUploaded : EditProfileEvent()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadAvatarUseCase: com.sdu.threads.domain.usecase.UploadAvatarUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState(isLoading = true))
    val state: StateFlow<EditProfileUiState> = _state

    private val _events = MutableSharedFlow<EditProfileEvent>()
    val events = _events.asSharedFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { getMyProfileUseCase() }
                .onSuccess { user ->
                    _state.value = EditProfileUiState(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        avatarUrl = user.avatarUrl,
                        grade = user.grade,
                        major = user.major,
                        city = user.city,
                        description = user.description,
                        isLoading = false
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

    fun uploadAvatar(bytes: ByteArray, fileName: String, mimeType: String, previewUri: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isUploadingAvatar = true,
                previewAvatarUri = previewUri,
                error = null
            )
            runCatching { uploadAvatarUseCase(bytes, fileName, mimeType) }
                .onSuccess { user ->
                    _state.value = _state.value.copy(
                        avatarUrl = user.avatarUrl,
                        previewAvatarUri = null,
                        isUploadingAvatar = false,
                        error = null
                    )
                    _events.emit(EditProfileEvent.OnAvatarUploaded)
                    loadProfile()
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isUploadingAvatar = false,
                        previewAvatarUri = null,
                        error = throwable.readableMessage()
                    )
                }
        }
    }

    fun onFieldChange(field: (EditProfileUiState) -> String, newValue: String) {
        val current = _state.value
        _state.value = when (field) {
            EditProfileUiState::firstName -> current.copy(firstName = newValue)
            EditProfileUiState::lastName -> current.copy(lastName = newValue)
            EditProfileUiState::avatarUrl -> current.copy(avatarUrl = newValue)
            EditProfileUiState::grade -> current.copy(grade = newValue)
            EditProfileUiState::major -> current.copy(major = newValue)
            EditProfileUiState::city -> current.copy(city = newValue)
            EditProfileUiState::description -> current.copy(description = newValue)
            else -> current
        }
    }

    fun saveProfile() {
        val current = _state.value
        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, error = null, successMessage = null)
            val update = ProfileUpdate(
                firstName = current.firstName.takeIf { it.isNotBlank() },
                lastName = current.lastName.takeIf { it.isNotBlank() },
                avatarUrl = current.avatarUrl.takeIf { it.isNotBlank() },
                grade = current.grade.takeIf { it.isNotBlank() },
                major = current.major.takeIf { it.isNotBlank() },
                city = current.city.takeIf { it.isNotBlank() },
                description = current.description.takeIf { it.isNotBlank() }
            )
            runCatching { updateProfileUseCase(update) }
                .onSuccess {
                    _state.value = _state.value.copy(isLoading = false, successMessage = "Profile updated!")
                    _events.emit(EditProfileEvent.OnSaved)
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = throwable.readableMessage()
                    )
                }
        }
    }
}
