package com.example.barbershop.ui.components.customComponent

import android.net.Uri
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.barbershop.R
import com.skydoves.landscapist.glide.GlideImage
import timber.log.Timber
import java.io.File

@Composable
fun AdaptiveIcon(
    imageSrc: String,
    tintColor: Color = Color(0xff14a9a9),
    modifier: Modifier = Modifier,
    containerShape: Shape = RoundedCornerShape(20.dp),
    containerSize: Dp = 64.dp,
    iconSize: Dp = 44.dp,
    backgroundColor: Color = Color.Transparent,
    contentAlignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    placeholder: @Composable () -> Unit = {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = tintColor,
            strokeWidth = 2.dp
        )
    },
    errorIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size((iconSize * 0.8f))
        )
    },
    applyTint: Boolean = true,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Дополнительное логирование и проверка источника изображения
    Timber.tag("AdaptiveIcon").d("Инициализация компонента с imageSrc: $imageSrc")

    val isResource = imageSrc.isResourceString()

    val isValidSource = if (isResource) {
        val resId = imageSrc.toDrawableResId()
        resId?.let {
            try {
                context.resources.getResourceName(it)
                true
            } catch (e: Exception) {
                Timber.tag("AdaptiveIcon").e(e, "Неверное ресурсное имя для resId: $it")
                false
            }
        } ?: false
    } else {
        when {
            imageSrc.startsWith("file://") -> {
                val exists = File(imageSrc.removePrefix("file://")).exists()
                Timber.tag("AdaptiveIcon").d("Проверка существования локального файла: $exists")
                exists
            }
            else -> {
                val matcherResult = Patterns.WEB_URL.matcher(imageSrc).matches()
                Timber.tag("AdaptiveIcon").d("Проверка URL: $matcherResult")
                matcherResult
            }
        }
    }

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(backgroundColor, containerShape)
            .size(containerSize)
            .clip(containerShape)
    ) {
        if (!isValidSource) {
            Timber.tag("AdaptiveIcon").e("Неверный источник изображения: $imageSrc")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(contentAlignment),
                contentAlignment = Alignment.Center
            ) {
                errorIcon()
            }
            return@Box
        }

        if (isResource) {
            val resId = imageSrc.toDrawableResId() ?: R.drawable.cross
            val painter = painterResource(resId)
            Image(
                painter = painter,
                contentDescription = null,
                colorFilter = if (applyTint) ColorFilter.tint(backgroundColor.copy(alpha = 1f)) else null,
                modifier = Modifier
                    .size(iconSize)
                    .align(contentAlignment),
                contentScale = contentScale
            )
        } else {
            // Использование библиотеки Landscapist Glide для загрузки изображения по ссылке
            Timber.tag("AdaptiveIcon").d("Используем GlideImage для загрузки: $imageSrc")
            GlideImage(
                imageModel = imageSrc,
                contentScale = contentScale,
                modifier = Modifier
                    .size(iconSize)
                    .align(contentAlignment),
                // Встроенные обработчики для placeholder и error
                loading = {
                    Timber.tag("AdaptiveIcon").d("Состояние загрузки для: $imageSrc")
                    placeholder()
                },
                // Параметр failure – для отображения ошибки при загрузке
                failure = {
                    Timber.tag("AdaptiveIcon").e("Ошибка загрузки изображения с $imageSrc")
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(contentAlignment),
                        contentAlignment = Alignment.Center
                    ) {
                        errorIcon()
                    }
                }
            )
        }
    }
}

// Дополнительные функции для обработки строк ресурсов
fun Int.toImageResourceString(): String = "res://$this"

fun Uri.toLocalFileString(): String = "file://$this"

fun String.isResourceString(): Boolean = startsWith("res://")

fun String.isLocalFile(): Boolean = startsWith("file://")

fun String.toDrawableResId(): Int? = removePrefix("res://").toIntOrNull()