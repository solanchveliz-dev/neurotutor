package com.neurotutor.app.mobile.ui.models

data class StudentProfileResponse(
    val nombreCompleto: String,
    val gradoSeccion: String,
    val nivelActual: String,
    val puntosTotales: Int,
    val modulos: List<ModuleItem> // 🚀 NUEVO: Sincronizado con el Backend
)
