package com.example.barbershop.data.model.subscription.template

data class TemplatePlans(
    val id: Int,
    val templateId: Int,
    val name: String,
    val amount: Double,
    var interval: Int = 1,
    var period: String
)
