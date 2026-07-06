package com.neurotutor.app.mobile.data.model.auth

import com.google.gson.annotations.SerializedName

data class AchievementResponse(
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val icon: String,
    val category: String,
    @SerializedName("points_required")
    val pointsRequired: Int?,
    @SerializedName("unlocked_at")
    val unlockedAt: String?
)

data class StudentAchievementsResponse(
    val unlocked: List<AchievementResponse>,
    val locked: List<AchievementResponse>
)
