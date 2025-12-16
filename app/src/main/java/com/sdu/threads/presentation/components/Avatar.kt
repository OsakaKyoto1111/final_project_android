package com.sdu.threads.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sdu.threads.R

@Composable
fun Avatar(
    avatarUrl: String?,
    initials: String,
    modifier: Modifier = Modifier
) {
    val placeholder = initials.take(2).uppercase()
    val finalModifier = if (modifier == Modifier) {
        modifier.size(64.dp)
    } else {
        modifier
    }
    
    val fixedUrl = if (!avatarUrl.isNullOrBlank()) {
        if (avatarUrl.startsWith("http")) {
            avatarUrl
        } else {
            "http://13.62.55.188$avatarUrl"
        }
    } else {
        null
    }
    
    if (fixedUrl != null) {
        AsyncImage(
            model = fixedUrl,
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = finalModifier
                .clip(CircleShape),
            error = painterResource(id = R.drawable.avatar_placeholder),
            placeholder = painterResource(id = R.drawable.avatar_placeholder)
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.avatar_placeholder),
            contentDescription = "avatar placeholder",
            contentScale = ContentScale.Crop,
            modifier = finalModifier
                .clip(CircleShape)
        )
    }
}
