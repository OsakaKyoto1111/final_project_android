@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.threads.presentation.editprofile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdu.threads.presentation.components.Avatar
import com.sdu.threads.presentation.components.ErrorView
import com.sdu.threads.presentation.components.LoadingView
import com.sdu.threads.presentation.components.PrimaryButton
import com.sdu.threads.presentation.components.ThreadsTextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditProfileScreen(
    state: EditProfileUiState,
    events: SharedFlow<EditProfileEvent>,
    onFieldChange: (EditProfileUiState.() -> String, String) -> Unit,
    onSave: () -> Unit,
    onUploadAvatar: (ByteArray, String, String, String?) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                scope.launch {
                    val bytes = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    } ?: return@launch
                    val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
                    val ext = when (mime) {
                        "image/png" -> "png"
                        "image/gif" -> "gif"
                        "image/jpeg", "image/jpg" -> "jpg"
                        else -> "jpg"
                    }
                    val name = "avatar.$ext"

                    onUploadAvatar(bytes, name, mime, uri.toString())

                }
            }
        }
    )

    LaunchedEffect(events) {
        events.collectLatest { event ->
            when (event) {
                is EditProfileEvent.OnSaved -> {
                    snackbarHostState.showSnackbar("Profile updated")
                }
                is EditProfileEvent.OnAvatarUploaded -> {
                    snackbarHostState.showSnackbar("Avatar updated successfully")
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        when {
            state.isLoading -> LoadingView(modifier = Modifier.padding(padding))
            state.error != null -> ErrorView(
                modifier = Modifier.padding(padding),
                message = state.error
            )
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Update your academic vibe",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clickable(enabled = !state.isUploadingAvatar) { avatarPicker.launch("image/*") }
                    ) {
                        Box {
                            val previewUri = state.previewAvatarUri
                            val currentAvatarUrl = state.avatarUrl.takeIf { it.isNotBlank() && !it.startsWith("content://") && !it.startsWith("file://") }

                            if (previewUri != null) {

                                AsyncImage(
                                    model = previewUri,
                                    contentDescription = "Avatar preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Avatar(
                                    avatarUrl = currentAvatarUrl,
                                    initials = "${state.firstName.firstOrNull()}${state.lastName.firstOrNull()}".takeIf { it.isNotBlank() } ?: "U",
                                    modifier = Modifier.size(120.dp)
                                )
                            }

                            if (state.isUploadingAvatar) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(48.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clickable(enabled = !state.isUploadingAvatar) { avatarPicker.launch("image/*") }
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CameraAlt,
                            contentDescription = "Change avatar",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = if (state.isUploadingAvatar) "Uploading avatar..." else "Tap to change avatar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                ThreadsTextField(
                    value = state.firstName,
                    onValueChange = { onFieldChange(EditProfileUiState::firstName, it) },
                    label = "First name"
                )
                ThreadsTextField(
                    value = state.lastName,
                    onValueChange = { onFieldChange(EditProfileUiState::lastName, it) },
                    label = "Last name"
                )
                ThreadsTextField(
                    value = state.grade,
                    onValueChange = { onFieldChange(EditProfileUiState::grade, it) },
                    label = "Grade"
                )
                ThreadsTextField(
                    value = state.major,
                    onValueChange = { onFieldChange(EditProfileUiState::major, it) },
                    label = "Major"
                )
                ThreadsTextField(
                    value = state.city,
                    onValueChange = { onFieldChange(EditProfileUiState::city, it) },
                    label = "City"
                )
                ThreadsTextField(
                    value = state.description,
                    onValueChange = { onFieldChange(EditProfileUiState::description, it) },
                    label = "Bio / Description",
                    singleLine = false
                )
                state.error?.let { errorMessage: String ->
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                PrimaryButton(
                    text = if (state.isLoading) "Saving..." else "Save",
                    enabled = !state.isLoading,
                    onClick = onSave
                )
            }
        }
    }
}
