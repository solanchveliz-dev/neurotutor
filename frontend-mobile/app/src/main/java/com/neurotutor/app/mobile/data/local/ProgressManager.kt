package com.neurotutor.app.mobile.data.local

import android.content.Context
import android.content.SharedPreferences

class ProgressManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("neurotutor_progress", Context.MODE_PRIVATE)

    fun saveExerciseCompleted(studentId: String, moduleId: String, exerciseId: String) {
        val key = "${studentId}_${moduleId}_completed"
        val completedSet = prefs.getStringSet(key, emptySet())?.toMutableSet() ?: mutableSetOf()
        completedSet.add(exerciseId)
        prefs.edit().putStringSet(key, completedSet).apply()
    }

    fun getCompletedExercisesCount(studentId: String, moduleId: String): Int {
        val key = "${studentId}_${moduleId}_completed"
        return prefs.getStringSet(key, emptySet())?.size ?: 0
    }

    // --- RECOMPENSAS ---

    fun isRewardClaimed(studentId: String, moduleId: String, level: String): Boolean {
        val key = "reward_${studentId}_${moduleId}_${level}_claimed"
        return prefs.getBoolean(key, false)
    }

    fun markRewardAsClaimed(studentId: String, moduleId: String, level: String) {
        val key = "reward_${studentId}_${moduleId}_${level}_claimed"
        prefs.edit().putBoolean(key, true).apply()
    }

    // --- LIMPIEZA ---

    fun clearAllProgressForStudent(studentId: String) {
        val keyPrefix = "${studentId}_"
        val allKeys = prefs.all.keys
        allKeys.filter { it.startsWith(keyPrefix) || it.contains("_$studentId") }.forEach { key ->
            prefs.edit().remove(key).apply()
        }
        println("Progreso limpiado para estudiante: $studentId")
    }

    fun clearSpecificModule(studentId: String, moduleId: String) {
        val key = "${studentId}_${moduleId}_completed"
        prefs.edit().remove(key).apply()
        println("✅ Progreso limpiado para módulo $moduleId")
    }

    fun resetAllProgress() {
        prefs.edit().clear().apply()
        println("✅ TODOS los datos de progreso han sido eliminados")
    }
}