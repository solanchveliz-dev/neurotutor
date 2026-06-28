package com.neurotutor.app.mobile.ui.screens.learning

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LevelSectionsViewModelFactory(
    private val context: Context,
    private val studentId: String,
    private val moduleId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LevelSectionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LevelSectionsViewModel(context, studentId, moduleId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}