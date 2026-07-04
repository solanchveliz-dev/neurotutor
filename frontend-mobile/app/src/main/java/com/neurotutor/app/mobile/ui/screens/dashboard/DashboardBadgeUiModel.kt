package com.neurotutor.app.mobile.ui.screens.dashboard

data class DashboardBadgeUiModel(
    val id: String,
    val moduleId: String,
    val topic: String,
    val level: String,
    val levelTag: String,
    val badgeRes: Int,
    val completedAt: String?
)
