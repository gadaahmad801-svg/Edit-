package com.example.data

import com.example.data.local.ExportEntity
import com.example.data.local.ProjectDao
import com.example.data.local.ProjectEntity
import com.example.data.local.TemplateEntity
import com.example.domain.model.EditBlueprint
import com.example.domain.model.TransferConfig
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ProjectRepository(private val dao: ProjectDao) {

    val allProjects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    val allTemplates: Flow<List<TemplateEntity>> = dao.getAllTemplates()
    val allExports: Flow<List<ExportEntity>> = dao.getAllExports()

    suspend fun getProjectById(id: String): ProjectEntity? = dao.getProjectById(id)

    suspend fun createProject(
        name: String,
        referenceVideoUri: String?,
        userVideoUris: List<String>,
        blueprint: EditBlueprint?,
        transferConfig: TransferConfig,
        thumbnailUri: String? = null,
        durationMs: Long = 15000L
    ): String {
        val id = UUID.randomUUID().toString()
        val entity = ProjectEntity(
            id = id,
            name = name,
            referenceVideoUri = referenceVideoUri,
            userVideoUrisJson = BlueprintJsonHelper.stringListToJson(userVideoUris),
            blueprintJson = blueprint?.let { BlueprintJsonHelper.blueprintToJson(it) },
            transferConfigJson = BlueprintJsonHelper.transferConfigToJson(transferConfig),
            transferMode = transferConfig.mode.name,
            matchStrength = transferConfig.matchStrength.name,
            thumbnailUri = thumbnailUri,
            lastExportUri = null,
            durationMs = durationMs,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        dao.insertProject(entity)
        return id
    }

    suspend fun updateProject(project: ProjectEntity) {
        dao.updateProject(project.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteProject(id: String) {
        dao.deleteProjectById(id)
    }

    suspend fun saveTemplate(template: TemplateEntity) {
        dao.insertTemplate(template)
    }

    suspend fun recordExport(export: ExportEntity) {
        dao.insertExport(export)
    }

    suspend fun populatePresetTemplatesIfNeeded() {
        // Will be seeded if templates are empty
    }
}
