package com.neurotutor.app.mobile.data.model.auth

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("emailSent") val emailSent: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("debugToken") val debugToken: String? = null
)
