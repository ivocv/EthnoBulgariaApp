package com.etnobulgaria.app.ui

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

data class LocalizedText(
    val bg: String,
    val en: String,
) {
    fun resolve(locale: Locale): String = if (locale.language.equals("bg", ignoreCase = true)) {
        bg
    } else {
        en
    }
}

fun LocalizedText.asString(context: Context): String {
    val configuration = context.resources.configuration
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        configuration.locale
    } ?: Locale.getDefault()

    return resolve(locale)
}

@Composable
@ReadOnlyComposable
fun LocalizedText.asString(): String {
    val configuration = LocalContext.current.resources.configuration
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        configuration.locale
    } ?: Locale.getDefault()

    return resolve(locale)
}
