package com.example.barbershop.data.repository

import com.example.barbershop.data.network.ApiService
import com.example.barbershop.data.network.response.SecurityMessageResponse
import com.example.barbershop.data.network.response.SecurityResponse
import retrofit2.Response

class SecurityRepository(
    private val apiService: ApiService
) {
    suspend fun getSession(): Response<SecurityResponse>{
        return apiService.getSession()
    }

    suspend fun deleteSession(id: String): Response<SecurityMessageResponse>{
        return apiService.deleteSession(id)
    }

    suspend fun deleteAllSession(): Response<SecurityMessageResponse>{
        return apiService.deleteAllSession()
    }
}