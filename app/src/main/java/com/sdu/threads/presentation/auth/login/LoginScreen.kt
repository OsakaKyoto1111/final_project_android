@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.threads.presentation.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.material3.TopAppBar

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Welcome back") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "SDU Threads",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Log in to explore student profiles.",
                style = MaterialTheme.typography.bodyLarge
            )
            ThreadsTextField(
                value = state.emailOrUsername,
                onValueChange = onEmailChange,
                label = "Email or username",
                leadingIcon = Icons.Default.Email
            )
            ThreadsTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = "Password",
                isPassword = true
            )
            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
            PrimaryButton(
                text = if (state.isLoading) "Loading..." else "Log In",
                enabled = !state.isLoading,
                onClick = onLogin
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "New to SDU Threads? Create account",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
            PrimaryButton(
                text = "Sign Up",
                enabled = !state.isLoading,
                onClick = onNavigateToRegister
            )
        }
    }
}
