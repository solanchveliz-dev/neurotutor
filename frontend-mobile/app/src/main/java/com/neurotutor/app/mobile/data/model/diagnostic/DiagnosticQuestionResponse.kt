package com.neurotutor.app.mobile.data.model.diagnostic

import com.google.gson.annotations.SerializedName

data class DiagnosticQuestionResponse(
    val id: Long,
    @SerializedName("text_before_image")
    val textBeforeImage: String?,
    @SerializedName("text_after_image")
    val textAfterImage: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    val options: List<String>,
    val topic: String?,
    val order: Int
)
