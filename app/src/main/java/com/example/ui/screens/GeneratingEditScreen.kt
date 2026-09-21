package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BadgeChip
import com.example.ui.theme.*

@Composable
fun GeneratingEditScreen(
    progressPercent: Int,
    statusMessage: String
) {
    Scaffold(
        containerColor = StudioBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BadgeChip(text = "SUBJECT-RELATIVE ADAPTATION", color = StudioCyan)

                Spacer(modifier = Modifier.height(20.dp))

                CircularProgressIndicator(
                    progress = { progressPercent / 100f },
                    color = StudioCyan,
                    trackColor = StudioSurfaceVariant,
                    strokeWidth = 6.dp,
                    modifier = Modifier
                        .size(80.dp)
                        .testTag("generating_circular_progress")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "$progressPercent%",
                    color = StudioTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusMessage,
                    color = StudioTextSecondary,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                LinearProgressIndicator(
                    progress = { progressPercent / 100f },
                    color = StudioCyan,
                    trackColor = StudioSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }
    }
}
