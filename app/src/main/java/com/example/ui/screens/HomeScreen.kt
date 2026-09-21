package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectEntity
import com.example.data.local.TemplateEntity
import com.example.domain.model.TransferMode
import com.example.ui.components.BadgeChip
import com.example.ui.components.StudioButton
import com.example.ui.components.StudioCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun HomeScreen(
    projects: List<ProjectEntity>,
    templates: List<TemplateEntity>,
    onStartNewEdit: () -> Unit,
    onSelectMode: (TransferMode) -> Unit,
    onSelectTemplate: (TemplateEntity) -> Unit,
    onSelectProject: (ProjectEntity) -> Unit,
    onDeleteProject: (String) -> Unit,
    onOpenTemplates: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Scaffold(
        containerColor = StudioBackground,
        topBar = {
            Surface(
                color = StudioBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AHMED EDITS",
                                color = StudioTextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BadgeChip(text = "STUDIO PRO", color = StudioCyan)
                        }
                        Text(
                            text = "AI Video Edit Transfer & Motion Effect Recreation",
                            color = StudioTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onOpenTemplates,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(StudioSurfaceVariant)
                                .testTag("home_templates_button")
                        ) {
                            Icon(imageVector = Icons.Default.GridView, contentDescription = "Templates", tint = StudioCyan)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(StudioSurfaceVariant)
                                .testTag("home_settings_button")
                        ) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = StudioTextSecondary)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // HERO CTA BANNER
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF005662),
                                    Color(0xFF0A2B35),
                                    Color(0xFF131722)
                                )
                            )
                        )
                        .border(1.dp, StudioCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { onStartNewEdit() }
                        .padding(22.dp)
                        .testTag("hero_create_edit_card")
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BadgeChip(text = "FLAGSHIP WORKFLOW", color = StudioAmber)
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = StudioCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Create Edit From Video",
                            color = StudioTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Upload any video you love. Ahmed Edits decomposes its cuts, motion, camera shake, zoom ramps, transitions, and color grade, adapting them onto your clips.",
                            color = StudioTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StudioButton(
                            text = "Select Reference Video",
                            icon = Icons.Default.UploadFile,
                            onClick = onStartNewEdit,
                            testTag = "hero_select_reference_button"
                        )
                    }
                }
            }

            // DEDICATED QUICK ACTION MODES
            item {
                Text(
                    text = "DEDICATED MODES",
                    color = StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickModeCard(
                        title = "Filter Only",
                        subtitle = "Color Grade",
                        icon = Icons.Default.ColorLens,
                        color = StudioAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectMode(TransferMode.FILTER_ONLY) }
                    )
                    QuickModeCard(
                        title = "Motion Only",
                        subtitle = "Zooms & Pans",
                        icon = Icons.Default.ZoomIn,
                        color = StudioCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectMode(TransferMode.MOTION_ONLY) }
                    )
                    QuickModeCard(
                        title = "Transitions",
                        subtitle = "Flashes & Cuts",
                        icon = Icons.Default.Transform,
                        color = StudioPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectMode(TransferMode.TRANSITIONS_ONLY) }
                    )
                }
            }

            // REUSABLE EDIT TEMPLATES SECTION
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REUSABLE BLUEPRINTS",
                        color = StudioCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "View All (${templates.size})",
                        color = StudioTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onOpenTemplates() }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(templates) { template ->
                        TemplateCard(
                            template = template,
                            onClick = { onSelectTemplate(template) }
                        )
                    }
                }
            }

            // RECENT PROJECTS SECTION
            item {
                Text(
                    text = "RECENT PROJECTS",
                    color = StudioCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (projects.isEmpty()) {
                item {
                    StudioCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MovieFilter,
                                contentDescription = null,
                                tint = StudioTextTertiary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Projects Yet",
                                color = StudioTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap 'Select Reference Video' to recreate your first video edit style.",
                                color = StudioTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(projects) { project ->
                    ProjectItemCard(
                        project = project,
                        onClick = { onSelectProject(project) },
                        onDelete = { onDeleteProject(project.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = StudioSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = StudioTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = StudioTextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun TemplateCard(
    template: TemplateEntity,
    onClick: () -> Unit
) {
    Surface(
        color = StudioSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BadgeChip(text = template.category, color = StudioAmber)
                Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = template.name,
                color = StudioTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = template.description,
                color = StudioTextSecondary,
                fontSize = 11.sp,
                maxLines = 2,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun ProjectItemCard(
    project: ProjectEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    StudioCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E2838)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = StudioCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    color = StudioTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BadgeChip(text = project.transferMode, color = StudioCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${project.durationMs / 1000}s duration",
                        color = StudioTextTertiary,
                        fontSize = 11.sp
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = StudioTextTertiary)
            }
        }
    }
}
