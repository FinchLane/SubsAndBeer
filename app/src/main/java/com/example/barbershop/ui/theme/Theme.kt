package com.example.barbershop.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80

    /* Темная тема v1 */
   /* primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkAccent,
    background = DarkPrimary,
    surface = DarkPrimary,
    onPrimary = DarkText,
    onSecondary = Color.Gray,
    onBackground = DarkText,
    onSurface = Dark_Grey */

    /* Темная тема v2 */
//    primary = DarkBackground,           // Фон
//    secondary = DarkContainer,          // Контейнеры
//    tertiary = DarkButton,              // Цвет кнопок
//    background = DarkBackground,        // Основной фон
//    surface = DarkContainer,            // Поверхности и карточки
//    onPrimary = DarkTextPrimary,        // Основной цвет текста
//    onSecondary = DarkTextSecondary,    // Второй цвет текста
//    onBackground = DarkTextPrimary,     // Текст на фоне
//    onSurface = DarkTextPrimary         // Текст на поверхностях

    //темная тема 2.1
    primary = DarkPrimary,           // Основной цвет (Primary)
    onPrimary = DarkOnPrimary,       // Цвет контента на Primary
    secondary = DarkSecondary,       // Вторичный цвет (Secondary)
    onSecondary = DarkOnSecondary,   // Цвет контента на Secondary
    background = DarkBackground,     // Фон (Background)
    onBackground = DarkOnBackground, // Цвет контента на Background
    surface = DarkSurface,           // Поверхность (Surface)
    onSurface = DarkOnSurface,       // Цвет контента на Surface
    tertiary = DarkAccent,            // Акцентный цвет (Tertiary)
)

private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */

    /* Светлая тема v1 */
//    primary = LightPrimary,
//    secondary = LightSecondary,
//    tertiary = LightAccent,
//    background = LightPrimary,
//    surface = LightPrimary,
//    onPrimary = LightText,
//    onSecondary = Color.Gray,
//    onBackground = LightText,
//    onSurface = Dark_Grey

    /* Светлая тема v2 */
//    primary = LightPrimary,            // Основной фон
//    secondary = LightSecondary,        // Контейнеры и акценты
//    tertiary = LightAccent,            // Цвет кнопок
//    background = LightPrimary,         // Основной фон
//    surface = LightSurface,            // Поверхности и карточки
//    onPrimary = LightTextPrimary,      // Основной цвет текста
//    onSecondary = LightTextSecondary,  // Второй цвет текста
//    onBackground = LightTextPrimary,   // Текст на фоне
//    onSurface = LightTextPrimary       // Текст на поверхностях

    // Светлая тема 2.1
    primary = LightPrimary,          // Основной цвет (Primary)
    onPrimary = LightOnPrimary,      // Цвет контента на Primary
    secondary = LightSecondary,      // Вторичный цвет (Secondary)
    onSecondary = LightOnSecondary,  // Цвет контента на Secondary
    background = LightBackground,    // Фон (Background)
    onBackground = LightOnBackground,// Цвет контента на Background
    surface = LightSurface,          // Поверхность (Surface)
    onSurface = LightOnSurface,      // Цвет контента на Surface
    tertiary = LightAccent,           // Акцентный цвет (Tertiary)
)

@Composable
fun BarbershopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}