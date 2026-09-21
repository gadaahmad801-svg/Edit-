package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val referenceVideoUri: String?,
    val userVideoUrisJson: String, // JSON array of URI strings
    val blueprintJson: String?,
    val transferConfigJson: String,
    val transferMode: String,
    val matchStrength: String,
    val thumbnailUri: String?,
    val lastExportUri: String?,
    val durationMs: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String, // "Cinematic", "Reel / Beat", "Vintage / Film", "Action / Sport", "Vlog"
    val blueprintJson: String,
    val thumbnailResName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "export_history")
data class ExportEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val projectName: String,
    val filePath: String,
    val resolution: String,
    val fps: Int,
    val quality: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val timestamp: Long = System.currentTimeMillis()
)
