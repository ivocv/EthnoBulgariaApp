package com.etnobulgaria.app.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.etnobulgaria.app.data.EthnoSQLiteHelper
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class EthnoDiaryEntry(
    val id: String,
    val createdAtMillis: Long,
    val note: String,
    val photoPath: String?,
)

data class NearbyEthnoPlace(
    val region: EthnoRegion,
    val place: CulturalPlaceInfo,
    val distanceKm: Double,
)

private val NEARBY_ETHNO_RADII_KM = listOf(50.0, 120.0, 250.0, 400.0)
private const val BULGARIA_MIN_LAT = 41.2
private const val BULGARIA_MAX_LAT = 44.3
private const val BULGARIA_MIN_LON = 22.3
private const val BULGARIA_MAX_LON = 28.7
const val SOFIA_FALLBACK_LAT = 42.6977
const val SOFIA_FALLBACK_LON = 23.3219

object EthnoDiaryRepository {
    fun loadEntries(context: Context): List<EthnoDiaryEntry> {
        return EthnoSQLiteHelper.getInstance(context).loadDiaryEntries()
    }

    fun saveEntry(context: Context, entry: EthnoDiaryEntry) {
        EthnoSQLiteHelper.getInstance(context).saveDiaryEntry(entry)
    }
}

fun createDiaryImageFile(context: Context): File {
    val diaryDir = File(context.filesDir, "ethno_diary").apply { mkdirs() }
    return File(diaryDir, "entry_${System.currentTimeMillis()}.jpg")
}

fun copyDiaryImageFromUri(context: Context, sourceUri: Uri): String? {
    val destinationFile = createDiaryImageFile(context)
    return runCatching {
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destinationFile.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        destinationFile.absolutePath
    }.getOrElse {
        destinationFile.delete()
        null
    }
}

fun newDiaryEntry(
    note: String,
    photoPath: String?,
): EthnoDiaryEntry = EthnoDiaryEntry(
    id = UUID.randomUUID().toString(),
    createdAtMillis = System.currentTimeMillis(),
    note = note.trim(),
    photoPath = photoPath,
)

fun loadDiaryImage(photoPath: String?): ImageBitmap? {
    if (photoPath.isNullOrBlank()) return null
    val bitmap = BitmapFactory.decodeFile(photoPath) ?: return null
    return rotateBitmapIfNeeded(bitmap, photoPath).asImageBitmap()
}

fun formatDiaryDate(context: Context, createdAtMillis: Long): String {
    val locale = context.readAppLanguagePreference().locale
    val formatter = SimpleDateFormat("d MMM yyyy, HH:mm", locale)
    return formatter.format(Date(createdAtMillis))
}

fun formatNearbyDistance(context: Context, distanceKm: Double): String {
    val locale = context.readAppLanguagePreference().locale
    return if (distanceKm < 1.0) {
        val meters = distanceKm * 1000.0
        val formatter = NumberFormat.getIntegerInstance(locale)
        "${formatter.format(meters)} m"
    } else {
        val formatter = NumberFormat.getNumberInstance(locale).apply {
            maximumFractionDigits = 1
            minimumFractionDigits = 1
        }
        "${formatter.format(distanceKm)} km"
    }
}

private fun rotateBitmapIfNeeded(bitmap: Bitmap, photoPath: String): Bitmap {
    val exifOrientation = runCatching {
        ExifInterface(photoPath).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL,
        )
    }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

    val rotationDegrees = when (exifOrientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }
    if (rotationDegrees == 0f) return bitmap

    val matrix = Matrix().apply { postRotate(rotationDegrees) }
    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true,
    )
}

fun isWithinBulgaria(latitude: Double, longitude: Double): Boolean {
    return latitude in BULGARIA_MIN_LAT..BULGARIA_MAX_LAT &&
        longitude in BULGARIA_MIN_LON..BULGARIA_MAX_LON
}

fun computeNearbyEthnoPlaces(
    regions: List<EthnoRegion>,
    latitude: Double,
    longitude: Double,
    limit: Int = 5,
): List<NearbyEthnoPlace> {
    val origin = FloatArray(1)
    val sortedPlaces = regions
        .flatMap { region ->
            region.places.mapNotNull { place ->
                val placeLatitude = place.latitude
                val placeLongitude = place.longitude
                if (placeLatitude == null || placeLongitude == null) {
                    null
                } else {
                    Location.distanceBetween(
                        latitude,
                        longitude,
                        placeLatitude,
                        placeLongitude,
                        origin,
                    )
                    NearbyEthnoPlace(
                        region = region,
                        place = place,
                        distanceKm = origin[0] / 1000.0,
                    )
                }
            }
        }
        .sortedBy { it.distanceKm }

    if (sortedPlaces.isEmpty()) return emptyList()

    val selectedRadius = NEARBY_ETHNO_RADII_KM.firstOrNull { radius ->
        sortedPlaces.count { it.distanceKm <= radius } >= 3
    } ?: NEARBY_ETHNO_RADII_KM.last()

    val placesWithinRadius = sortedPlaces.filter { it.distanceKm <= selectedRadius }
    return placesWithinRadius.ifEmpty { sortedPlaces.take(limit) }.take(limit)
}
