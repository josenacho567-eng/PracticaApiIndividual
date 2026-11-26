package com.example.pruebaapis.data.remote


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object retrofitClient {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"


    private val clienteHttp: OkHttpClient = OkHttpClient.Builder()
        // Muestra cada peticion en Logcat (filtrar por "OkHttp"): util para depurar y para clase.
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()


    val api: rickAndMortyAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(com.example.pruebaapis.data.remote.retrofitClient.clienteHttp)
            .addConverterFactory(GsonConverterFactory.create()) // JSON <-> objetos Kotlin
            .build()
            .create(rickAndMortyAPIService::class.java)
    }

}