package com.neurotutor.app.mobile.data.model.auth

data class StudentProgressResponse(
    val studentId: String,
    val overallProgress: Int,
    val points: Int,
    val streakDays: Int,
    val modules: List<ModuleProgressResponse>
)
