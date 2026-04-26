package com.etnobulgaria.app.ui

import kotlin.math.max

data class EthnoArchiveEntry(
    val id: String,
    val unlockPoints: Int,
    val title: LocalizedText,
    val summary: LocalizedText,
    val highlights: List<LocalizedText>,
)

data class EthnoArchiveProgress(
    val totalPoints: Int,
    val entries: List<EthnoArchiveEntry>,
    val unlockedEntries: List<EthnoArchiveEntry>,
    val nextEntry: EthnoArchiveEntry?,
    val previousThreshold: Int,
    val nextThreshold: Int?,
) {
    val unlockedCount: Int
        get() = unlockedEntries.size

    val totalCount: Int
        get() = entries.size

    val latestUnlockedEntry: EthnoArchiveEntry?
        get() = unlockedEntries.maxByOrNull { it.unlockPoints }

    val pointsToNextUnlock: Int?
        get() = nextThreshold?.let { max(0, it - totalPoints) }

    fun progressToNextUnlock(): Float {
        val next = nextThreshold ?: return 1f
        val span = (next - previousThreshold).coerceAtLeast(1)
        val completed = (totalPoints - previousThreshold).coerceIn(0, span)
        return completed / span.toFloat()
    }
}

fun ethnoArchiveProgress(
    entries: List<EthnoArchiveEntry> = ethnoArchiveEntries,
    totalPoints: Int,
): EthnoArchiveProgress {
    val sortedEntries = entries.sortedBy { it.unlockPoints }
    val unlocked = sortedEntries.filter { totalPoints >= it.unlockPoints }
    val next = sortedEntries.firstOrNull { totalPoints < it.unlockPoints }
    val previousThreshold = unlocked.maxOfOrNull { it.unlockPoints } ?: 0

    return EthnoArchiveProgress(
        totalPoints = totalPoints,
        entries = sortedEntries,
        unlockedEntries = unlocked,
        nextEntry = next,
        previousThreshold = previousThreshold,
        nextThreshold = next?.unlockPoints,
    )
}

private fun archiveText(bg: String, en: String) = LocalizedText(bg = bg, en = en)

val ethnoArchiveEntries = listOf(
    EthnoArchiveEntry(
        id = "forgotten_words",
        unlockPoints = 5,
        title = archiveText("Забравени български думи", "Forgotten Bulgarian words"),
        summary = archiveText(
            "Кратка колекция от стари думи и названия, които днес се срещат рядко, но пазят следи от бита и занаятите.",
            "A short collection of old words and names that are rare today but preserve traces of everyday life and crafts.",
        ),
        highlights = listOf(
            archiveText(
                "Софра е ниска кръгла трапеза, около която семейството се събира за обща храна.",
                "Sofra is a low round table around which the family gathers for a shared meal.",
            ),
            archiveText(
                "Кросно е дървеният стан, на който се тъкат платна, черги и престилки.",
                "Krosno is the wooden loom used to weave cloth, rugs and aprons.",
            ),
            archiveText(
                "Чутура е съд за вода или вино, често носен при път и полска работа.",
                "Chutura is a vessel for water or wine, often carried while travelling or working in the fields.",
            ),
        ),
    ),
    EthnoArchiveEntry(
        id = "beliefs_and_taboo",
        unlockPoints = 10,
        title = archiveText("Народни поверия и забрани", "Folk beliefs and taboos"),
        summary = archiveText(
            "Тези вярвания не са просто суеверия, а опит на общността да подреди деня, дома и отношенията между хората.",
            "These beliefs are not merely superstitions but the community's way of ordering the day, the home and human relationships.",
        ),
        highlights = listOf(
            archiveText(
                "На големи празници не се пере и не се шие, за да бъде денят отделен за почит и събиране.",
                "On major feast days people avoid washing and sewing so the day remains dedicated to reverence and gathering.",
            ),
            archiveText(
                "След залез не се изнася сол или огън, за да не излезе берекетът от дома.",
                "After sunset people avoid taking salt or fire out of the house so prosperity does not leave with them.",
            ),
            archiveText(
                "Прагът не се подава с празни ръце, защото се смята за граница между вътрешния и външния свят.",
                "One does not cross the threshold empty-handed because it is seen as a boundary between the inner and outer world.",
            ),
        ),
    ),
    EthnoArchiveEntry(
        id = "home_symbols",
        unlockPoints = 15,
        title = archiveText("Тайни символи в българския дом", "Hidden symbols in the Bulgarian home"),
        summary = archiveText(
            "Много предмети в дома имат не само битова, но и защитна, обредна и паметова функция.",
            "Many household objects serve not only a practical role but also a protective, ritual and memorial one.",
        ),
        highlights = listOf(
            archiveText(
                "Менчето е знак за чиста вода, плодородие и благослов при посрещане и празник.",
                "The ceremonial copper vessel symbolizes pure water, fertility and blessing during greetings and festivities.",
            ),
            archiveText(
                "Хурката е знак за женско трудолюбие, сръчност и предаване на умение между поколенията.",
                "The distaff symbolizes women's diligence, dexterity and the passing of skill between generations.",
            ),
            archiveText(
                "Хлопката и звънците не само звучат силно, а маркират прогонване на злото и преход към нов цикъл.",
                "Cowbells and ritual bells are not only loud; they mark the driving away of evil and the passage into a new cycle.",
            ),
        ),
    ),
    EthnoArchiveEntry(
        id = "colors_and_numbers",
        unlockPoints = 20,
        title = archiveText("Смисълът на цветовете и числата", "The meaning of colors and numbers"),
        summary = archiveText(
            "В обредите и орнаментите цветът и числото често носят скрито послание за живот, защита и повторяемост.",
            "In rituals and ornaments color and number often carry a hidden message about life, protection and cyclical renewal.",
        ),
        highlights = listOf(
            archiveText(
                "Червеното се свързва със здраве, сила и пазене от лоши влияния.",
                "Red is linked to health, strength and protection from harmful influences.",
            ),
            archiveText(
                "Бялото символизира чистота, ново начало и празнична завършеност.",
                "White symbolizes purity, a new beginning and festive completeness.",
            ),
            archiveText(
                "Числата 3, 7 и 9 се повтарят в ритуали, защото носят идея за ред, завършен цикъл и благослов.",
                "The numbers 3, 7 and 9 recur in rituals because they carry the idea of order, a complete cycle and blessing.",
            ),
        ),
    ),
    EthnoArchiveEntry(
        id = "blessings",
        unlockPoints = 25,
        title = archiveText("Старинни благословии и пожелания", "Traditional blessings and wishes"),
        summary = archiveText(
            "Благословията е кратък словесен ритуал, чрез който се пожелават здраве, дом, плодородие и добър път.",
            "A blessing is a short verbal ritual used to wish health, home, fertility and a good journey.",
        ),
        highlights = listOf(
            archiveText(
                "„Да е пълна къщата като кошер.“ пожелава изобилие, движение и живот в дома.",
                "\"May the house be full like a beehive\" wishes abundance, movement and life in the home.",
            ),
            archiveText(
                "„Да ти е лек пътят.“ не е просто изпращане, а пожелание за закрила по време на път.",
                "\"May your road be easy\" is more than farewell; it is a wish for protection while travelling.",
            ),
            archiveText(
                "„Да се множи доброто.“ свързва личното щастие с благото на цялата общност.",
                "\"May the good multiply\" connects personal happiness with the well-being of the whole community.",
            ),
        ),
    ),
    EthnoArchiveEntry(
        id = "ritual_meanings",
        unlockPoints = 30,
        title = archiveText("Скрити значения на ритуалите", "Hidden meanings of rituals"),
        summary = archiveText(
            "Зад народните действия често стои символика за пречистване, плодородие, преход и ново начало.",
            "Folk actions often carry symbolism of purification, fertility, transition and a new beginning.",
        ),
        highlights = listOf(
            archiveText(
                "Прескачането на огън не е само зрелище, а образ на пречистване и здраве.",
                "Jumping over fire is not just spectacle but an image of purification and health.",
            ),
            archiveText(
                "Венецът от зеленина бележи цикъла на възраждането, младостта и силата на природата.",
                "A wreath of greenery marks the cycle of renewal, youth and the power of nature.",
            ),
            archiveText(
                "Връзването на конец или мартеница изразява надежда за закрила и благополучие през идния период.",
                "Tying a thread or martenitsa expresses hope for protection and well-being in the coming period.",
            ),
        ),
    ),
)
