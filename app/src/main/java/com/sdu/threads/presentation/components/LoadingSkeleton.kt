package com.sdu.threads.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun PostSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                ShimmerBox(modifier = Modifier.width(120.dp).height(16.dp))
                ShimmerBox(modifier = Modifier.width(80.dp).height(12.dp))
            }
        }
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(100.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(16.dp))
        ShimmerBox(modifier = Modifier.width(200.dp).height(16.dp))
    }
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        start = Offset(shimmerTranslateAnim - 300f, shimmerTranslateAnim - 300f),
        end = Offset(shimmerTranslateAnim, shimmerTranslateAnim)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
    )
}






















