package com.example.wmc_wewatch.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://www.omdbapi.com/"
    private const val API_KEY = "e79ec589"

    // Добавим логирующий интерсептор
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // Логируем всё тело ответа
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiInterceptor(API_KEY))
        .addInterceptor(loggingInterceptor)  // Добавляем логирование
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: OmdbApiService = retrofit.create(OmdbApiService::class.java)


}