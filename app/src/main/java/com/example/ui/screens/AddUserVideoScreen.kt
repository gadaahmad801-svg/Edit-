package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BadgeChip
import com.example.ui.components.StudioButton
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioTopBar
import com.example.ui.theme.*

@Composable
fun AddUserVideoScreen(
    userVideoUris: List<String>,
    onAddVideo: (String) -> Unit,
    onRemoveVideo: (Int) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val multiPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            onAddVideo(uri.toString())
        }
    }

    Scaffold(
        containerColor = StudioBackground,
        topBar = {
            StudioTopBar(
                title = "Add Your Video Footage",
                subtitle = "Select one or multiple clips to receive the edit style",
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(
                color = StudioBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                StudioButton(
                    text = "Continue to Transfer Settings",
                    icon = Icons.Default.Tune,
                    onClick = onContinue,
                    enabled = userVideoUris.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_continue_to_transfer_settings"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // SOURCE PICKER BUTTONS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SourceButton(
                        label = "Select Clips",
                        icon = Icons.Default.AddPhotoAlternate,
                        modifier = Modifier.weight(1f),
                        onClick = { multiPickerLauncher.launch("video/*") },
                        testTag = "btn_pick_user_clips"
                    )
                    SourceButton(
                        label = "Add Sample Clips",
                        icon = Icons.AutoMirrored.Filled.PlaylistPlay,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onAddVideo("sample_user_clip_1")
                            onAddVideo("sample_user_clip_2")
                        },
                        testTag = "btn_add_sample_user_clips"
                    )
                }
            }

            // FOOTAGE SUMMARY
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECTED FOOTAGE (${userVideoUris.size} CLIPS)",
                        color = StudioCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    if (userVideoUris.isNotEmpty()) {
                        BadgeChip(text = "${userVideoUris.size * 5}s estimated", color = StudioGreen)
                    }
                }
            }

            if (userVideoUris.isEmpty()) {
                item {
                    StudioCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.VideoCall, contentDescription = null, tint = StudioTextTertiary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "No Clips Added Yet", color = StudioTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Add one or more clips from your device or use sample clips.", color = StudioTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                itemsIndexed(userVideoUris) { index, uri ->
                    StudioCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E2838)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "#${index + 1}", color = StudioCyan, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (uri.startsWith("sample_")) "Sample Footage Clip ${index + 1}" else "Clip ${index + 1}",
                                    color = StudioTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ready for subject tracking & transfer",
                                    color = StudioTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            IconButton(onClick = { onRemoveVideo(index) }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = StudioTextTertiary)
                            }
                        }
                    }
                }
            }
        }
    }
}
