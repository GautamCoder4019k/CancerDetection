package com.project.cancerdetect.api

import com.project.cancerdetect.model.CancerResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("result")
    fun uploadImage(@Part image: MultipartBody.Part): Call<CancerResponse>
}

object RetrofitClient {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.134.64:5000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}