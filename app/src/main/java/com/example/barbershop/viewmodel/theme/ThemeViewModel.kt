package com.example.barbershop.viewmodel.theme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barbershop.AppSettings
import com.example.barbershop.data.model.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {
    private val _currentTheme = mutableStateOf(ThemeType.DARK)
    val currentTheme: State<ThemeType> = _currentTheme

    init {
        viewModelScope.launch {
            appSettings.themeFlow.collect { theme ->
                _currentTheme.value = theme
            }
        }
    }

    fun toggleTheme() {
        val newTheme = when (_currentTheme.value) {
            ThemeType.LIGHT -> ThemeType.DARK
            ThemeType.DARK -> ThemeType.LIGHT
        }
        updateTheme(newTheme)
    }

    fun updateTheme(theme: ThemeType) {
        _currentTheme.value = theme
        viewModelScope.launch {
            appSettings.setTheme(theme)
        }
    }
}