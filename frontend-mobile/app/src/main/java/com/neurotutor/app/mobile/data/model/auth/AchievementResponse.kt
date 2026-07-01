package com.neurotutor.app.mobile.data.model.auth

data class AchievementResponse(
    val id: String,
    val title: String,
    val description: String,
    val type: String, // BASIC, INTERMEDIATE, ADVANCED
    val unlockedAt: String
)
