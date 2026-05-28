package com.example.barbershop.ui.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarBack(modifier: Modifier = Modifier, nav: () -> Unit, title: String? = null, content: @Composable (() -> Unit)? = null) {
    TopAppBar(
        title = { Text( text = title ?: "", color = MaterialTheme.colorScheme.onBackground) },
        navigationIcon = {
            IconButton(onClick = { nav() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    modifier = modifier
                )
            }
        },
        actions = {
            content?.invoke()
        }
//                windowInsets = WindowInsets(
//                    top = dimensionResource(id = R.dimen.size_0dp),
//                    bottom = dimensionResource(id = R.dimen.size_0dp)
//                )
    )
}