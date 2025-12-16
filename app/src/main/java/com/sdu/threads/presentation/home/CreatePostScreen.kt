@file:OptIn(ExperimentalMaterial3Api::class)

package com.sdu.threads.presentation.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sdu.threads.domain.model.UploadFile
import com.sdu.threads.presentation.components.ErrorView
import com.sdu.threads.presentation.components.PrimaryButton
import com.sdu.threads.presentation.components.ThreadsTextField
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape

@Composable
fun CreatePostScreen(
    state: HomeUiState,
    onBack: () -> Unit,
    onValueChange: (String) -> Unit,
    onAddAttachments: (List<UploadFile>) -> Unit,
    onClearAttachments: () -> Unit,
    onRemoveAttachment: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val attachmentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            scope.launch {
                val files = toUploadFiles(context, uris)
                onAddAttachments(files)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Create a new post",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Add photo here",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (state.newPostAttachments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable(enabled = !state.isPosting) { attachmentPicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add photo",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    val firstAttachment = state.newPostAttachments.first()
                    val bitmap = remember(firstAttachment.bytes) {
                        BitmapFactory.decodeByteArray(firstAttachment.bytes, 0, firstAttachment.bytes.size)?.asImageBitmap()
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Selected photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "Photo selected",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${state.newPostAttachments.size} photo${if (state.newPostAttachments.size > 1) "s" else ""} selected",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = { onClearAttachments() },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Remove photo",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .padding(8.dp),
                                    tint = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                ThreadsTextField(
                    value = state.newPostText,
                    onValueChange = onValueChange,
                    label = "Write a description...",
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            PrimaryButton(
                text = if (state.isPosting) "Posting..." else "Create Post",
                enabled = (state.newPostText.isNotBlank() || state.newPostAttachments.isNotEmpty()) && !state.isPosting,
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            if (!state.error.isNullOrBlank()) {
                ErrorView(
                    message = state.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
