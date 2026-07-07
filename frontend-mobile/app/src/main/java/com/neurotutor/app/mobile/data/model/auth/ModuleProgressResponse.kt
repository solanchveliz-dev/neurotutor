package com.neurotutor.app.mobile.data.model.auth

import com.google.gson.annotations.SerializedName

data class ModuleProgressResponse(
    @SerializedName("module_id")
    val moduleId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("level")
    val level: String,
    @SerializedName("theory_completed")
    val theoryCompleted: Boolean,
    @SerializedName("practice_completed")
    val practiceCompleted: Boolean,
    @SerializedName("exam_passed")
    val examPassed: Boolean,
    @SerializedName("practice_completed_count")
    val practiceCompletedCount: Int,
    @SerializedName("practice_total_count")
    val practiceTotalCount: Int,
    @SerializedName("exam_best_score")
    val examBestScore: Int,
    @SerializedName("progress_percentage")
    val progressPercentage: Int,
    @SerializedName("last_activity_at")
    val lastActivityAt: String?,
    @SerializedName("completed_at")
    val completedAt: String?
)
