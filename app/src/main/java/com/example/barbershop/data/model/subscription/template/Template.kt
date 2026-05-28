package com.example.barbershop.data.model.subscription.template

data class Template(
    val id: Int,
    val name: String,
    val iconSource: String,
    val categoryId: Int,
    val cancelUrl: String?,
    val isPopular: Boolean
)
