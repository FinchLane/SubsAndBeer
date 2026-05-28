package com.example.barbershop.ui.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barbershop.Constants.BASE_URl
import com.example.barbershop.ui.components.customComponent.AdaptiveIcon
import com.example.barbershop.ui.components.navigation.AppBarBack
import com.example.barbershop.viewmodel.subscription.SubViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSubScreen(
    subViewModel: SubViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navigationEvent by subViewModel.navigateTo.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            "newSub" -> {
                navController.navigate("newSub")
                subViewModel.clearNavigationEvent()
            }
            "addSub" -> {
                navController.navigate("addSub")
                subViewModel.clearNavigationEvent()
            }

            else -> Unit
        }
    }

    val titles = listOf("Популярные", "Категории")
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { AppBarBack(nav = {navController.popBackStack()}, title = "Добавить подписку") }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                titles.forEachIndexed{index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> PopularTemplates(
                        subViewModel,
                        navController
                    )
                    1 -> CategoryTemplates(
                        subViewModel,
                        navController
                    )
                }
            }

            Button(
                onClick = {navController.navigate("addSub")},
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Создать подпсику",
                )
            }
        }
    }
}

@Composable
fun PopularTemplates(
    subViewModel: SubViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val templates = subViewModel.templates.filter { it.isPopular }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
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

@Composable
fun CategoryTemplates(
    subViewModel: SubViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val categoryTemplate = subViewModel.categoriesTemplate

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        itemsIndexed(categoryTemplate) { index, item ->
            val isFirst = index == 0
            val isLast = index == categoryTemplate.lastIndex

            CategoryTemplateItem(
                nameCategory = item.name,
                countCategory = subViewModel.templates.count { it.categoryId == item.id },
                onClick = { navController.navigate("categoryTemplate/${item.id}") },
                isFirst = isFirst,
                isLast = isLast
            )

            if (!isLast) {
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
fun TemplateItem(
    image: String,
    name: String,
    onClick: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 12.dp
    val shape = when {
        isFirst && isLast -> RoundedCornerShape(cornerRadius)
        isFirst -> RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
        isLast -> RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
        else -> RectangleShape
    }

    Surface(
        onClick = { onClick() },
        shape = shape,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                AdaptiveIcon(
                    imageSrc = BASE_URl + image,
                    modifier = Modifier.size(48.dp),
                    containerShape = RoundedCornerShape(16.dp),
                    containerSize = 48.dp,
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = onClick
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryTemplateItem(
    nameCategory: String,
    countCategory: Int,
    onClick: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 12.dp
    val shape = when {
        isFirst && isLast -> RoundedCornerShape(cornerRadius)
        isFirst -> RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
        isLast -> RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
        else -> RectangleShape
    }

    Surface(
        shape = shape,
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = nameCategory,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Row {
                Text(
                    text = countCategory.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }
        }
    }
}