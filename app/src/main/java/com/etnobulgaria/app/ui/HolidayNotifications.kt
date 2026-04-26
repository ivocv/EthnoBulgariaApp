package com.etnobulgaria.app.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.etnobulgaria.app.MainActivity
import com.etnobulgaria.app.R
import com.etnobulgaria.app.data.AppSettingsRepository
import com.etnobulgaria.app.data.EthnoContentRepository
import java.util.Calendar
import java.util.concurrent.TimeUnit

private const val HOLIDAY_NOTIFICATION_CHANNEL_ID = "holiday_notifications"
private const val HOLIDAY_NOTIFICATION_CHANNEL_NAME_BG = "Ежедневни етно известия"
private const val HOLIDAY_NOTIFICATION_CHANNEL_NAME_EN = "Daily ethno alerts"
private const val HOLIDAY_NOTIFICATION_CHANNEL_DESCRIPTION_BG =
    "Ежедневни известия за празници, именни дни и въпроса на деня"
private const val HOLIDAY_NOTIFICATION_CHANNEL_DESCRIPTION_EN =
    "Daily alerts for holidays, name days and the question of the day"
private const val HOLIDAY_NOTIFICATION_WORK_NAME = "ethno_holiday_notifications"

data class DailyNotificationPreview(
    val label: String,
)

private data class DailyNotificationContent(
    val title: String,
    val body: String,
    val previewLabel: String,
)

class HolidayNotificationWorker(
    appContext: Context,
    params: WorkerParameters,
) : Worker(appContext, params) {
    override fun doWork(): Result {
        ensureHolidayNotificationChannel(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val content = buildDailyNotificationContent(applicationContext) ?: return Result.success()
        postDailyNotification(
            context = applicationContext,
            notificationId = 1001,
            title = content.title,
            body = content.body,
        )
        return Result.success()
    }
}

fun ensureHolidayNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val appLanguage = context.readAppLanguagePreference()
    val channelName = if (appLanguage == AppLanguage.BG) {
        HOLIDAY_NOTIFICATION_CHANNEL_NAME_BG
    } else {
        HOLIDAY_NOTIFICATION_CHANNEL_NAME_EN
    }
    val channelDescription = if (appLanguage == AppLanguage.BG) {
        HOLIDAY_NOTIFICATION_CHANNEL_DESCRIPTION_BG
    } else {
        HOLIDAY_NOTIFICATION_CHANNEL_DESCRIPTION_EN
    }

    val channel = NotificationChannel(
        HOLIDAY_NOTIFICATION_CHANNEL_ID,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = channelDescription
    }

    context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
}

fun areHolidayNotificationsEnabled(context: Context): Boolean {
    return AppSettingsRepository.areHolidayNotificationsEnabled(context.applicationContext)
}

fun scheduleHolidayNotifications(context: Context) {
    ensureHolidayNotificationChannel(context)

    val initialDelay = millisUntilNextNotificationSlot()
    val request = PeriodicWorkRequestBuilder<HolidayNotificationWorker>(
        24,
        TimeUnit.HOURS,
    )
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
        HOLIDAY_NOTIFICATION_WORK_NAME,
        ExistingPeriodicWorkPolicy.UPDATE,
        request,
    )

    AppSettingsRepository.setHolidayNotificationsEnabled(context.applicationContext, true)
}

fun cancelHolidayNotifications(context: Context) {
    WorkManager.getInstance(context.applicationContext).cancelUniqueWork(HOLIDAY_NOTIFICATION_WORK_NAME)
    AppSettingsRepository.setHolidayNotificationsEnabled(context.applicationContext, false)
}

fun previewNextHolidayNotification(context: Context): DailyNotificationPreview? {
    ensureHolidayNotificationChannel(context)
    val content = buildDailyNotificationContent(context.applicationContext) ?: return null
    postDailyNotification(
        context = context.applicationContext,
        notificationId = 1002,
        title = content.title,
        body = content.body,
    )
    return DailyNotificationPreview(label = content.previewLabel)
}

private fun buildDailyNotificationContent(context: Context): DailyNotificationContent? {
    val localizedContext = context.createLocalizedContext(
        context.readAppLanguagePreference(),
    )
    val holiday = holidaysForDate(
        entries = EthnoContentRepository.loadHolidayEntries(context),
        calendar = Calendar.getInstance(),
    ).firstOrNull()

    val archiveProgress = ethnoArchiveProgress(
        entries = EthnoContentRepository.loadArchiveEntries(context),
        totalPoints = DailyQuestionRepository.loadState(context).totalPoints,
    )
    val archiveMessage = when (archiveProgress.pointsToNextUnlock) {
        null -> localizedContext.getString(R.string.archive_all_unlocked)
        0, 1 -> localizedContext.getString(R.string.notification_archive_ready)
        else -> localizedContext.getString(
            R.string.notification_archive_progress,
            archiveProgress.pointsToNextUnlock,
        )
    }

    return if (holiday != null) {
        val title = localizedContext.getString(
            R.string.notification_holiday_title,
            holiday.title.asString(localizedContext),
        )
        val nameDays = holiday.nameDays
            .takeIf { it.isNotEmpty() }
            ?.joinToString(separator = ", ") { it.asString(localizedContext) }
        val body = buildString {
            append(holiday.summary.asString(localizedContext))
            if (!nameDays.isNullOrBlank()) {
                append(" ")
                append(
                    localizedContext.getString(
                        R.string.notification_namedays_suffix,
                        nameDays,
                    ),
                )
            }
            append(" ")
            append(
                localizedContext.getString(
                    R.string.notification_daily_question_suffix,
                    archiveMessage,
                ),
            )
        }

        DailyNotificationContent(
            title = title,
            body = body,
            previewLabel = holiday.title.asString(localizedContext),
        )
    } else {
        DailyNotificationContent(
            title = localizedContext.getString(R.string.notification_daily_question_title),
            body = localizedContext.getString(
                R.string.notification_daily_question_body,
                archiveMessage,
            ),
            previewLabel = localizedContext.getString(R.string.home_daily_question_title),
        )
    }
}

private fun postDailyNotification(
    context: Context,
    notificationId: Int,
    title: String,
    body: String,
) {
    val launchIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        launchIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val notification = NotificationCompat.Builder(context, HOLIDAY_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    NotificationManagerCompat.from(context).notify(notificationId, notification)
}

private fun millisUntilNextNotificationSlot(now: Calendar = Calendar.getInstance()): Long {
    val nextRun = (now.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (!after(now)) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    return nextRun.timeInMillis - now.timeInMillis
}
