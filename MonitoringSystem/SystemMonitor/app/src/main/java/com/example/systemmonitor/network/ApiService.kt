package com.example.systemmonitor.network

import com.example.systemmonitor.data.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    
    @GET("api/data")
    fun getAllData(): Call<ApiResponse>
    
    @GET("api/data")
    fun getDataByType(@Query("type") type: String): Call<ApiResponse>
    
    // Convenience Methoden
    fun getLocationData(): Call<ApiResponse> = getDataByType("location")
    fun getMessageData(): Call<ApiResponse> = getDataByType("sms")
}