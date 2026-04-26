package com.etnobulgaria.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.etnobulgaria.app.ui.AppLanguage
import com.etnobulgaria.app.ui.AppThemeMode
import com.etnobulgaria.app.ui.AudioClipInfo
import com.etnobulgaria.app.ui.CalendarTraditionSpotlight
import com.etnobulgaria.app.ui.CostumeCategory
import com.etnobulgaria.app.ui.CostumeElementInfo
import com.etnobulgaria.app.ui.CostumeElementType
import com.etnobulgaria.app.ui.CostumeHotspot
import com.etnobulgaria.app.ui.CostumeInfo
import com.etnobulgaria.app.ui.CulturalPlaceInfo
import com.etnobulgaria.app.ui.EthnoArchiveEntry
import com.etnobulgaria.app.ui.DailyLandmarkQuestionSeed
import com.etnobulgaria.app.ui.DailyQuestionScoreState
import com.etnobulgaria.app.ui.EthnoDiaryEntry
import com.etnobulgaria.app.ui.EthnoPassportState
import com.etnobulgaria.app.ui.EthnoRegion
import com.etnobulgaria.app.ui.EthnoRegionId
import com.etnobulgaria.app.ui.EthnoRegionPreview
import com.etnobulgaria.app.ui.EmbroideryInfo
import com.etnobulgaria.app.ui.HolidayCalendarEntry
import com.etnobulgaria.app.ui.HolidayDateRule
import com.etnobulgaria.app.ui.HolidayRuleType
import com.etnobulgaria.app.ui.InstrumentInfo
import com.etnobulgaria.app.ui.LocalizedText
import com.etnobulgaria.app.ui.MusicInfo
import com.etnobulgaria.app.ui.QuizQuestion
import com.etnobulgaria.app.ui.RegionGamificationInfo
import com.etnobulgaria.app.ui.RegionPassportStatus
import com.etnobulgaria.app.ui.TraditionInfo
import com.etnobulgaria.app.ui.ethnoArchiveEntries
import com.etnobulgaria.app.ui.calendarTraditionSpotlights
import com.etnobulgaria.app.ui.dailyLandmarkQuestionBank
import com.etnobulgaria.app.ui.dailyObservanceEntries
import com.etnobulgaria.app.ui.holidayCalendarEntries
import com.etnobulgaria.app.ui.regionCatalog
import org.json.JSONArray
import org.json.JSONObject

private const val DATABASE_NAME = "ethno_bulgaria.db"
private const val DATABASE_VERSION = 2

private const val TABLE_REGIONS = "regions"
private const val TABLE_CALENDAR_ENTRIES = "calendar_entries"
private const val TABLE_CALENDAR_SPOTLIGHTS = "calendar_spotlights"
private const val TABLE_DAILY_QUESTIONS = "daily_questions"
private const val TABLE_ETHNO_ARCHIVE = "ethno_archive_entries"
private const val TABLE_SETTINGS = "settings"
private const val TABLE_PASSPORT_STATUS = "passport_status"
private const val TABLE_DAILY_QUESTION_STATE = "daily_question_state"
private const val TABLE_DIARY_ENTRIES = "diary_entries"

private const val CALENDAR_KIND_HOLIDAY = "holiday"
private const val CALENDAR_KIND_OBSERVANCE = "observance"

private const val SETTING_APP_LANGUAGE = "app_language"
private const val SETTING_APP_THEME = "app_theme"
private const val SETTING_HOLIDAY_NOTIFICATIONS = "holiday_notifications_enabled"
private const val SETTING_LEGACY_IMPORT_DONE = "legacy_import_done"

private const val LEGACY_APP_PREFERENCES = "ethno_bulgaria_preferences"
private const val LEGACY_APP_LANGUAGE_KEY = "app_language"
private const val LEGACY_APP_THEME_KEY = "app_theme_mode"
private const val LEGACY_NOTIFICATION_PREFERENCES = "ethno_holiday_notifications"
private const val LEGACY_NOTIFICATION_ENABLED_KEY = "enabled"
private const val LEGACY_PASSPORT_PREFERENCES = "ethno_passport_preferences"
private const val LEGACY_PASSPORT_STATE_KEY = "ethno_passport_state"
private const val LEGACY_DAILY_QUESTION_PREFERENCES = "daily_question_preferences"
private const val LEGACY_DAILY_QUESTION_TOTAL_POINTS_KEY = "daily_question_total_points"
private const val LEGACY_DAILY_QUESTION_ANSWERED_DATE_KEY = "daily_question_answered_date"
private const val LEGACY_DAILY_QUESTION_ANSWERED_CORRECT_KEY = "daily_question_answered_correct"
private const val LEGACY_DIARY_PREFERENCES = "ethno_diary_preferences"
private const val LEGACY_DIARY_ENTRIES_KEY = "ethno_diary_entries"

data class EthnoContentSnapshot(
    val regionCatalog: List<EthnoRegionPreview>,
    val holidayEntries: List<HolidayCalendarEntry>,
    val observanceEntries: List<HolidayCalendarEntry>,
    val calendarSpotlights: List<CalendarTraditionSpotlight>,
    val dailyQuestionBank: List<DailyLandmarkQuestionSeed>,
    val archiveEntries: List<EthnoArchiveEntry>,
)

object AppSettingsRepository {
    fun readLanguage(context: Context): AppLanguage {
        return EthnoSQLiteHelper.getInstance(context).readLanguage()
    }

    fun writeLanguage(context: Context, language: AppLanguage) {
        EthnoSQLiteHelper.getInstance(context).writeLanguage(language)
    }

    fun readThemeMode(context: Context): AppThemeMode {
        return EthnoSQLiteHelper.getInstance(context).readThemeMode()
    }

    fun writeThemeMode(context: Context, themeMode: AppThemeMode) {
        EthnoSQLiteHelper.getInstance(context).writeThemeMode(themeMode)
    }

    fun areHolidayNotificationsEnabled(context: Context): Boolean {
        return EthnoSQLiteHelper.getInstance(context).areHolidayNotificationsEnabled()
    }

    fun setHolidayNotificationsEnabled(context: Context, enabled: Boolean) {
        EthnoSQLiteHelper.getInstance(context).setHolidayNotificationsEnabled(enabled)
    }
}

object EthnoContentRepository {
    fun loadSnapshot(context: Context): EthnoContentSnapshot {
        return EthnoSQLiteHelper.getInstance(context).loadContentSnapshot()
    }

    fun loadRegionCatalog(context: Context): List<EthnoRegionPreview> {
        return EthnoSQLiteHelper.getInstance(context).loadRegionCatalog()
    }

    fun loadHolidayEntries(context: Context): List<HolidayCalendarEntry> {
        return EthnoSQLiteHelper.getInstance(context).loadCalendarEntries(CALENDAR_KIND_HOLIDAY)
    }

    fun loadObservanceEntries(context: Context): List<HolidayCalendarEntry> {
        return EthnoSQLiteHelper.getInstance(context).loadCalendarEntries(CALENDAR_KIND_OBSERVANCE)
    }

    fun loadCalendarSpotlights(context: Context): List<CalendarTraditionSpotlight> {
        return EthnoSQLiteHelper.getInstance(context).loadCalendarSpotlights()
    }

    fun loadDailyQuestionBank(context: Context): List<DailyLandmarkQuestionSeed> {
        return EthnoSQLiteHelper.getInstance(context).loadDailyQuestionBank()
    }

    fun loadArchiveEntries(context: Context): List<EthnoArchiveEntry> {
        return EthnoSQLiteHelper.getInstance(context).loadArchiveEntries()
    }
}

internal class EthnoSQLiteHelper private constructor(
    private val appContext: Context,
) : SQLiteOpenHelper(appContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        @Volatile
        private var instance: EthnoSQLiteHelper? = null

        fun getInstance(context: Context): EthnoSQLiteHelper {
            return instance ?: synchronized(this) {
                instance ?: EthnoSQLiteHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    @Volatile
    private var prepared = false

    override fun onCreate(db: SQLiteDatabase) {
        createRegionsTable(db)
        createCalendarEntriesTable(db)
        createCalendarSpotlightsTable(db)
        createDailyQuestionsTable(db)
        createEthnoArchiveTable(db)
        createSettingsTable(db)
        createPassportStatusTable(db)
        createDailyQuestionStateTable(db)
        createDiaryEntriesTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            createEthnoArchiveTable(db)
        }
    }

    private fun createRegionsTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_REGIONS (
                region_id TEXT PRIMARY KEY,
                display_order INTEGER NOT NULL,
                name_bg TEXT NOT NULL,
                name_en TEXT NOT NULL,
                highlight_bg TEXT NOT NULL,
                highlight_en TEXT NOT NULL,
                region_json TEXT NOT NULL
            )
            """.trimIndent(),
        )
    }

    private fun createCalendarEntriesTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_CALENDAR_ENTRIES (
                entry_id TEXT PRIMARY KEY,
                entry_kind TEXT NOT NULL,
                display_order INTEGER NOT NULL,
                entry_json TEXT NOT NULL
            )
            """.trimIndent(),
        )
    }

    private fun createCalendarSpotlightsTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_CALENDAR_SPOTLIGHTS (
                spotlight_id TEXT PRIMARY KEY,
                display_order INTEGER NOT NULL,
                spotlight_json TEXT NOT NULL
            )
            """.trimIndent(),
        )
    }

    private fun createDailyQuestionsTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_DAILY_QUESTIONS (
                question_id TEXT PRIMARY KEY,
                display_order INTEGER NOT NULL,
                question_json TEXT NOT NULL
            )
            """.trimIndent(),
        )
    }

    private fun createEthnoArchiveTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_ETHNO_ARCHIVE (
                entry_id TEXT PRIMARY KEY,
                display_order INTEGER NOT NULL,
                unlock_points INTEGER NOT NULL,
                entry_json TEXT NOT NULL
            )
            """.trimIndent(),
        )
    }

    private fun createSettingsTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_SETTINGS (
                setting_key TEXT PRIMARY KEY,
                setting_value TEXT NOT NULL
            )
            """.trimIndent(),
        )
    }

    private fun createPassportStatusTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_PASSPORT_STATUS (
                region_id TEXT PRIMARY KEY,
                viewed INTEGER NOT NULL DEFAULT 0,
                best_quiz_score INTEGER,
                question_count INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent(),
        )
    }

    private fun createDailyQuestionStateTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_DAILY_QUESTION_STATE (
                id INTEGER PRIMARY KEY CHECK(id = 1),
                total_points INTEGER NOT NULL DEFAULT 0,
                answered_date_key TEXT,
                answered_correct INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent(),
        )
    }

    private fun createDiaryEntriesTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_DIARY_ENTRIES (
                entry_id TEXT PRIMARY KEY,
                created_at_millis INTEGER NOT NULL,
                note TEXT NOT NULL,
                photo_path TEXT
            )
            """.trimIndent(),
        )
    }

    fun loadContentSnapshot(): EthnoContentSnapshot {
        ensurePrepared()
        return EthnoContentSnapshot(
            regionCatalog = loadRegionCatalog(),
            holidayEntries = loadCalendarEntries(CALENDAR_KIND_HOLIDAY),
            observanceEntries = loadCalendarEntries(CALENDAR_KIND_OBSERVANCE),
            calendarSpotlights = loadCalendarSpotlights(),
            dailyQuestionBank = loadDailyQuestionBank(),
            archiveEntries = loadArchiveEntries(),
        )
    }

    fun loadRegionCatalog(): List<EthnoRegionPreview> {
        ensurePrepared()
        return readableDatabase.query(
            TABLE_REGIONS,
            arrayOf("region_id", "name_bg", "name_en", "highlight_bg", "highlight_en", "region_json"),
            null,
            null,
            null,
            null,
            "display_order ASC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    val regionId = EthnoRegionId.valueOf(cursor.getString("region_id"))
                    add(
                        EthnoRegionPreview(
                            id = regionId,
                            name = LocalizedText(
                                bg = cursor.getString("name_bg"),
                                en = cursor.getString("name_en"),
                            ),
                            highlight = LocalizedText(
                                bg = cursor.getString("highlight_bg"),
                                en = cursor.getString("highlight_en"),
                            ),
                            availableRegion = cursor.getString("region_json").toRegion(),
                        ),
                    )
                }
            }
        }
    }

    fun loadCalendarEntries(kind: String): List<HolidayCalendarEntry> {
        ensurePrepared()
        return readableDatabase.query(
            TABLE_CALENDAR_ENTRIES,
            arrayOf("entry_json"),
            "entry_kind = ?",
            arrayOf(kind),
            null,
            null,
            "display_order ASC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(cursor.getString("entry_json").toHolidayCalendarEntry())
                }
            }
        }
    }

    fun loadCalendarSpotlights(): List<CalendarTraditionSpotlight> {
        ensurePrepared()
        return readableDatabase.query(
            TABLE_CALENDAR_SPOTLIGHTS,
            arrayOf("spotlight_json"),
            null,
            null,
            null,
            null,
            "display_order ASC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(cursor.getString("spotlight_json").toCalendarTraditionSpotlight())
                }
            }
        }
    }

    fun loadDailyQuestionBank(): List<DailyLandmarkQuestionSeed> {
        ensurePrepared()
        return readableDatabase.query(
            TABLE_DAILY_QUESTIONS,
            arrayOf("question_json"),
            null,
            null,
            null,
            null,
            "display_order ASC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(cursor.getString("question_json").toDailyLandmarkQuestionSeed())
                }
            }
        }
    }

    fun loadArchiveEntries(): List<EthnoArchiveEntry> {
        ensurePrepared()
        return readableDatabase.query(
            TABLE_ETHNO_ARCHIVE,
            arrayOf("entry_json"),
            null,
            null,
            null,
            null,
            "display_order ASC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(cursor.getString("entry_json").toEthnoArchiveEntry())
                }
            }
        }
    }

    fun readLanguage(): AppLanguage {
        ensurePrepared()
        return AppLanguage.fromCode(readSetting(SETTING_APP_LANGUAGE))
    }

    fun writeLanguage(language: AppLanguage) {
        ensurePrepared()
        writeSetting(SETTING_APP_LANGUAGE, language.code)
    }

    fun readThemeMode(): AppThemeMode {
        ensurePrepared()
        return AppThemeMode.fromPreferenceValue(readSetting(SETTING_APP_THEME))
    }

    fun writeThemeMode(themeMode: AppThemeMode) {
        ensurePrepared()
        writeSetting(SETTING_APP_THEME, themeMode.preferenceValue)
    }

    fun areHolidayNotificationsEnabled(): Boolean {
        ensurePrepared()
        return readSetting(SETTING_HOLIDAY_NOTIFICATIONS) == "1"
    }

    fun setHolidayNotificationsEnabled(enabled: Boolean) {
        ensurePrepared()
        writeSetting(SETTING_HOLIDAY_NOTIFICATIONS, if (enabled) "1" else "0")
    }

    fun loadPassportState(): EthnoPassportState {
        ensurePrepared()
        return readableDatabase.query(
            TABLE_PASSPORT_STATUS,
            arrayOf("region_id", "viewed", "best_quiz_score", "question_count"),
            null,
            null,
            null,
            null,
            "region_id ASC",
        ).use { cursor ->
            val regions = buildMap {
                while (cursor.moveToNext()) {
                    val regionId = EthnoRegionId.valueOf(cursor.getString("region_id"))
                    val bestQuizScore = if (cursor.isNull(cursor.getColumnIndexOrThrow("best_quiz_score"))) {
                        null
                    } else {
                        cursor.getInt(cursor.getColumnIndexOrThrow("best_quiz_score"))
                    }
                    put(
                        regionId,
                        RegionPassportStatus(
                            viewed = cursor.getInt(cursor.getColumnIndexOrThrow("viewed")) == 1,
                            bestQuizScore = bestQuizScore,
                            questionCount = cursor.getInt(cursor.getColumnIndexOrThrow("question_count")),
                        ),
                    )
                }
            }
            EthnoPassportState(regions)
        }
    }

    fun markRegionViewed(regionId: EthnoRegionId): EthnoPassportState {
        ensurePrepared()
        val currentStatus = loadPassportState().regionStatus(regionId)
        upsertPassportStatus(
            regionId = regionId,
            status = currentStatus.copy(viewed = true),
        )
        return loadPassportState()
    }

    fun recordQuizResult(
        regionId: EthnoRegionId,
        score: Int,
        questionCount: Int,
    ): EthnoPassportState {
        ensurePrepared()
        val currentStatus = loadPassportState().regionStatus(regionId)
        upsertPassportStatus(
            regionId = regionId,
            status = currentStatus.copy(
                viewed = true,
                bestQuizScore = maxOf(currentStatus.bestQuizScore ?: 0, score),
                questionCount = questionCount,
            ),
        )
        return loadPassportState()
    }

    fun loadDailyQuestionState(): DailyQuestionScoreState {
        ensurePrepared()
        return readableDatabase.query(
            TABLE_DAILY_QUESTION_STATE,
            arrayOf("total_points", "answered_date_key", "answered_correct"),
            "id = 1",
            null,
            null,
            null,
            null,
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                DailyQuestionScoreState(
                    totalPoints = cursor.getInt(cursor.getColumnIndexOrThrow("total_points")),
                    answeredDateKey = cursor.getString(cursor.getColumnIndexOrThrow("answered_date_key")),
                    answeredCorrect = cursor.getInt(cursor.getColumnIndexOrThrow("answered_correct")) == 1,
                )
            } else {
                DailyQuestionScoreState()
            }
        }
    }

    fun recordDailyAnswer(
        dateKey: String,
        isCorrect: Boolean,
    ): DailyQuestionScoreState {
        ensurePrepared()
        val current = loadDailyQuestionState()
        if (current.hasAnswered(dateKey)) return current

        val updated = DailyQuestionScoreState(
            totalPoints = current.totalPoints + if (isCorrect) 1 else 0,
            answeredDateKey = dateKey,
            answeredCorrect = isCorrect,
        )
        writableDatabase.insertWithOnConflict(
            TABLE_DAILY_QUESTION_STATE,
            null,
            ContentValues().apply {
                put("id", 1)
                put("total_points", updated.totalPoints)
                put("answered_date_key", updated.answeredDateKey)
                put("answered_correct", if (updated.answeredCorrect) 1 else 0)
            },
            SQLiteDatabase.CONFLICT_REPLACE,
        )
        return updated
    }

    fun loadDiaryEntries(): List<EthnoDiaryEntry> {
        ensurePrepared()
        return readableDatabase.query(
            TABLE_DIARY_ENTRIES,
            arrayOf("entry_id", "created_at_millis", "note", "photo_path"),
            null,
            null,
            null,
            null,
            "created_at_millis DESC",
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        EthnoDiaryEntry(
                            id = cursor.getString("entry_id"),
                            createdAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow("created_at_millis")),
                            note = cursor.getString("note"),
                            photoPath = cursor.getString(cursor.getColumnIndexOrThrow("photo_path")),
                        ),
                    )
                }
            }
        }
    }

    fun saveDiaryEntry(entry: EthnoDiaryEntry) {
        ensurePrepared()
        writableDatabase.insertWithOnConflict(
            TABLE_DIARY_ENTRIES,
            null,
            ContentValues().apply {
                put("entry_id", entry.id)
                put("created_at_millis", entry.createdAtMillis)
                put("note", entry.note)
                put("photo_path", entry.photoPath)
            },
            SQLiteDatabase.CONFLICT_REPLACE,
        )
    }

    @Synchronized
    private fun ensurePrepared() {
        if (prepared) return
        val db = writableDatabase
        db.beginTransaction()
        try {
            syncStaticContent(db)
            migrateLegacyStateIfNeeded(db)
            ensureDefaultSettings(db)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        prepared = true
    }

    private fun syncStaticContent(db: SQLiteDatabase) {
        db.delete(TABLE_REGIONS, null, null)
        db.delete(TABLE_CALENDAR_ENTRIES, null, null)
        db.delete(TABLE_CALENDAR_SPOTLIGHTS, null, null)
        db.delete(TABLE_DAILY_QUESTIONS, null, null)
        db.delete(TABLE_ETHNO_ARCHIVE, null, null)

        regionCatalog.forEachIndexed { index, preview ->
            db.insert(
                TABLE_REGIONS,
                null,
                ContentValues().apply {
                    put("region_id", preview.id.name)
                    put("display_order", index)
                    put("name_bg", preview.name.bg)
                    put("name_en", preview.name.en)
                    put("highlight_bg", preview.highlight.bg)
                    put("highlight_en", preview.highlight.en)
                    put("region_json", preview.availableRegion?.toJsonString() ?: "")
                },
            )
        }

        holidayCalendarEntries.forEachIndexed { index, entry ->
            insertCalendarEntry(db, entry, CALENDAR_KIND_HOLIDAY, index)
        }
        dailyObservanceEntries.forEachIndexed { index, entry ->
            insertCalendarEntry(db, entry, CALENDAR_KIND_OBSERVANCE, index)
        }
        calendarTraditionSpotlights.forEachIndexed { index, spotlight ->
            db.insert(
                TABLE_CALENDAR_SPOTLIGHTS,
                null,
                ContentValues().apply {
                    put("spotlight_id", "spotlight_$index")
                    put("display_order", index)
                    put("spotlight_json", spotlight.toJsonString())
                },
            )
        }
        dailyLandmarkQuestionBank.forEachIndexed { index, question ->
            db.insert(
                TABLE_DAILY_QUESTIONS,
                null,
                ContentValues().apply {
                    put("question_id", question.id)
                    put("display_order", index)
                    put("question_json", question.toJsonString())
                },
            )
        }
        ethnoArchiveEntries.forEachIndexed { index, entry ->
            db.insert(
                TABLE_ETHNO_ARCHIVE,
                null,
                ContentValues().apply {
                    put("entry_id", entry.id)
                    put("display_order", index)
                    put("unlock_points", entry.unlockPoints)
                    put("entry_json", entry.toJsonString())
                },
            )
        }
    }

    private fun insertCalendarEntry(
        db: SQLiteDatabase,
        entry: HolidayCalendarEntry,
        kind: String,
        index: Int,
    ) {
        db.insert(
            TABLE_CALENDAR_ENTRIES,
            null,
            ContentValues().apply {
                put("entry_id", entry.id)
                put("entry_kind", kind)
                put("display_order", index)
                put("entry_json", entry.toJsonString())
            },
        )
    }

    private fun ensureDefaultSettings(db: SQLiteDatabase) {
        if (readSetting(db, SETTING_APP_LANGUAGE) == null) {
            writeSetting(db, SETTING_APP_LANGUAGE, AppLanguage.BG.code)
        }
        if (readSetting(db, SETTING_APP_THEME) == null) {
            writeSetting(db, SETTING_APP_THEME, AppThemeMode.LIGHT.preferenceValue)
        }
        if (readSetting(db, SETTING_HOLIDAY_NOTIFICATIONS) == null) {
            writeSetting(db, SETTING_HOLIDAY_NOTIFICATIONS, "0")
        }
    }

    private fun migrateLegacyStateIfNeeded(db: SQLiteDatabase) {
        if (readSetting(db, SETTING_LEGACY_IMPORT_DONE) == "1") return

        migrateLegacyAppSettings(db)
        migrateLegacyNotifications(db)
        migrateLegacyPassport(db)
        migrateLegacyDailyQuestionState(db)
        migrateLegacyDiaryEntries(db)

        writeSetting(db, SETTING_LEGACY_IMPORT_DONE, "1")
    }

    private fun migrateLegacyAppSettings(db: SQLiteDatabase) {
        val preferences = appContext.getSharedPreferences(LEGACY_APP_PREFERENCES, Context.MODE_PRIVATE)
        val legacyLanguage = preferences.getString(LEGACY_APP_LANGUAGE_KEY, null)
        val legacyTheme = preferences.getString(LEGACY_APP_THEME_KEY, null)

        if (legacyLanguage != null && readSetting(db, SETTING_APP_LANGUAGE) == null) {
            writeSetting(db, SETTING_APP_LANGUAGE, legacyLanguage)
        }
        if (legacyTheme != null && readSetting(db, SETTING_APP_THEME) == null) {
            writeSetting(db, SETTING_APP_THEME, legacyTheme)
        }
    }

    private fun migrateLegacyNotifications(db: SQLiteDatabase) {
        val preferences = appContext.getSharedPreferences(LEGACY_NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE)
        if (preferences.contains(LEGACY_NOTIFICATION_ENABLED_KEY) &&
            readSetting(db, SETTING_HOLIDAY_NOTIFICATIONS) == null
        ) {
            writeSetting(
                db,
                SETTING_HOLIDAY_NOTIFICATIONS,
                if (preferences.getBoolean(LEGACY_NOTIFICATION_ENABLED_KEY, false)) "1" else "0",
            )
        }
    }

    private fun migrateLegacyPassport(db: SQLiteDatabase) {
        val hasPassportRows = DatabaseUtils.countRows(db, TABLE_PASSPORT_STATUS) > 0
        if (hasPassportRows) return

        val preferences = appContext.getSharedPreferences(LEGACY_PASSPORT_PREFERENCES, Context.MODE_PRIVATE)
        val rawJson = preferences.getString(LEGACY_PASSPORT_STATE_KEY, "[]") ?: "[]"
        val array = JSONArray(rawJson)

        repeat(array.length()) { index ->
            val item = array.optJSONObject(index) ?: return@repeat
            val regionId = item.optString("regionId").takeIf { it.isNotBlank() } ?: return@repeat
            db.insertWithOnConflict(
                TABLE_PASSPORT_STATUS,
                null,
                ContentValues().apply {
                    put("region_id", regionId)
                    put("viewed", if (item.optBoolean("viewed", false)) 1 else 0)
                    if (item.has("bestQuizScore")) {
                        put("best_quiz_score", item.optInt("bestQuizScore"))
                    } else {
                        putNull("best_quiz_score")
                    }
                    put("question_count", item.optInt("questionCount", 0))
                },
                SQLiteDatabase.CONFLICT_REPLACE,
            )
        }
    }

    private fun migrateLegacyDailyQuestionState(db: SQLiteDatabase) {
        val hasDailyState = DatabaseUtils.countRows(db, TABLE_DAILY_QUESTION_STATE) > 0
        if (hasDailyState) return

        val preferences = appContext.getSharedPreferences(LEGACY_DAILY_QUESTION_PREFERENCES, Context.MODE_PRIVATE)
        val hasAnyLegacyValue = preferences.contains(LEGACY_DAILY_QUESTION_TOTAL_POINTS_KEY) ||
            preferences.contains(LEGACY_DAILY_QUESTION_ANSWERED_DATE_KEY) ||
            preferences.contains(LEGACY_DAILY_QUESTION_ANSWERED_CORRECT_KEY)
        if (!hasAnyLegacyValue) return

        db.insertWithOnConflict(
            TABLE_DAILY_QUESTION_STATE,
            null,
            ContentValues().apply {
                put("id", 1)
                put("total_points", preferences.getInt(LEGACY_DAILY_QUESTION_TOTAL_POINTS_KEY, 0))
                put("answered_date_key", preferences.getString(LEGACY_DAILY_QUESTION_ANSWERED_DATE_KEY, null))
                put(
                    "answered_correct",
                    if (preferences.getBoolean(LEGACY_DAILY_QUESTION_ANSWERED_CORRECT_KEY, false)) {
                        1
                    } else {
                        0
                    },
                )
            },
            SQLiteDatabase.CONFLICT_REPLACE,
        )
    }

    private fun migrateLegacyDiaryEntries(db: SQLiteDatabase) {
        val hasDiaryEntries = DatabaseUtils.countRows(db, TABLE_DIARY_ENTRIES) > 0
        if (hasDiaryEntries) return

        val preferences = appContext.getSharedPreferences(LEGACY_DIARY_PREFERENCES, Context.MODE_PRIVATE)
        val rawJson = preferences.getString(LEGACY_DIARY_ENTRIES_KEY, "[]") ?: "[]"
        val array = JSONArray(rawJson)

        repeat(array.length()) { index ->
            val item = array.optJSONObject(index) ?: return@repeat
            db.insertWithOnConflict(
                TABLE_DIARY_ENTRIES,
                null,
                ContentValues().apply {
                    put("entry_id", item.optString("id"))
                    put("created_at_millis", item.optLong("createdAtMillis"))
                    put("note", item.optString("note"))
                    put("photo_path", item.optString("photoPath").takeIf { it.isNotBlank() })
                },
                SQLiteDatabase.CONFLICT_REPLACE,
            )
        }
    }

    private fun upsertPassportStatus(
        regionId: EthnoRegionId,
        status: RegionPassportStatus,
    ) {
        writableDatabase.insertWithOnConflict(
            TABLE_PASSPORT_STATUS,
            null,
            ContentValues().apply {
                put("region_id", regionId.name)
                put("viewed", if (status.viewed) 1 else 0)
                if (status.bestQuizScore != null) {
                    put("best_quiz_score", status.bestQuizScore)
                } else {
                    putNull("best_quiz_score")
                }
                put("question_count", status.questionCount)
            },
            SQLiteDatabase.CONFLICT_REPLACE,
        )
    }

    private fun readSetting(key: String): String? {
        ensurePrepared()
        return readSetting(readableDatabase, key)
    }

    private fun readSetting(db: SQLiteDatabase, key: String): String? {
        return db.query(
            TABLE_SETTINGS,
            arrayOf("setting_value"),
            "setting_key = ?",
            arrayOf(key),
            null,
            null,
            null,
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getString("setting_value") else null
        }
    }

    private fun writeSetting(key: String, value: String) {
        writableDatabase.insertWithOnConflict(
            TABLE_SETTINGS,
            null,
            ContentValues().apply {
                put("setting_key", key)
                put("setting_value", value)
            },
            SQLiteDatabase.CONFLICT_REPLACE,
        )
    }

    private fun writeSetting(db: SQLiteDatabase, key: String, value: String) {
        db.insertWithOnConflict(
            TABLE_SETTINGS,
            null,
            ContentValues().apply {
                put("setting_key", key)
                put("setting_value", value)
            },
            SQLiteDatabase.CONFLICT_REPLACE,
        )
    }
}

private object DatabaseUtils {
    fun countRows(db: SQLiteDatabase, tableName: String): Long {
        return db.rawQuery("SELECT COUNT(*) FROM $tableName", null).use { cursor ->
            if (cursor.moveToFirst()) cursor.getLong(0) else 0L
        }
    }
}

private fun Cursor.getString(columnName: String): String {
    return getString(getColumnIndexOrThrow(columnName))
}

private fun JSONObject.putLocalizedText(key: String, value: LocalizedText) {
    put(key, value.toJson())
}

private fun LocalizedText.toJson(): JSONObject {
    return JSONObject()
        .put("bg", bg)
        .put("en", en)
}

private fun JSONObject.toLocalizedText(): LocalizedText {
    return LocalizedText(
        bg = optString("bg"),
        en = optString("en"),
    )
}

private fun List<LocalizedText>.toLocalizedTextJsonArray(): JSONArray {
    return JSONArray().apply {
        forEach { put(it.toJson()) }
    }
}

private fun JSONArray.toLocalizedTextList(): List<LocalizedText> {
    return buildList {
        repeat(length()) { index ->
            add(getJSONObject(index).toLocalizedText())
        }
    }
}

private fun List<CostumeElementInfo>.toCostumeElementJsonArray(): JSONArray {
    return JSONArray().apply {
        forEach { element ->
            put(
                JSONObject()
                    .put("id", element.id)
                    .put("type", element.type.name)
                    .put("title", element.title.toJson())
                    .put("summary", element.summary.toJson()),
            )
        }
    }
}

private fun JSONArray.toCostumeElementInfoList(): List<CostumeElementInfo> {
    return buildList {
        repeat(length()) { index ->
            val item = getJSONObject(index)
            add(
                CostumeElementInfo(
                    id = item.getString("id"),
                    type = CostumeElementType.valueOf(item.getString("type")),
                    title = item.getJSONObject("title").toLocalizedText(),
                    summary = item.getJSONObject("summary").toLocalizedText(),
                ),
            )
        }
    }
}

private fun List<CostumeHotspot>.toHotspotJsonArray(): JSONArray {
    return JSONArray().apply {
        forEach { hotspot ->
            put(
                JSONObject()
                    .put("elementId", hotspot.elementId)
                    .put("relativeX", hotspot.relativeX.toDouble())
                    .put("relativeY", hotspot.relativeY.toDouble()),
            )
        }
    }
}

private fun JSONArray.toCostumeHotspots(): List<CostumeHotspot> {
    return buildList {
        repeat(length()) { index ->
            val item = getJSONObject(index)
            add(
                CostumeHotspot(
                    elementId = item.getString("elementId"),
                    relativeX = item.getDouble("relativeX").toFloat(),
                    relativeY = item.getDouble("relativeY").toFloat(),
                ),
            )
        }
    }
}

private fun CostumeInfo.toJson(): JSONObject {
    return JSONObject()
        .put("title", title.toJson())
        .put("shortDescription", shortDescription.toJson())
        .put("elements", elements.toLocalizedTextJsonArray())
        .put("category", category.name)
        .put("imageAssetName", imageAssetName)
        .put("elementDetails", elementDetails.toCostumeElementJsonArray())
        .put("hotspots", hotspots.toHotspotJsonArray())
}

private fun JSONObject.toCostumeInfo(): CostumeInfo {
    return CostumeInfo(
        title = getJSONObject("title").toLocalizedText(),
        shortDescription = getJSONObject("shortDescription").toLocalizedText(),
        elements = getJSONArray("elements").toLocalizedTextList(),
        category = CostumeCategory.valueOf(getString("category")),
        imageAssetName = optString("imageAssetName").takeIf { it.isNotBlank() },
        elementDetails = getJSONArray("elementDetails").toCostumeElementInfoList(),
        hotspots = getJSONArray("hotspots").toCostumeHotspots(),
    )
}

private fun EmbroideryInfo.toJson(): JSONObject {
    return JSONObject()
        .put("motif", motif.toJson())
        .put("meaning", meaning.toJson())
        .put("colors", colors.toLocalizedTextJsonArray())
        .put("imageAssetName", imageAssetName)
}

private fun JSONObject.toEmbroideryInfo(): EmbroideryInfo {
    return EmbroideryInfo(
        motif = getJSONObject("motif").toLocalizedText(),
        meaning = getJSONObject("meaning").toLocalizedText(),
        colors = getJSONArray("colors").toLocalizedTextList(),
        imageAssetName = optString("imageAssetName").takeIf { it.isNotBlank() },
    )
}

private fun TraditionInfo.toJson(): JSONObject {
    return JSONObject()
        .put("title", title.toJson())
        .put("season", season.toJson())
        .put("summary", summary.toJson())
        .put("holidayIds", JSONArray(holidayIds))
}

private fun JSONObject.toTraditionInfo(): TraditionInfo {
    return TraditionInfo(
        title = getJSONObject("title").toLocalizedText(),
        season = getJSONObject("season").toLocalizedText(),
        summary = getJSONObject("summary").toLocalizedText(),
        holidayIds = getJSONArray("holidayIds").toStringList(),
    )
}

private fun InstrumentInfo.toJson(): JSONObject {
    return JSONObject()
        .put("name", name.toJson())
        .put("imageAssetName", imageAssetName)
}

private fun JSONObject.toInstrumentInfo(): InstrumentInfo {
    return InstrumentInfo(
        name = getJSONObject("name").toLocalizedText(),
        imageAssetName = optString("imageAssetName").takeIf { it.isNotBlank() },
    )
}

private fun AudioClipInfo.toJson(): JSONObject {
    return JSONObject()
        .put("title", title.toJson())
        .put("assetPath", assetPath)
        .put("durationSeconds", durationSeconds)
        .put("note", note.toJson())
}

private fun JSONObject.toAudioClipInfo(): AudioClipInfo {
    return AudioClipInfo(
        title = getJSONObject("title").toLocalizedText(),
        assetPath = optString("assetPath").takeIf { it.isNotBlank() },
        durationSeconds = optInt("durationSeconds", 0),
        note = getJSONObject("note").toLocalizedText(),
    )
}

private fun MusicInfo.toJson(): JSONObject {
    return JSONObject()
        .put("songExamples", songExamples.toLocalizedTextJsonArray())
        .put("instruments", JSONArray().apply { instruments.forEach { put(it.toJson()) } })
        .put("audioClips", JSONArray().apply { audioClips.forEach { put(it.toJson()) } })
}

private fun JSONObject.toMusicInfo(): MusicInfo {
    return MusicInfo(
        songExamples = getJSONArray("songExamples").toLocalizedTextList(),
        instruments = getJSONArray("instruments").toInstrumentInfoList(),
        audioClips = getJSONArray("audioClips").toAudioClipInfoList(),
    )
}

private fun JSONArray.toInstrumentInfoList(): List<InstrumentInfo> {
    return buildList {
        repeat(length()) { index ->
            add(getJSONObject(index).toInstrumentInfo())
        }
    }
}

private fun JSONArray.toAudioClipInfoList(): List<AudioClipInfo> {
    return buildList {
        repeat(length()) { index ->
            add(getJSONObject(index).toAudioClipInfo())
        }
    }
}

private fun CulturalPlaceInfo.toJson(): JSONObject {
    return JSONObject()
        .put("name", name.toJson())
        .put("type", type.toJson())
        .put("location", location.toJson())
        .put("summary", summary.toJson())
        .put("latitude", latitude)
        .put("longitude", longitude)
        .put("imageAssetName", imageAssetName)
}

private fun JSONObject.toCulturalPlaceInfo(): CulturalPlaceInfo {
    return CulturalPlaceInfo(
        name = getJSONObject("name").toLocalizedText(),
        type = getJSONObject("type").toLocalizedText(),
        location = getJSONObject("location").toLocalizedText(),
        summary = getJSONObject("summary").toLocalizedText(),
        latitude = optDoubleOrNull("latitude"),
        longitude = optDoubleOrNull("longitude"),
        imageAssetName = optString("imageAssetName").takeIf { it.isNotBlank() },
    )
}

private fun QuizQuestion.toJson(): JSONObject {
    return JSONObject()
        .put("question", question.toJson())
        .put("options", options.toLocalizedTextJsonArray())
        .put("correctAnswerIndex", correctAnswerIndex)
        .put("explanation", explanation?.toJson())
}

private fun JSONObject.toQuizQuestion(): QuizQuestion {
    return QuizQuestion(
        question = getJSONObject("question").toLocalizedText(),
        options = getJSONArray("options").toLocalizedTextList(),
        correctAnswerIndex = getInt("correctAnswerIndex"),
        explanation = optJSONObject("explanation")?.toLocalizedText(),
    )
}

private fun RegionGamificationInfo.toJson(): JSONObject {
    return JSONObject()
        .put("stampTitle", stampTitle.toJson())
        .put("stampDescription", stampDescription.toJson())
        .put("badgeTitle", badgeTitle.toJson())
        .put("badgeDescription", badgeDescription.toJson())
        .put("progressDescription", progressDescription.toJson())
}

private fun JSONObject.toRegionGamificationInfo(): RegionGamificationInfo {
    return RegionGamificationInfo(
        stampTitle = getJSONObject("stampTitle").toLocalizedText(),
        stampDescription = getJSONObject("stampDescription").toLocalizedText(),
        badgeTitle = getJSONObject("badgeTitle").toLocalizedText(),
        badgeDescription = getJSONObject("badgeDescription").toLocalizedText(),
        progressDescription = getJSONObject("progressDescription").toLocalizedText(),
    )
}

private fun EthnoRegion.toJsonString(): String {
    return JSONObject()
        .put("id", id.name)
        .put("name", name.toJson())
        .put("subtitle", subtitle.toJson())
        .put("overview", overview.toJson())
        .put("coverage", coverage.toJson())
        .put("visualIdentity", visualIdentity.toJson())
        .put("recognizableFeatures", recognizableFeatures.toLocalizedTextJsonArray())
        .put("costumes", JSONArray().apply { costumes.forEach { put(it.toJson()) } })
        .put("embroideries", JSONArray().apply { embroideries.forEach { put(it.toJson()) } })
        .put("traditions", JSONArray().apply { traditions.forEach { put(it.toJson()) } })
        .put("music", music.toJson())
        .put("places", JSONArray().apply { places.forEach { put(it.toJson()) } })
        .put("quiz", JSONArray().apply { quiz.forEach { put(it.toJson()) } })
        .put("gamification", gamification?.toJson())
        .toString()
}

private fun String.toRegion(): EthnoRegion? {
    if (isBlank()) return null
    val json = JSONObject(this)
    return EthnoRegion(
        id = EthnoRegionId.valueOf(json.getString("id")),
        name = json.getJSONObject("name").toLocalizedText(),
        subtitle = json.getJSONObject("subtitle").toLocalizedText(),
        overview = json.getJSONObject("overview").toLocalizedText(),
        coverage = json.getJSONObject("coverage").toLocalizedText(),
        visualIdentity = json.getJSONObject("visualIdentity").toLocalizedText(),
        recognizableFeatures = json.getJSONArray("recognizableFeatures").toLocalizedTextList(),
        costumes = json.getJSONArray("costumes").toCostumeInfoList(),
        embroideries = json.getJSONArray("embroideries").toEmbroideryInfoList(),
        traditions = json.getJSONArray("traditions").toTraditionInfoList(),
        music = json.getJSONObject("music").toMusicInfo(),
        places = json.getJSONArray("places").toCulturalPlaceInfoList(),
        quiz = json.getJSONArray("quiz").toQuizQuestionList(),
        gamification = json.optJSONObject("gamification")?.toRegionGamificationInfo(),
    )
}

private fun JSONArray.toCostumeInfoList(): List<CostumeInfo> {
    return buildList {
        repeat(length()) { index ->
            add(getJSONObject(index).toCostumeInfo())
        }
    }
}

private fun JSONArray.toEmbroideryInfoList(): List<EmbroideryInfo> {
    return buildList {
        repeat(length()) { index ->
            add(getJSONObject(index).toEmbroideryInfo())
        }
    }
}

private fun JSONArray.toTraditionInfoList(): List<TraditionInfo> {
    return buildList {
        repeat(length()) { index ->
            add(getJSONObject(index).toTraditionInfo())
        }
    }
}

private fun JSONArray.toCulturalPlaceInfoList(): List<CulturalPlaceInfo> {
    return buildList {
        repeat(length()) { index ->
            add(getJSONObject(index).toCulturalPlaceInfo())
        }
    }
}

private fun JSONArray.toQuizQuestionList(): List<QuizQuestion> {
    return buildList {
        repeat(length()) { index ->
            add(getJSONObject(index).toQuizQuestion())
        }
    }
}

private fun HolidayCalendarEntry.toJsonString(): String {
    return JSONObject()
        .put("id", id)
        .put("title", title.toJson())
        .put("summary", summary.toJson())
        .put("rule", rule.toJson())
        .put("nameDays", nameDays.toLocalizedTextJsonArray())
        .put("priority", priority)
        .toString()
}

private fun HolidayDateRule.toJson(): JSONObject {
    return JSONObject()
        .put("ruleType", ruleType.name)
        .put("month", month)
        .put("day", day)
        .put("easterOffsetDays", easterOffsetDays)
}

private fun String.toHolidayCalendarEntry(): HolidayCalendarEntry {
    val json = JSONObject(this)
    return HolidayCalendarEntry(
        id = json.getString("id"),
        title = json.getJSONObject("title").toLocalizedText(),
        summary = json.getJSONObject("summary").toLocalizedText(),
        rule = json.getJSONObject("rule").toHolidayDateRule(),
        nameDays = json.getJSONArray("nameDays").toLocalizedTextList(),
        priority = json.optInt("priority", 0),
    )
}

private fun JSONObject.toHolidayDateRule(): HolidayDateRule {
    return HolidayDateRule(
        ruleType = HolidayRuleType.valueOf(getString("ruleType")),
        month = optIntOrNull("month"),
        day = optIntOrNull("day"),
        easterOffsetDays = optIntOrNull("easterOffsetDays"),
    )
}

private fun CalendarTraditionSpotlight.toJsonString(): String {
    return JSONObject()
        .put("title", title.toJson())
        .put("body", body.toJson())
        .put("startMonth", startMonth)
        .put("startDay", startDay)
        .put("endMonth", endMonth)
        .put("endDay", endDay)
        .toString()
}

private fun String.toCalendarTraditionSpotlight(): CalendarTraditionSpotlight {
    val json = JSONObject(this)
    return CalendarTraditionSpotlight(
        title = json.getJSONObject("title").toLocalizedText(),
        body = json.getJSONObject("body").toLocalizedText(),
        startMonth = json.optInt("startMonth", 1),
        startDay = json.optInt("startDay", 1),
        endMonth = json.optInt("endMonth", 12),
        endDay = json.optInt("endDay", 31),
    )
}

private fun DailyLandmarkQuestionSeed.toJsonString(): String {
    return JSONObject()
        .put("id", id)
        .put("regionName", regionName.toJson())
        .put("imageAssetName", imageAssetName)
        .put("correctAnswer", correctAnswer.toJson())
        .put("distractors", distractors.toLocalizedTextJsonArray())
        .toString()
}

private fun String.toDailyLandmarkQuestionSeed(): DailyLandmarkQuestionSeed {
    val json = JSONObject(this)
    return DailyLandmarkQuestionSeed(
        id = json.getString("id"),
        regionName = json.getJSONObject("regionName").toLocalizedText(),
        imageAssetName = json.getString("imageAssetName"),
        correctAnswer = json.getJSONObject("correctAnswer").toLocalizedText(),
        distractors = json.getJSONArray("distractors").toLocalizedTextList(),
    )
}

private fun EthnoArchiveEntry.toJsonString(): String {
    return JSONObject()
        .put("id", id)
        .put("unlockPoints", unlockPoints)
        .put("title", title.toJson())
        .put("summary", summary.toJson())
        .put("highlights", highlights.toLocalizedTextJsonArray())
        .toString()
}

private fun String.toEthnoArchiveEntry(): EthnoArchiveEntry {
    val json = JSONObject(this)
    return EthnoArchiveEntry(
        id = json.getString("id"),
        unlockPoints = json.getInt("unlockPoints"),
        title = json.getJSONObject("title").toLocalizedText(),
        summary = json.getJSONObject("summary").toLocalizedText(),
        highlights = json.getJSONArray("highlights").toLocalizedTextList(),
    )
}

private fun JSONArray.toStringList(): List<String> {
    return buildList {
        repeat(length()) { index ->
            add(optString(index))
        }
    }
}

private fun JSONObject.optDoubleOrNull(key: String): Double? {
    return if (has(key) && !isNull(key)) optDouble(key) else null
}

private fun JSONObject.optIntOrNull(key: String): Int? {
    return if (has(key) && !isNull(key)) optInt(key) else null
}
