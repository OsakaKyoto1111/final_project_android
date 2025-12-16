package com.sdu.threads.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sdu.threads.domain.model.User

@Composable
fun UserCard(
    user: User,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                avatarUrl = user.avatarUrl,
                initials = user.nickname
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.nickname,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = user.fullName.ifBlank { user.email },
                    style = MaterialTheme.typography.bodyLarge
                )
                if (user.city.isNotBlank()) {
                    Text(
                        text = user.city,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            trailingContent?.invoke()
        }
    }
}

