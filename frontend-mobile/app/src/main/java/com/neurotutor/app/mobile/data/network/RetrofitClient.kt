package com.neurotutor.app.mobile.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://neurotutor-production.up.railway.app/"
    @Volatile
    private var authToken: String? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            authToken?.takeIf { it.isNotBlank() }?.let { token ->
                requestBuilder.header("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun updateAuthToken(token: String?) {
        authToken = token?.takeIf { it.isNotBlank() }
    }

    fun clearAuthToken() {
        authToken = null
    }
}
