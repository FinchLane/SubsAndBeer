package com.example.barbershop.ui.subscription.category

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.viewmodel.subscription.SubViewModel

@Composable
fun CategoryScreen(
    navController: NavController,
    viewModel: SubViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val categories = viewModel.categories

    Scaffold(
        topBar = { AppBarBack(nav = {navController.popBackStack()}, title = "Категории") },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            if (categories.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Нет категорий",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories){ category ->
                        CategoryItem(
                            title = category.name,
                            countSub = viewModel.getSubscriptionsByCategory(category.id).size,
                            onClick = {
                                val previousRoute = navController.previousBackStackEntry?.destination?.route

                                if (previousRoute == "addSub" ||
                                    previousRoute?.startsWith("subWithTemplate/") == true ||
                                    previousRoute?.startsWith("editSub/") == true
                                ) {
                                    navController.popBackStack()
                                    viewModel.updateSelectedCategory(category.id)
                                }
                            },
                            onDeleteClick = {viewModel.removeCategory(category.id)}
                        )
                    }
                }
            }
            Button(
                onClick = {
                    viewModel.openCategoryDialog()
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 64.dp, end = 16.dp)
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
    }

    if (viewModel.uiState.showDialogCategory) {
        AddCategoryDialog(
            value = viewModel.uiState.nameCategory,
            onValueChange = {viewModel.updateCategoryName(it)},
            onClick = {
                viewModel.saveCategory(viewModel.uiState.nameCategory)
                viewModel.closeDialogCategory()
            },
            onDismiss = {viewModel.closeDialogCategory()}
        )
    }
}

@Composable
fun CategoryItem(
    title: String,
    countSub: Int,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Dehaze,
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = when (countSub){
                        0 -> "Нет подписок"
                        1 -> "1 подписка"
                        2 -> "2 подписки"
                        3 -> "3 подписки"
                        4 -> "4 подписки"
                        else -> "$countSub подписок"
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                tint = Color(0xFF412a2b),
                modifier = Modifier.padding(horizontal = 16.dp).clickable { onDeleteClick() }
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CategoryItemPreview() {
    BarbershopTheme {
        CategoryItem("Развалечения", 2, {}, {})
    }
}