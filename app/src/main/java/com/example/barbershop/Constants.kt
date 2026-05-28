package com.example.barbershop

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChartOutlined
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.unit.dp
import com.example.barbershop.ui.components.navigation.BottomNavItem

object Constants {
    val BASE_URl = "http://192.168.1.142:8080/"
    //val BASE_URl = "http://77.239.115.100:5000/"

    /** Маршруты для навигации на главном экране */

    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Home",
            icon = Icons.Filled.CreditCard,
            route = "home"
        ),
        BottomNavItem(
            label = "Analytics",
            icon = Icons.Filled.InsertChartOutlined,
            route = "analytics"
        ),
        BottomNavItem(
            label = "Calendar",
            icon = Icons.Filled.CalendarMonth,
            route = "calendar"
        ),
        BottomNavItem(
            label = "Profile",
            icon = Icons.Filled.Person,
            route = "profile"
        )
    )

    /** Отступы по умолчанию */

    val mainPadding = 16.dp
}