package com.neurotutor.app.mobile.data.model.auth

data class ModuleProgressResponse(
    val moduleId: String,
    val title: String,
    val level: String,
    val theoryCompleted: Boolean,
    val practiceCompleted: Boolean,
    val examPassed: Boolean,
    val practiceCompletedCount: Int,
    val practiceTotalCount: Int,
    val examBestScore: Int,
    val progressPercentage: Int,
    val lastActivityAt: String?,
    val completedAt: String?
)
