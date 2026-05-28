package com.example.barbershop.data.repository

import com.example.barbershop.data.network.ApiService
import com.example.barbershop.data.network.response.AuthRequest
import com.example.barbershop.data.network.response.AuthResponse
import com.example.barbershop.data.network.response.VerifyCodeRequest
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun sendSms(phoneNumber: String, ipAddress: String): Response<AuthResponse> {
        return apiService.sendSms(AuthRequest(phoneNumber, ipAddress))
    }

    suspend fun verifyCode(phoneNumber: String, code: String, deviceName: String, appVersion: String, ipAddress: String): Response<AuthResponse> {
        return apiService.verifyCode(VerifyCodeRequest(phoneNumber, code, deviceName, appVersion, ipAddress))
    }
}