package com.example.barbershop.data.network.response

import com.example.barbershop.data.model.subscription.template.CategoryTemplate
import com.example.barbershop.data.model.subscription.template.Template
import com.example.barbershop.data.model.subscription.template.TemplatePlans

data class TemplatesResponse(
    val categories: List<CategoryTemplate>,
    val templates: List<Template>,
    val plans: List<TemplatePlans>
)
