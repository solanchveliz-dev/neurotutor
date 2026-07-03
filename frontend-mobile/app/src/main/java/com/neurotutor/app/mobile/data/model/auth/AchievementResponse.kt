package com.neurotutor.app.mobile.data.model.auth

data class AchievementResponse(
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val icon: String,
    val category: String,
    val pointsRequired: Int,
    val unlockedAt: String?
)
