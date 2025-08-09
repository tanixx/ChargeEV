package com.example.chargeev

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/")
    suspend fun getAllData(): ApiResponse
}
