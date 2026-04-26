package com.etnobulgaria.app.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale as ComposeLocale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.etnobulgaria.app.R
import com.etnobulgaria.app.data.EthnoContentRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Calendar

private enum class NotificationPermissionAction {
    ENABLE,
    PREVIEW,
}

@Composable
internal fun NearbyPlacesCard(regions: List<EthnoRegion>) {
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val locationPermissions = remember {
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
    val nearbyPermissionDenied = stringResource(R.string.nearby_permission_denied)
    val nearbyEmpty = stringResource(R.string.nearby_empty)
    var nearbyPlaces by remember(appContext, regions) {
        mutableStateOf(emptyList<NearbyEthnoPlace>())
    }
    var statusMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    fun resolveNearbyPlaces() {
        isLoading = true
        statusMessage = null
        fetchCurrentLocation(
            context = appContext,
            onSuccess = { latitude, longitude ->
                val useFallback = !isWithinBulgaria(latitude, longitude)
                val searchLatitude = if (useFallback) SOFIA_FALLBACK_LAT else latitude
                val searchLongitude = if (useFallback) SOFIA_FALLBACK_LON else longitude
                nearbyPlaces = computeNearbyEthnoPlaces(
                    regions = regions,
                    latitude = searchLatitude,
                    longitude = searchLongitude,
                )
                statusMessage = when {
                    nearbyPlaces.isEmpty() -> nearbyEmpty
                    else -> null
                }
                isLoading = false
            },
            onError = {
                nearbyPlaces = computeNearbyEthnoPlaces(
                    regions = regions,
                    latitude = SOFIA_FALLBACK_LAT,
                    longitude = SOFIA_FALLBACK_LON,
                )
                statusMessage = if (nearbyPlaces.isNotEmpty()) null else it
                isLoading = false
            },
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        if (grants.values.any { it }) {
            resolveNearbyPlaces()
        } else {
            statusMessage = nearbyPermissionDenied
        }
    }

    FeatureCard {
        Text(
            text = stringResource(R.string.nearby_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.nearby_summary),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(14.dp))
        FilledTonalButton(
            onClick = {
                if (hasLocationPermission(appContext)) {
                    resolveNearbyPlaces()
                } else {
                    locationPermissionLauncher.launch(locationPermissions)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.nearby_action))
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.nearby_loading),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        statusMessage?.takeIf { it.isNotBlank() }?.let { message ->
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (nearbyPlaces.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            nearbyPlaces.forEach { place ->
                MiniFeatureCard(
                    title = place.place.name.asString(),
                    subtitle = "${place.region.name.asString()} • ${place.place.location.asString()}",
                    body = place.place.summary.asString(),
                )
            }
        }
    }
}

@Composable
internal fun EthnoDiaryCard() {
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val appLanguage = context.readAppLanguagePreference()

    var entries by remember(appContext) {
        mutableStateOf(EthnoDiaryRepository.loadEntries(appContext))
    }
    var noteText by rememberSaveable { mutableStateOf("") }
    var pendingPhotoPath by rememberSaveable { mutableStateOf<String?>(null) }
    var feedbackMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isCameraCaptureOpen by rememberSaveable { mutableStateOf(false) }

    val saveSuccessMessage = stringResource(R.string.diary_saved)
    val captureFailedMessage = stringResource(R.string.diary_capture_failed)
    val pickFailedMessage = stringResource(R.string.diary_pick_failed)
    val cameraPermissionDeniedMessage = stringResource(R.string.diary_camera_permission_denied)
    val noteKeyboardOptions = remember(appLanguage) {
        KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrectEnabled = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default,
            hintLocales = LocaleList(ComposeLocale(appLanguage.code)),
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            isCameraCaptureOpen = true
        } else {
            feedbackMessage = cameraPermissionDeniedMessage
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val selectedUri = result.data?.data
        if (selectedUri == null) {
            return@rememberLauncherForActivityResult
        }

        takePersistableReadPermission(appContext, selectedUri)
        val copiedImagePath = copyDiaryImageFromUri(appContext, selectedUri)
        if (copiedImagePath != null) {
            pendingPhotoPath = copiedImagePath
            feedbackMessage = null
        } else {
            feedbackMessage = pickFailedMessage
        }
    }

    FeatureCard {
        Text(
            text = stringResource(R.string.diary_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.diary_summary),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(14.dp))
        OutlinedTextField(
            value = noteText,
            onValueChange = {
                noteText = it
                feedbackMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.diary_note_label)) },
            placeholder = { Text(text = stringResource(R.string.diary_note_placeholder)) },
            keyboardOptions = noteKeyboardOptions,
            minLines = 4,
        )

        pendingPhotoPath?.let { photoPath ->
            Spacer(modifier = Modifier.height(12.dp))
            DiaryPhotoPreview(
                photoPath = photoPath,
                description = stringResource(R.string.diary_photo_preview),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FilledTonalButton(
                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            appContext,
                            Manifest.permission.CAMERA,
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        isCameraCaptureOpen = true
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(text = stringResource(R.string.diary_action_capture))
            }

            FilledTonalButton(
                onClick = {
                    imagePickerLauncher.launch(createDiaryImagePickerIntent(appContext))
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(text = stringResource(R.string.diary_action_upload))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        FilledTonalButton(
            onClick = {
                val entry = newDiaryEntry(
                    note = noteText,
                    photoPath = pendingPhotoPath,
                )
                EthnoDiaryRepository.saveEntry(appContext, entry)
                entries = listOf(entry) + entries
                noteText = ""
                pendingPhotoPath = null
                feedbackMessage = saveSuccessMessage
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = noteText.isNotBlank() || pendingPhotoPath != null,
        ) {
            Text(text = stringResource(R.string.diary_action_save))
        }

        feedbackMessage?.takeIf { it.isNotBlank() }?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.diary_entries_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (entries.isEmpty()) {
            Text(
                text = stringResource(R.string.diary_no_entries),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            entries.forEach { entry ->
                DiaryEntryCard(entry = entry)
            }
        }
    }

    if (isCameraCaptureOpen) {
        DiaryCameraCaptureDialog(
            onDismiss = { isCameraCaptureOpen = false },
            onCaptured = { photoPath ->
                pendingPhotoPath = photoPath
                feedbackMessage = null
                isCameraCaptureOpen = false
            },
            onError = { message ->
                feedbackMessage = message.ifBlank { captureFailedMessage }
                isCameraCaptureOpen = false
            },
        )
    }
}

@Composable
internal fun HolidayNotificationsCard() {
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val holidayEntries = remember(appContext) {
        EthnoContentRepository.loadHolidayEntries(appContext)
    }
    val archiveEntries = remember(appContext) {
        EthnoContentRepository.loadArchiveEntries(appContext)
    }
    val todayHoliday = remember(holidayEntries) {
        holidaysForDate(
            entries = holidayEntries,
            calendar = Calendar.getInstance(),
        ).firstOrNull()
    }

    var areNotificationsEnabled by remember(appContext) {
        mutableStateOf(areHolidayNotificationsEnabled(appContext))
    }
    var feedbackMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingPermissionAction by rememberSaveable {
        mutableStateOf<NotificationPermissionAction?>(null)
    }

    fun enableNotifications() {
        scheduleHolidayNotifications(appContext)
        areNotificationsEnabled = true
        feedbackMessage = context.getString(R.string.notifications_enabled_state)
    }

    fun showPreview() {
        val preview = previewNextHolidayNotification(appContext)
        feedbackMessage = if (preview != null) {
            context.getString(
                R.string.notifications_preview_sent,
                preview.label,
            )
        } else {
            context.getString(R.string.notifications_none_today)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val action = pendingPermissionAction
        pendingPermissionAction = null
        if (!granted) {
            feedbackMessage = context.getString(R.string.notifications_permission_denied)
            return@rememberLauncherForActivityResult
        }

        when (action) {
            NotificationPermissionAction.ENABLE -> enableNotifications()
            NotificationPermissionAction.PREVIEW -> showPreview()
            null -> Unit
        }
    }

    val todaySummary = remember(todayHoliday, archiveEntries, context.resources.configuration) {
        val archiveProgress = ethnoArchiveProgress(
            entries = archiveEntries,
            totalPoints = DailyQuestionRepository.loadState(appContext).totalPoints,
        )
        val archiveLine = when (archiveProgress.pointsToNextUnlock) {
            null -> context.getString(R.string.archive_all_unlocked)
            0, 1 -> context.getString(R.string.notification_archive_ready)
            else -> context.getString(
                R.string.notification_archive_progress,
                archiveProgress.pointsToNextUnlock,
            )
        }
        buildString {
            if (todayHoliday != null) {
                append(
                    context.getString(
                        R.string.notifications_today_holiday,
                        todayHoliday.title.asString(context),
                    ),
                )
                if (todayHoliday.nameDays.isNotEmpty()) {
                    append("\n")
                    append(
                        context.getString(
                            R.string.notifications_namedays,
                            todayHoliday.nameDays.joinToString(", ") { it.asString(context) },
                        ),
                    )
                }
                append("\n")
            } else {
                append(context.getString(R.string.notifications_none_today))
                append("\n")
            }
            append(context.getString(R.string.notifications_today_question))
            append("\n")
            append(archiveLine)
        }
    }

    FeatureCard {
        Text(
            text = stringResource(R.string.notifications_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.notifications_summary),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = todaySummary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (areNotificationsEnabled) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.notifications_enabled_state),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FilledTonalButton(
                onClick = {
                    if (areNotificationsEnabled) {
                        cancelHolidayNotifications(appContext)
                        areNotificationsEnabled = false
                        feedbackMessage = null
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        !NotificationManagerCompat.from(appContext).areNotificationsEnabled()
                    ) {
                        pendingPermissionAction = NotificationPermissionAction.ENABLE
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        enableNotifications()
                    }
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = if (areNotificationsEnabled) {
                        stringResource(R.string.notifications_action_disable)
                    } else {
                        stringResource(R.string.notifications_action_enable)
                    },
                )
            }

            FilledTonalButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        !NotificationManagerCompat.from(appContext).areNotificationsEnabled()
                    ) {
                        pendingPermissionAction = NotificationPermissionAction.PREVIEW
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        showPreview()
                    }
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(text = stringResource(R.string.notifications_action_preview))
            }
        }

        feedbackMessage?.takeIf { it.isNotBlank() }?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FeatureCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content,
        )
    }
}

@Composable
private fun MiniFeatureCard(
    title: String,
    subtitle: String,
    body: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (body.isNotBlank()) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DiaryEntryCard(entry: EthnoDiaryEntry) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = formatDiaryDate(context, entry.createdAtMillis),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            entry.photoPath?.let { photoPath ->
                DiaryPhotoPreview(
                    photoPath = photoPath,
                    description = stringResource(R.string.diary_photo_preview),
                )
            }
            if (entry.note.isNotBlank()) {
                Text(
                    text = entry.note,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun DiaryPhotoPreview(
    photoPath: String,
    description: String,
) {
    val imageBitmap = remember(photoPath) { loadDiaryImage(photoPath) }
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = description,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(20.dp)),
        )
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
}

private fun fetchCurrentLocation(
    context: Context,
    onSuccess: (Double, Double) -> Unit,
    onError: (String) -> Unit,
) {
    if (!hasLocationPermission(context)) {
        onError(context.getString(R.string.nearby_permission_denied))
        return
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cancellationTokenSource = CancellationTokenSource()

    fun useLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    onSuccess(lastLocation.latitude, lastLocation.longitude)
                } else {
                    onError(context.getString(R.string.nearby_location_missing))
                }
            }
            .addOnFailureListener {
                onError(context.getString(R.string.nearby_location_missing))
            }
    }

    try {
        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token,
            )
            .addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(location.latitude, location.longitude)
                } else {
                    useLastKnownLocation()
                }
            }
            .addOnFailureListener {
                useLastKnownLocation()
            }
    } catch (_: SecurityException) {
        onError(context.getString(R.string.nearby_permission_denied))
    }
}

private fun createDiaryImagePickerIntent(context: Context): Intent {
    val mimeTypes = arrayOf("image/jpeg", "image/png", "image/webp")
    val openDocumentIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
        putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, MediaStore.Downloads.EXTERNAL_CONTENT_URI)
        }
    }
    val getContentIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
        putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        putExtra(Intent.EXTRA_LOCAL_ONLY, true)
    }

    return Intent.createChooser(
        openDocumentIntent,
        context.getString(R.string.diary_pick_chooser_title),
    ).apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(getContentIntent))
    }
}

private fun takePersistableReadPermission(
    context: Context,
    uri: Uri,
) {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
    }
}
