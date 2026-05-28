package com.example.barbershop.data.network.response

data class AuthRequest(val phoneNumber: String, val ipAddress: String)

data class AuthResponse(
    val message: String,
    val accessToken: String? = null,
    val status: String? = null,
    val data: Any? = null
)

data class VerifyCodeRequest(
    val phoneNumber: String,
    val code: String,
    val deviceName: String,
    val appVersion: String,
    val ipAddress: String
)