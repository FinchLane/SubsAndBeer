package com.example.barbershop

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.barbershop.data.model.ThemeType
import com.example.barbershop.ui.main.MainScreen
import com.example.barbershop.ui.theme.BarbershopTheme
import com.example.barbershop.viewmodel.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var appSettings: AppSettings
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var keepSplashOnScreen by mutableStateOf(true)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        lifecycleScope.launch {
            delay(1500)
            keepSplashOnScreen = false
        }

        enableEdgeToEdge()

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            isGranted: Boolean ->
            if (!isGranted){
                Toast.makeText(this, "Разрешение не выдано", Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val currentTheme by themeViewModel.currentTheme

            val darkTheme = when (currentTheme) {
                ThemeType.LIGHT -> false
                ThemeType.DARK -> true
            }

            BarbershopTheme(darkTheme) {
                MainScreen()
            }
        }
    }
}
