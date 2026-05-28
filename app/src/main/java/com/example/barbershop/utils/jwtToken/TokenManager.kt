package com.example.barbershop.utils.jwtToken

interface TokenManager {
    fun saveToken(token: String)
    fun getToken(): String?
    fun deleteToken()
}