package com.neurotutor.app.mobile.data.model.auth

import com.neurotutor.app.mobile.data.model.learning.ModuleItem

data class StudentProfileResponse(
    val nombreCompleto: String,
    val gradoSeccion: String,
    val nivelActual: String,
    val puntosTotales: Int,
    val modulos: List<ModuleItem>
)
