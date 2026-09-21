package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TemplateEntity
import com.example.ui.components.BadgeChip
import com.example.ui.components.StudioButton
import com.example.ui.components.StudioCard
import com.example.ui.components.StudioTopBar
import com.example.ui.theme.*

@Composable
fun TemplatesScreen(
    templates: List<TemplateEntity>,
    onSelectTemplate: (TemplateEntity) -> Unit,
    onBack: () -> Unit
) {
    val categories = listOf("All", "Cinematic", "Reel / Beat", "Vintage / Film", "Action / Sport", "Custom")
    var selectedCategory by remember { mutableStateOf("All") }

    val filtered = remember(templates, selectedCategory) {
        if (selectedCategory == "All") templates
        else templates.filter { it.category == selectedCategory }
    }

    Scaffold(
        containerColor = StudioBackground,
        topBar = {
            StudioTopBar(
                title = "Reusable Edit Blueprints",
                subtitle = "Pre-analyzed motion and aesthetic blueprints",
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // CATEGORY FILTER CHIPS
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSel = selectedCategory == cat
                        Surface(
                            color = if (isSel) StudioCyan.copy(alpha = 0.2f) else StudioSurface,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) StudioCyan else StudioBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) StudioCyan else StudioTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // TEMPLATE CARDS
            items(filtered) { template ->
                StudioCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSelectTemplate(template) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BadgeChip(text = template.category, color = StudioAmber)
                        Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = template.name,
                        color = StudioTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = template.description,
                        color = StudioTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    StudioButton(
                        text = "Use Blueprint on My Footage",
                        onClick = { onSelectTemplate(template) },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_use_template_${template.id}"
                    )
                }
            }
        }
    }
}
