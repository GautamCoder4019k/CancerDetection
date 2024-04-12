package com.project.cancerdetect.api

import android.net.Uri
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("showresult")
    fun uploadImage(@Part image: MultipartBody.Part): Call<ResponseBody>
}

object RetrofitClient {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://<your-server-ip>:5000/") // Replace with your actual IP and port
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}