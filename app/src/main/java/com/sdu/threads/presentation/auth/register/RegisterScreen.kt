@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.threads.presentation.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdu.threads.presentation.components.PrimaryButton
import com.sdu.threads.presentation.components.ThreadsTextField

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onValueChange: (RegisterUiState.() -> String, String) -> Unit,
    onSubmit: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create account") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SDU Threads",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Show your best academic self.",
                style = MaterialTheme.typography.bodyLarge
            )
            ThreadsTextField(
                value = state.email,
                onValueChange = { onValueChange(RegisterUiState::email, it) },
                label = "SDU email"
            )
            ThreadsTextField(
                value = state.nickname,
                onValueChange = { onValueChange(RegisterUiState::nickname, it) },
                label = "Nickname"
            )
            ThreadsTextField(
                value = state.password,
                onValueChange = { onValueChange(RegisterUiState::password, it) },
                label = "Password",
                isPassword = true
            )
            ThreadsTextField(
                value = state.firstName,
                onValueChange = { onValueChange(RegisterUiState::firstName, it) },
                label = "First name"
            )
            ThreadsTextField(
                value = state.lastName,
                onValueChange = { onValueChange(RegisterUiState::lastName, it) },
                label = "Last name"
            )
            ThreadsTextField(
                value = state.grade,
                onValueChange = { onValueChange(RegisterUiState::grade, it) },
                label = "Grade (e.g. 2 курс)"
            )
            ThreadsTextField(
                value = state.major,
                onValueChange = { onValueChange(RegisterUiState::major, it) },
                label = "Major"
            )
            ThreadsTextField(
                value = state.city,
                onValueChange = { onValueChange(RegisterUiState::city, it) },
                label = "City"
            )
            state.error?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(4.dp))
            PrimaryButton(
                text = if (state.isLoading) "Creating..." else "Sign Up",
                enabled = !state.isLoading,
                onClick = onSubmit
            )
        }
    }
}
