package com.example.barbershop.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.barbershop.ui.components.navigation.AppNavGraph
import com.example.barbershop.Constants
import com.example.barbershop.ui.components.navigation.CustomBottomNavigation

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navItems = Constants.BottomNavItems
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val selectedRoute = navBackStackEntry.value?.destination?.route

    val showBottomNav = navItems.any{it.route == selectedRoute}

    Box(
        modifier = modifier
            .background
                (
            MaterialTheme.colorScheme.background
        )
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        AppNavGraph(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )

        if (showBottomNav){
            CustomBottomNavigation(
                items = navItems,
                selectedRoute = selectedRoute,
                onItemSelected = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//private fun PreviewPlug() {
//    BarbershopTheme {
//        MainScreen()
//    }
//}