package com.example.barbershop.data.network.response

data class FamilyResponse(
    val id: Int,
    val name: String,
    val createdAt: Long
)

data class FamilyRequest(
    val name: String
)