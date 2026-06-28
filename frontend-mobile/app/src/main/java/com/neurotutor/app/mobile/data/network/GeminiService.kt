package com.neurotutor.app.mobile.data.network

import android.util.Log
import com.neurotutor.app.mobile.data.model.learning.PreguntaPractica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // ✅ ACTUALIZADO: Ahora acepta un parámetro opcional para forzar la respuesta tipo JSON
    private suspend fun callGemini(prompt: String, forzarJson: Boolean = false): String {
        return withContext(Dispatchers.IO) {
            val url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash-latest:generateContent?key=${GeminiConfig.API_KEY}"

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })

                // ✅ CONFIGURACIÓN CLAVE: Le dice a los servidores de Google que RECHACEN texto plano
                if (forzarJson) {
                    put("generationConfig", JSONObject().apply {
                        put("responseMimeType", "application/json")
                    })
                }
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) throw Exception("HTTP ${response.code}: $responseBody")

            val jsonResponse = JSONObject(responseBody)
            jsonResponse.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
        }
    }

    suspend fun explicarError(pregunta: String, respuestaUsuario: String, respuestaCorrecta: String, explicacionOriginal: String): String {
        return try {
            val prompt = "El estudiante falló. Pregunta: $pregunta. Respondió: $respuestaUsuario. Correcta: $respuestaCorrecta. Explica por qué falló (2 oraciones, motivador, emojis)."
            callGemini(prompt)
        } catch (_: Exception) {
            "¡Casi! La respuesta correcta era $respuestaCorrecta. 💪"
        }
    }

    suspend fun explicarConcepto(pregunta: String, explicacionOriginal: String): String {
        return try {
            val prompt = "Explica esto para un niño: $pregunta. Usa ejemplos de pizza. Máximo 3 oraciones."
            callGemini(prompt)
        } catch (_: Exception) {
            "📚 $explicacionOriginal"
        }
    }

    suspend fun generarExplicacion(pregunta: String, opciones: List<String>, respuestaCorrecta: String): String {
        return try {
            val prompt = "Explica el concept de esta pregunta para un niño: $pregunta. No des la respuesta."
            callGemini(prompt)
        } catch (_: Exception) {
            "Vamos a repasar el concepto. Una fracción es una parte de un todo. 🍕"
        }
    }

    suspend fun generarPista(pregunta: String, opciones: List<String>): String {
        return try {
            val prompt = "Da una pista corta (1 oración) para: $pregunta. No des la respuesta."
            callGemini(prompt)
        } catch (_: Exception) {
            "💡 Revisa bien los números."
        }
    }

    suspend fun generarPreguntasPractica(preguntaOriginal: String, tema: String): List<PreguntaPractica> {
        return try {
            val prompt = """
                You are a strict JSON generator for educational apps.
                Generate exactly 2 multiple-choice practice questions for a 6th-grade student based on this question: "$preguntaOriginal" and this topic: "$tema".
                Return ONLY a raw JSON array.
                Format:
                [
                  {
                    "pregunta": "Texto",
                    "opciones": ["1", "2", "3", "4"],
                    "respuestaCorrecta": 0
                  }
                ]
            """.trimIndent()

            // ✅ Pasamos true para obligar el modo JSON estricto
            val responseText = callGemini(prompt, forzarJson = true)

            // 🚨 LOG DE INSPECCIÓN: Imprime la respuesta exacta en rojo para auditarla
            Log.e("DEBUG_GEMINI", "🤖 TEXTO RECIBIDO DE GEMINI:\n$responseText")

            extraerYParsearJSON(responseText)
        } catch (e: Exception) {
            Log.e("DEBUG_GEMINI", "❌ ¡ERROR DETECTADO EN LA PETICIÓN!: ${e.message}", e)
            obtenerPreguntasEmergencia()
        }
    }

    private fun extraerYParsearJSON(texto: String): List<PreguntaPractica> {
        return try {
            var jsonLimpio = texto.trim()

            if (jsonLimpio.startsWith("```")) {
                jsonLimpio = jsonLimpio.removePrefix("```json").removePrefix("```").trim()
            }
            if (jsonLimpio.endsWith("```")) {
                jsonLimpio = jsonLimpio.removeSuffix("```").trim()
            }

            val inicioJson = jsonLimpio.indexOf("[")
            val finJson = jsonLimpio.lastIndexOf("]")
            if (inicioJson != -1 && finJson != -1 && finJson > inicioJson) {
                jsonLimpio = jsonLimpio.substring(inicioJson, finJson + 1)
            }

            val jsonArray = JSONArray(jsonLimpio)
            val preguntas = mutableListOf<PreguntaPractica>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val pregunta = obj.getString("pregunta")
                val opcionesArray = obj.getJSONArray("opciones")
                val opciones = mutableListOf<String>()
                for (j in 0 until opcionesArray.length()) {
                    opciones.add(opcionesArray.getString(j))
                }
                val respuestaCorrecta = obj.getInt("respuestaCorrecta")
                preguntas.add(PreguntaPractica(pregunta, opciones, respuestaCorrecta))
            }
            preguntas
        } catch (e: Exception) {
            // 🚨 Si el parseo falla, este log nos dirá la razón exacta en la pestaña Problems/Logcat
            Log.e("DEBUG_GEMINI", "❌ Error al parsear el JSON recibido", e)
            obtenerPreguntasEmergencia()
        }
    }

    private fun obtenerPreguntasEmergencia(): List<PreguntaPractica> {
        return listOf(
            PreguntaPractica(
                pregunta = "¡Ups! Tuvimos un problema al cargar los ejercicios de la nube. ¿Seguimos practicando? 🍕",
                opciones = listOf("¡Sí, claro!", "Intentar otra vez", "Ver teoría", "Salir"),
                respuestaCorrecta = 0
            )
        )
    }
}