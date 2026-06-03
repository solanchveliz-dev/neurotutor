package com.neurotutor.app.mobile.ui.models

data class ModuleItem(
    val id: String,
    val titulo: String,
    val ejerciciosCompletados: Int,
    val ejerciciosTotales: Int,
    val estado: ModuleStatus
)