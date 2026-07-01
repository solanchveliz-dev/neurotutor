package com.neurotutor.app.mobile.data.model.auth

import com.neurotutor.app.mobile.data.model.learning.ModuleItem

data class StudentProgressResponse(
    val overallProgress: Double,
    val progressPercentage: Int,
    val modules: List<ModuleItem>
)
