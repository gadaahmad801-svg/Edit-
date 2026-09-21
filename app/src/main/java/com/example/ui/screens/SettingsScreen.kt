package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppLanguage
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    useGeminiCloud: Boolean,
    geminiStatusMessage: String,
    currentLanguage: AppLanguage,
    onToggleGemini: (Boolean) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onClearCache: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = StudioBackground,
        topBar = {
            StudioTopBar(
                title = "Studio Settings",
                subtitle = "Engine providers, language, and storage",
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // AI PROVIDER CONFIGURATION
            item {
                Text(
                    text = "AI ANALYSIS & DECONSTRUCTION ENGINE",
                    color = StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Gemini AI Cloud Analysis", color = StudioTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Deep aesthetic reasoning & cinematic style advice", color = StudioTextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = useGeminiCloud,
                            onCheckedChange = onToggleGemini,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = StudioCyan,
                                checkedTrackColor = StudioCyan.copy(alpha = 0.3f),
                                uncheckedThumbColor = StudioTextTertiary,
                                uncheckedTrackColor = StudioSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    BadgeChip(text = geminiStatusMessage, color = if (useGeminiCloud) StudioCyan else StudioAmber)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Note: Ahmed Edits includes an on-device local analysis engine for 100% offline frame histograms, cut detection, subject trajectory mapping, and keyframing.",
                        color = StudioTextTertiary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            // LANGUAGE SETTINGS
            item {
                Text(
                    text = "LANGUAGE / भाषा",
                    color = StudioAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    AppLanguage.values().forEach { lang ->
                        val isSel = currentLanguage == lang
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSel,
                                onClick = { onSelectLanguage(lang) },
                                colors = RadioButtonDefaults.colors(selectedColor = StudioCyan)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = lang.displayName,
                                color = if (isSel) StudioTextPrimary else StudioTextSecondary,
                                fontSize = 14.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // STORAGE & CACHE
            item {
                Text(
                    text = "STORAGE & PERFORMANCE",
                    color = StudioPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Clear Proxy Frame Cache", color = StudioTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Frees temporary frames & sampled bitmaps", color = StudioTextTertiary, fontSize = 11.sp)
                        }
                        StudioOutlinedButton(
                            text = "Clear",
                            icon = Icons.Default.CleaningServices,
                            onClick = onClearCache,
                            color = StudioCyan,
                            testTag = "btn_clear_cache"
                        )
                    }
                }
            }

            // APP INFO
            item {
                StudioCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "AHMED EDITS STUDIO PRO", color = StudioCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Version 1.0.0 • Production Build", color = StudioTextSecondary, fontSize = 12.sp)
                    Text(text = "Engine: Android Compose Native Media Pipeline", color = StudioTextTertiary, fontSize = 11.sp)
                }
            }
        }
    }
}
