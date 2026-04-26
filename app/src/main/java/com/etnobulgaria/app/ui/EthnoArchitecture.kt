package com.etnobulgaria.app.ui

import java.util.Calendar
import java.util.GregorianCalendar

enum class CostumeCategory {
    WOMEN,
    MEN,
}

enum class CostumeElementType {
    SHIRT,
    APRON,
    SKIRT,
    SASH,
    VEST,
    BELTS,
    JEWELRY,
    BUCKLES,
    LEATHER_SHOES,
    LEG_WRAPS,
    SKARPINI,
    TROUSERS,
    ORNAMENT,
}

data class CostumeElementInfo(
    val id: String,
    val type: CostumeElementType,
    val title: LocalizedText,
    val summary: LocalizedText,
)

data class CostumeHotspot(
    val elementId: String,
    val relativeX: Float,
    val relativeY: Float,
)

data class AudioClipInfo(
    val title: LocalizedText,
    val assetPath: String?,
    val durationSeconds: Int,
    val note: LocalizedText,
)

data class RegionGamificationInfo(
    val stampTitle: LocalizedText,
    val stampDescription: LocalizedText,
    val badgeTitle: LocalizedText,
    val badgeDescription: LocalizedText,
    val progressDescription: LocalizedText,
)

data class EthnoDiaryPrompt(
    val title: LocalizedText,
    val hint: LocalizedText,
)

data class EthnoDiaryBlueprint(
    val title: LocalizedText,
    val summary: LocalizedText,
    val prompts: List<EthnoDiaryPrompt>,
    val supportsCameraPhoto: Boolean,
)

data class MapPoint(
    val x: Float,
    val y: Float,
)

data class MapRegionArea(
    val regionId: EthnoRegionId,
    val label: LocalizedText,
    val center: MapPoint,
    val touchPolygon: List<MapPoint> = emptyList(),
)

data class InteractiveMapBlueprint(
    val assetPath: String?,
    val fallbackTitle: LocalizedText,
    val fallbackSummary: LocalizedText,
    val areas: List<MapRegionArea>,
)

enum class HolidayRuleType {
    FIXED_DATE,
    ORTHODOX_EASTER_OFFSET,
}

data class HolidayDateRule(
    val ruleType: HolidayRuleType,
    val month: Int? = null,
    val day: Int? = null,
    val easterOffsetDays: Int? = null,
)

data class HolidayCalendarEntry(
    val id: String,
    val title: LocalizedText,
    val summary: LocalizedText,
    val rule: HolidayDateRule,
    val nameDays: List<LocalizedText> = emptyList(),
    val priority: Int = 0,
)

fun HolidayDateRule.resolveDate(year: Int): Calendar {
    return when (ruleType) {
        HolidayRuleType.FIXED_DATE -> {
            require(month != null && day != null) {
                "Fixed-date holidays require month and day."
            }
            gregorianCalendar(year, month, day)
        }

        HolidayRuleType.ORTHODOX_EASTER_OFFSET -> {
            require(easterOffsetDays != null) {
                "Orthodox Easter-based holidays require an offset."
            }
            orthodoxEasterSunday(year).apply {
                add(Calendar.DAY_OF_YEAR, easterOffsetDays)
            }
        }
    }
}

fun HolidayCalendarEntry.occursOn(calendar: Calendar): Boolean {
    val resolvedDate = rule.resolveDate(calendar.get(Calendar.YEAR))
    return resolvedDate.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
        resolvedDate.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
}

fun holidaysForDate(
    entries: List<HolidayCalendarEntry>,
    calendar: Calendar = Calendar.getInstance(),
): List<HolidayCalendarEntry> {
    return entries
        .filter { it.occursOn(calendar) }
        .sortedByDescending { it.priority }
}

fun orthodoxEasterSunday(year: Int): Calendar {
    val a = year % 4
    val b = year % 7
    val c = year % 19
    val d = (19 * c + 15) % 30
    val e = (2 * a + 4 * b - d + 34) % 7
    val julianMonth = (d + e + 114) / 31
    val julianDay = ((d + e + 114) % 31) + 1
    val delta = julianToGregorianDelta(year)

    return gregorianCalendar(year, julianMonth, julianDay).apply {
        add(Calendar.DAY_OF_YEAR, delta)
    }
}

private fun gregorianCalendar(year: Int, month: Int, day: Int): Calendar {
    return GregorianCalendar(year, month - 1, day).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

private fun julianToGregorianDelta(year: Int): Int {
    return year / 100 - year / 400 - 2
}
