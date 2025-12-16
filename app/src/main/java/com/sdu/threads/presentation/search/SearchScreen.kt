package com.sdu.threads.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdu.threads.domain.model.User
import com.sdu.threads.presentation.components.Avatar

@Composable
fun SearchScreen(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onUserSelected: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Find someone",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface
        )

        TextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            placeholder = { Text("") },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
            ),
            shape = MaterialTheme.shapes.extraLarge
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(state.results) { user ->
                UserSearchItem(
                    user = user,
                    onClick = { onUserSelected(user.id) },
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(
                    modifier = Modifier.padding(start = 60.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun UserSearchItem(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            avatarUrl = user.avatarUrl,
            initials = user.nickname
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.fullName.ifBlank { user.nickname },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "@${user.nickname}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
