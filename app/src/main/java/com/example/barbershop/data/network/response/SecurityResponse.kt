package com.example.barbershop.data.network.response


data class SecurityResponse(
    val sessions : List<UserSession>
)

data class UserSession(
    val id: String,
    val deviceName: String,
    val appVersion: String,
    val ipAddress: String,
    val startedAt: String,
    val expiresAt: String,
    val isCurrent: Boolean
)

data class SecurityMessageResponse(val message: String)