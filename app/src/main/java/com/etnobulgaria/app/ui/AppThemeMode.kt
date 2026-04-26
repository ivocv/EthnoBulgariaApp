package com.etnobulgaria.app.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.etnobulgaria.app.data.AppSettingsRepository

enum class AppThemeMode(val preferenceValue: String) {
    LIGHT("light"),
    DARK("dark");

    fun isDarkTheme(): Boolean = this == DARK

    companion object {
        fun fromPreferenceValue(value: String?): AppThemeMode {
            return entries.firstOrNull { it.preferenceValue == value } ?: LIGHT
        }
    }
}

@Composable
fun rememberAppThemePreference(context: Context): Pair<AppThemeMode, (AppThemeMode) -> Unit> {
    val appContext = remember(context) { context.applicationContext }
    var appThemeMode by remember(appContext) {
        mutableStateOf(AppSettingsRepository.readThemeMode(appContext))
    }

    val onThemeSelected: (AppThemeMode) -> Unit = remember(appContext) {
        { newMode ->
            appThemeMode = newMode
            AppSettingsRepository.writeThemeMode(appContext, newMode)
        }
    }

    return appThemeMode to onThemeSelected
}

fun Context.readAppThemePreference(): AppThemeMode {
    return AppSettingsRepository.readThemeMode(applicationContext)
}
