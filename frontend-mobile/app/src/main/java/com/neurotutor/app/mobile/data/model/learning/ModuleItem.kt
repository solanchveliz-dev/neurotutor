package com.neurotutor.app.mobile.data.model.learning

import com.google.gson.annotations.SerializedName

data class ModuleItem(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("titulo")
    val titulo: String,
    
    @SerializedName(value = "tema_nombre", alternate = ["temaNombre"])
    val temaNombre: String,
    
    @SerializedName(value = "nivel_requerido", alternate = ["nivelRequerido"])
    val nivelRequerido: String,
    
    @SerializedName(value = "ejercicios_completados", alternate = ["ejerciciosCompletados"])
    val ejerciciosCompletados: Int,
    
    @SerializedName(value = "ejercicios_totales", alternate = ["ejerciciosTotales"])
    val ejerciciosTotales: Int,
    
    @SerializedName("estado")
    val estado: ModuleStatus,
    
    @SerializedName("progress_percentage")
    val progressPercentage: Int = 0,
    
    @SerializedName("theory_completed")
    val theoryCompleted: Boolean = false,
    
    @SerializedName("practice_completed")
    val practiceCompleted: Boolean = false,
    
    @SerializedName("exam_passed")
    val examPassed: Boolean = false
)
