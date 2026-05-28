package com.example.barbershop.ui.components.navigation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.barbershop.Constants
import com.example.barbershop.R
import com.example.barbershop.ui.theme.BarbershopTheme

@Composable
fun CustomBottomNavigation(
    items: List<BottomNavItem>,
    selectedRoute: String?,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.Transparent)
    ) {
        Surface(
            shape = RoundedCornerShape(90.dp),
            color = Color.Black.copy(alpha = 0.6f),
            contentColor = Color.White,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .navigationBarsPadding()
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                windowInsets = WindowInsets(
                    top = dimensionResource(id = R.dimen.size_0dp),
                    bottom = dimensionResource(id = R.dimen.size_0dp)
                ),
                modifier = Modifier.navigationBarsPadding()
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = selectedRoute == item.route,
                        onClick = { onItemSelected(item.route) },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(if (selectedRoute == item.route) 56.dp else 48.dp)
                                    .background(
                                        if (selectedRoute == item.route) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (selectedRoute == item.route) Color.Black else Color.White
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BottomNavigationPreview() {
    BarbershopTheme {
        CustomBottomNavigation(
            Constants.BottomNavItems,
            { Constants.BottomNavItems.first().route}.toString(), {})
    }
}