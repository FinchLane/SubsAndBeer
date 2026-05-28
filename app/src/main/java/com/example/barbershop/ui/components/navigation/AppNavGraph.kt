package com.example.barbershop.ui.components.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.barbershop.ui.analytics.AnalyticsScreen
import com.example.barbershop.ui.analytics.CategoryChartScreen
import com.example.barbershop.ui.analytics.CurrencyChartScreen
import com.example.barbershop.ui.analytics.ListCurrencyScreen
import com.example.barbershop.ui.analytics.PaymentMethodChartScreen
import com.example.barbershop.ui.analytics.TotalSubscription
import com.example.barbershop.ui.authorization.ConfirmationLoginScreen
import com.example.barbershop.ui.authorization.LoginScreen
import com.example.barbershop.ui.calendar.CalendarScreen
import com.example.barbershop.ui.profile.ProfileScreen
import com.example.barbershop.ui.profile.editProfile.EditName
import com.example.barbershop.ui.profile.editProfile.EditProfileScreen
import com.example.barbershop.ui.profile.editProfile.RelatedAccountsScreen
import com.example.barbershop.ui.profile.editProfile.editEmail.EditEmail
import com.example.barbershop.ui.profile.editProfile.editPhoneNumber.EditPhoneNumber
import com.example.barbershop.ui.profile.notification.NotificationScreen
import com.example.barbershop.ui.profile.security.AuthDevicesScreen
import com.example.barbershop.ui.profile.security.DeviceScreen
import com.example.barbershop.ui.subscription.AddSubScreen
import com.example.barbershop.ui.subscription.CategoryTemplateScreen
import com.example.barbershop.ui.subscription.HomeScreen
import com.example.barbershop.ui.subscription.NewSubScreen
import com.example.barbershop.ui.subscription.PrepaymentScreen
import com.example.barbershop.ui.subscription.SubScreen
import com.example.barbershop.ui.subscription.category.CategoryScreen
import com.example.barbershop.ui.subscription.currency.CurrencyScreen
import com.example.barbershop.ui.subscription.paymentMethod.PaymentMethodScreen
import com.example.barbershop.viewmodel.authorization.AuthViewModel
import com.example.barbershop.viewmodel.subscription.SubViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val subViewModel: SubViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {

        /** bottomNavigation Главный экран */

        composable("home") {
            HomeScreen(navController, viewModel = subViewModel)
        }
        composable("analytics") { AnalyticsScreen(navController, subViewModel =  subViewModel) }
        composable("calendar") { CalendarScreen(navController, subViewModel = subViewModel) }
        composable("profile") { ProfileScreen(navController) }

        /** Авторизация (Authorization) */

        composable("login") { LoginScreen(authViewModel, ipAddress = "-1", navController = navController) }
        composable(
            route = "confirmation?phoneNumber={phoneNumber}",
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            ConfirmationLoginScreen(authViewModel, "-1", phoneNumber, navController)
        }



        /** Профиль */

        composable("notification") { NotificationScreen(navController) }
        composable("editProfile") { EditProfileScreen(navController) }
        composable("editName") { EditName(navController) }
        composable("editEmail") { EditEmail(navController) }
        composable("editPhoneNumber") { EditPhoneNumber(navController)}
        composable("relatedAccounts") { RelatedAccountsScreen(navController)}
        composable("authDevices") { AuthDevicesScreen(navController) }
        composable("device/{sessionJson}") { backStackEntry ->
            val sessionJson = backStackEntry.arguments?.getString("sessionJson")
            sessionJson?.let {
                DeviceScreen(navController, it)
            }
        }

        /** Подписки */

        composable("newSub") {
            NewSubScreen(subViewModel, navController)
        }
        composable("categoryTemplate/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            CategoryTemplateScreen(subViewModel, navController, categoryId = categoryId?.toInt() ?: 0)
        }
        composable("addSub") {
            AddSubScreen(navController, subViewModel)
        }
        composable("subWithTemplate/{templateId}") { backStackEntry ->
            val templateId = backStackEntry.arguments?.getString("templateId")
            AddSubScreen(navController, subViewModel, templateId = templateId?.toInt() ?: 0)
        }
        composable("editSub/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            AddSubScreen(navController, subViewModel, id = (id?:"0"))
        }
        composable("category") {
            CategoryScreen(navController, subViewModel)
        }
        composable("paymentMethod") {
            PaymentMethodScreen(navController, subViewModel)
        }
        composable(
            route = "subInfo/{id}",
            deepLinks = listOf(navDeepLink { uriPattern = "app://dora/subs/{id}" })
        ){ backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            SubScreen(id = id?:"0", subViewModel, navController = navController)
        }
        composable("subPrepayment/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            PrepaymentScreen(
                navController = navController,
                subViewModel = subViewModel,
                id = id.toString()
            )
        }
        composable("currency") {
            CurrencyScreen(navController, subViewModel)
        }

        /** Аналитика */

        composable("totalSub") {
            TotalSubscription(
                subViewModel = subViewModel,
                navController = navController
            )
        }

        composable("listCurrency") {
            ListCurrencyScreen(
                navController = navController,
                subViewModel = subViewModel
            )
        }

        composable("currencyChart/{currency}/{percent}") { backStackEntry ->
            val currency = backStackEntry.arguments?.getString("currency")
            val percent = backStackEntry.arguments?.getString("percent")
            CurrencyChartScreen(
                subViewModel = subViewModel,
                navController = navController,
                currency = currency.toString(),
                percent = percent?.toDouble() ?: 100.0
            )
        }

        composable("categoryChart/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            CategoryChartScreen(
                subViewModel = subViewModel,
                navController = navController,
                categoryId = categoryId.toString()
            )
        }

        composable("paymentMethodChart/{paymentMethodId}") { backStackEntry ->
            val paymentMethodId = backStackEntry.arguments?.getString("paymentMethodId")
            PaymentMethodChartScreen(
                subViewModel = subViewModel,
                navController = navController,
                paymentMethodId = paymentMethodId.toString()
            )
        }
    }
}