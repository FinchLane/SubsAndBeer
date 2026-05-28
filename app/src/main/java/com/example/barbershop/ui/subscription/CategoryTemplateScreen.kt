package com.example.barbershop.ui.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.viewmodel.subscription.SubViewModel

@Composable
fun CategoryTemplateScreen(
    subViewModel: SubViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    categoryId: Int? = null
) {
    val name = subViewModel.categoriesTemplate.first {it.id == categoryId}.name
    val templates = subViewModel.templates.filter { it.categoryId == categoryId }

    Scaffold(
        topBar = { AppBarBack(nav = {navController.popBackStack()}, title = name) }
    ) { innerPadding ->

        Box(
            modifier = modifier.padding(innerPadding)
        ){
            Column {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(templates){ index, item ->
                        val isFirst = index == 0
                        val isLast = index == templates.lastIndex
                        TemplateItem(
                            image = item.iconSource,
                            name = item.name,
                            onClick = {
                                navController.navigate("subWithTemplate/${item.id}")
                            },
                            isFirst = isFirst,
                            isLast = isLast
                        )
                    }
                }
            }
        }
    }
}