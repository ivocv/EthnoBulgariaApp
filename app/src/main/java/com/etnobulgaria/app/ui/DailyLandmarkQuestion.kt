package com.etnobulgaria.app.ui

import android.content.Context
import com.etnobulgaria.app.data.EthnoSQLiteHelper
import java.util.Calendar
import java.util.Locale

data class DailyLandmarkQuestion(
    val id: String,
    val dateKey: String,
    val regionName: LocalizedText,
    val imageAssetName: String,
    val correctAnswer: LocalizedText,
    val options: List<LocalizedText>,
    val correctAnswerIndex: Int,
)

data class DailyQuestionScoreState(
    val totalPoints: Int = 0,
    val answeredDateKey: String? = null,
    val answeredCorrect: Boolean = false,
) {
    fun hasAnswered(dateKey: String): Boolean = answeredDateKey == dateKey

    fun answeredCorrectFor(dateKey: String): Boolean = hasAnswered(dateKey) && answeredCorrect
}

object DailyQuestionRepository {
    fun loadState(context: Context): DailyQuestionScoreState {
        return EthnoSQLiteHelper.getInstance(context).loadDailyQuestionState()
    }

    fun recordAnswer(
        context: Context,
        dateKey: String,
        isCorrect: Boolean,
    ): DailyQuestionScoreState {
        return EthnoSQLiteHelper.getInstance(context).recordDailyAnswer(dateKey, isCorrect)
    }
}

fun landmarkQuestionOfTheDay(
    questionBank: List<DailyLandmarkQuestionSeed>,
    calendar: Calendar = Calendar.getInstance(),
): DailyLandmarkQuestion {
    val safeBank = questionBank.ifEmpty { dailyLandmarkQuestionBank }
    val seedIndex = (calendar.get(Calendar.YEAR) * 366 + calendar.get(Calendar.DAY_OF_YEAR) - 1)
    val seed = safeBank[seedIndex % safeBank.size]
    val rawOptions = listOf(seed.correctAnswer) + seed.distractors.take(2)
    val rotation = seedIndex % rawOptions.size
    val options = rawOptions.drop(rotation) + rawOptions.take(rotation)

    return DailyLandmarkQuestion(
        id = seed.id,
        dateKey = calendar.dailyQuestionDateKey(),
        regionName = seed.regionName,
        imageAssetName = seed.imageAssetName,
        correctAnswer = seed.correctAnswer,
        options = options,
        correctAnswerIndex = options.indexOf(seed.correctAnswer),
    )
}

data class DailyLandmarkQuestionSeed(
    val id: String,
    val regionName: LocalizedText,
    val imageAssetName: String,
    val correctAnswer: LocalizedText,
    val distractors: List<LocalizedText>,
)

private fun Calendar.dailyQuestionDateKey(): String {
    return String.format(
        Locale.ROOT,
        "%04d-%02d-%02d",
        get(Calendar.YEAR),
        get(Calendar.MONTH) + 1,
        get(Calendar.DAY_OF_MONTH),
    )
}

private fun lt(bg: String, en: String) = LocalizedText(bg = bg, en = en)

val dailyLandmarkQuestionBank = listOf(
    DailyLandmarkQuestionSeed(
        id = "shopska_sofia_museum",
        regionName = lt("Шопска област", "Shopska region"),
        imageAssetName = "daily_q_shopska_sofia_museum.png",
        correctAnswer = lt("Регионален исторически музей - София", "Regional History Museum - Sofia"),
        distractors = listOf(
            lt("Музей на розата", "Museum of the Rose"),
            lt("Етър", "Etar"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "shopska_samokov_museum",
        regionName = lt("Шопска област", "Shopska region"),
        imageAssetName = "daily_q_shopska_samokov_museum.png",
        correctAnswer = lt("Исторически музей - Самоков", "History Museum - Samokov"),
        distractors = listOf(
            lt("Кордопулова къща", "Kordopulova House"),
            lt("Етнографска къща - Добрич", "Ethnographic House - Dobrich"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "shopska_pernik_museum",
        regionName = lt("Шопска област", "Shopska region"),
        imageAssetName = "daily_q_shopska_pernik_museum.png",
        correctAnswer = lt("Регионален исторически музей - Перник", "Regional History Museum - Pernik"),
        distractors = listOf(
            lt("Регионален исторически музей - Русе", "Regional History Museum - Ruse"),
            lt("Регионален етнографски музей - Пловдив", "Regional Ethnographic Museum - Plovdiv"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "thracian_plovdiv_ethnographic",
        regionName = lt("Тракийска област", "Thracian region"),
        imageAssetName = "daily_q_thracian_plovdiv_ethnographic.png",
        correctAnswer = lt("Регионален етнографски музей - Пловдив", "Regional Ethnographic Museum - Plovdiv"),
        distractors = listOf(
            lt("Регионален исторически музей - София", "Regional History Museum - Sofia"),
            lt("Регионален исторически музей „Стою Шишков“", "Stoyu Shishkov Regional History Museum"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "thracian_old_town_plovdiv",
        regionName = lt("Тракийска област", "Thracian region"),
        imageAssetName = "daily_q_thracian_old_town_plovdiv.png",
        correctAnswer = lt("Старинен Пловдив", "Old Town Plovdiv"),
        distractors = listOf(
            lt("Самоводска чаршия", "Samovodska Charshia"),
            lt("Архитектурно-етнографски комплекс „Стария Добрич“", "Old Dobrich Outdoor Museum"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "thracian_rose_museum",
        regionName = lt("Тракийска област", "Thracian region"),
        imageAssetName = "daily_q_thracian_rose_museum.png",
        correctAnswer = lt("Музей на розата", "Museum of the Rose"),
        distractors = listOf(
            lt("Етнографски музей - Широка лъка", "Ethnographic Museum - Shiroka Laka"),
            lt("Исторически музей - Самоков", "History Museum - Samokov"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "severnyashka_etar",
        regionName = lt("Северняшка област", "Severnyashka region"),
        imageAssetName = "daily_q_severnyashka_etar.png",
        correctAnswer = lt("Етър", "Etar"),
        distractors = listOf(
            lt("Етнографски ареален комплекс - Златоград", "Ethnographic Areal Complex - Zlatograd"),
            lt("Старинен Пловдив", "Old Town Plovdiv"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "severnyashka_samovodska_charshia",
        regionName = lt("Северняшка област", "Severnyashka region"),
        imageAssetName = "daily_q_severnyashka_samovodska_charshia.png",
        correctAnswer = lt("Самоводска чаршия", "Samovodska Charshia"),
        distractors = listOf(
            lt("Старинен Пловдив", "Old Town Plovdiv"),
            lt("Велянова къща", "House of Velyan"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "severnyashka_ruse_museum",
        regionName = lt("Северняшка област", "Severnyashka region"),
        imageAssetName = "daily_q_severnyashka_ruse_museum.png",
        correctAnswer = lt("Регионален исторически музей - Русе", "Regional History Museum - Ruse"),
        distractors = listOf(
            lt("Регионален исторически музей - Перник", "Regional History Museum - Pernik"),
            lt("Археологически музей - Сандански", "Archaeological Museum - Sandanski"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "pirin_velyan_house",
        regionName = lt("Пиринска област", "Pirin region"),
        imageAssetName = "daily_q_pirin_velyan_house.png",
        correctAnswer = lt("Велянова къща", "House of Velyan"),
        distractors = listOf(
            lt("Етнографска къща - Добрич", "Ethnographic House - Dobrich"),
            lt("Къща-музей „Неофит Рилски“", "Neofit Rilski House Museum"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "pirin_neofit_rilski_house",
        regionName = lt("Пиринска област", "Pirin region"),
        imageAssetName = "daily_q_pirin_neofit_rilski_house.png",
        correctAnswer = lt("Къща-музей „Неофит Рилски“", "Neofit Rilski House Museum"),
        distractors = listOf(
            lt("Велянова къща", "House of Velyan"),
            lt("Етнографски музей - Широка лъка", "Ethnographic Museum - Shiroka Laka"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "pirin_kordopulova_house",
        regionName = lt("Пиринска област", "Pirin region"),
        imageAssetName = "daily_q_pirin_kordopulova_house.png",
        correctAnswer = lt("Кордопулова къща", "Kordopulova House"),
        distractors = listOf(
            lt("Музей на розата", "Museum of the Rose"),
            lt("Етър", "Etar"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "rhodope_zlatograd_complex",
        regionName = lt("Родопска област", "Rhodope region"),
        imageAssetName = "daily_q_rhodope_zlatograd_complex.png",
        correctAnswer = lt("Етнографски ареален комплекс - Златоград", "Ethnographic Areal Complex - Zlatograd"),
        distractors = listOf(
            lt("Архитектурно-етнографски комплекс „Стария Добрич“", "Old Dobrich Outdoor Museum"),
            lt("Регионален етнографски музей - Пловдив", "Regional Ethnographic Museum - Plovdiv"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "rhodope_stoyu_shishkov_museum",
        regionName = lt("Родопска област", "Rhodope region"),
        imageAssetName = "daily_q_rhodope_stoyu_shishkov_museum.png",
        correctAnswer = lt("Регионален исторически музей „Стою Шишков“", "Stoyu Shishkov Regional History Museum"),
        distractors = listOf(
            lt("Регионален исторически музей - Русе", "Regional History Museum - Ruse"),
            lt("Регионален исторически музей - София", "Regional History Museum - Sofia"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "rhodope_shiroka_laka_museum",
        regionName = lt("Родопска област", "Rhodope region"),
        imageAssetName = "daily_q_rhodope_shiroka_laka_museum.png",
        correctAnswer = lt("Етнографски музей - Широка лъка", "Ethnographic Museum - Shiroka Laka"),
        distractors = listOf(
            lt("Етнографска къща - Добрич", "Ethnographic House - Dobrich"),
            lt("Къща-музей „Неофит Рилски“", "Neofit Rilski House Museum"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "dobrudzha_old_dobrich",
        regionName = lt("Добруджанска област", "Dobrudzha region"),
        imageAssetName = "daily_q_dobrudzha_old_dobrich.png",
        correctAnswer = lt("Архитектурно-етнографски комплекс „Стария Добрич“", "Old Dobrich Outdoor Museum"),
        distractors = listOf(
            lt("Етнографски ареален комплекс - Златоград", "Ethnographic Areal Complex - Zlatograd"),
            lt("Старинен Пловдив", "Old Town Plovdiv"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "dobrudzha_ethnographic_house",
        regionName = lt("Добруджанска област", "Dobrudzha region"),
        imageAssetName = "daily_q_dobrudzha_ethnographic_house.png",
        correctAnswer = lt("Етнографска къща - Добрич", "Ethnographic House - Dobrich"),
        distractors = listOf(
            lt("Велянова къща", "House of Velyan"),
            lt("Исторически музей - Самоков", "History Museum - Samokov"),
        ),
    ),
    DailyLandmarkQuestionSeed(
        id = "dobrudzha_tutrakan_museum",
        regionName = lt("Добруджанска област", "Dobrudzha region"),
        imageAssetName = "daily_q_dobrudzha_tutrakan_museum.png",
        correctAnswer = lt(
            "Етнографски музей „Дунавски риболов и лодкостроене“",
            "Ethnographic Museum Danube Fishing and Boat Construction",
        ),
        distractors = listOf(
            lt("Музей на розата", "Museum of the Rose"),
            lt("Етър", "Etar"),
        ),
    ),
)
