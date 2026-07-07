package com.neurotutor.app.mobile.data.model.auth

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    val name: String,
    val grade: String,
    val section: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    val gender: String
)
