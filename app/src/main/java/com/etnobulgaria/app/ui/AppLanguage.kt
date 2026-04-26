package com.etnobulgaria.app.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.etnobulgaria.app.data.AppSettingsRepository
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val locale: Locale,
) {
    BG(
        code = "bg",
        locale = Locale("bg"),
    ),
    EN(
        code = "en",
        locale = Locale.ENGLISH,
    );

    companion object {
        fun fromCode(code: String?): AppLanguage = entries.firstOrNull { it.code == code } ?: BG
    }
}

@Composable
fun rememberAppLanguagePreference(context: Context): Pair<AppLanguage, (AppLanguage) -> Unit> {
    val appContext = remember(context) { context.applicationContext }
    var appLanguage by remember(appContext) {
        mutableStateOf(AppSettingsRepository.readLanguage(appContext))
    }

    val onLanguageSelected: (AppLanguage) -> Unit = remember(appContext) {
        { newLanguage ->
            appLanguage = newLanguage
            AppSettingsRepository.writeLanguage(appContext, newLanguage)
        }
    }

    return appLanguage to onLanguageSelected
}

fun Context.readAppLanguagePreference(): AppLanguage {
    return AppSettingsRepository.readLanguage(applicationContext)
}

fun Context.createLocalizedContext(language: AppLanguage): Context {
    val locale = language.locale
    Locale.setDefault(locale)

    val configuration = Configuration(resources.configuration)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))
    } else {
        @Suppress("DEPRECATION")
        configuration.locale = locale
    }

    return createConfigurationContext(configuration)
}
