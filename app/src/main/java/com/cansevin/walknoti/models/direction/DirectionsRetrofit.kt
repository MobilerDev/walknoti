package com.cansevin.walknoti.models.direction

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DirectionsRetrofit {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://mapapi.cloud.huawei.com/mapApi/v1/")
        .client(setInterceptors())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <S> createService(serviceClass: Class<S>?): S {
        return retrofit.create(serviceClass)
    }

    private fun setInterceptors() : OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val url: HttpUrl = chain.request().url.newBuilder()
                    .addQueryParameter("key", "CV7coJwW5hLhLVTccQwNC3S/+lMos63/0ChEnYmqF2mQKkv4ap5DZvy6gX96h7P6WecZoGPDEeM5L5uEcbuiVSEnhqLo")
                    .build()
                val request = chain.request().newBuilder()
                    .header("Content-Type","application/json")
                    .url(url)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logger)
            .build()
    }
}