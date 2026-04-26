package com.etnobulgaria.app.ui

import java.util.Calendar

enum class EthnoRegionId {
    SHOPSKA,
    THRACIAN,
    SEVERNYASHKA,
    PIRIN,
    RHODOPE,
    DOBRUDZHA,
}

data class EthnoRegionPreview(
    val id: EthnoRegionId,
    val name: LocalizedText,
    val highlight: LocalizedText,
    val availableRegion: EthnoRegion? = null,
)

data class EthnoRegion(
    val id: EthnoRegionId,
    val name: LocalizedText,
    val subtitle: LocalizedText,
    val overview: LocalizedText,
    val coverage: LocalizedText,
    val visualIdentity: LocalizedText,
    val recognizableFeatures: List<LocalizedText> = emptyList(),
    val costumes: List<CostumeInfo>,
    val embroideries: List<EmbroideryInfo>,
    val traditions: List<TraditionInfo>,
    val music: MusicInfo,
    val places: List<CulturalPlaceInfo>,
    val quiz: List<QuizQuestion>,
    val gamification: RegionGamificationInfo? = null,
)

data class CostumeInfo(
    val title: LocalizedText,
    val shortDescription: LocalizedText,
    val elements: List<LocalizedText>,
    val category: CostumeCategory = CostumeCategory.WOMEN,
    val imageAssetName: String? = null,
    val elementDetails: List<CostumeElementInfo> = emptyList(),
    val hotspots: List<CostumeHotspot> = emptyList(),
)

data class EmbroideryInfo(
    val motif: LocalizedText,
    val meaning: LocalizedText,
    val colors: List<LocalizedText> = emptyList(),
    val imageAssetName: String? = null,
)

data class TraditionInfo(
    val title: LocalizedText,
    val season: LocalizedText,
    val summary: LocalizedText,
    val holidayIds: List<String> = emptyList(),
)

data class MusicInfo(
    val songExamples: List<LocalizedText>,
    val instruments: List<InstrumentInfo>,
    val audioClips: List<AudioClipInfo> = emptyList(),
)

data class InstrumentInfo(
    val name: LocalizedText,
    val imageAssetName: String? = null,
)

data class CulturalPlaceInfo(
    val name: LocalizedText,
    val type: LocalizedText,
    val location: LocalizedText,
    val summary: LocalizedText,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageAssetName: String? = null,
)

data class QuizQuestion(
    val question: LocalizedText,
    val options: List<LocalizedText>,
    val correctAnswerIndex: Int,
    val explanation: LocalizedText? = null,
)

data class RoadmapModule(
    val title: LocalizedText,
    val summary: LocalizedText,
)

data class DeviceFeaturePlan(
    val title: LocalizedText,
    val summary: LocalizedText,
)

data class CalendarTraditionSpotlight(
    val title: LocalizedText,
    val body: LocalizedText,
    val startMonth: Int = 1,
    val startDay: Int = 1,
    val endMonth: Int = 12,
    val endDay: Int = 31,
)

enum class DailyCalendarMomentType {
    HOLIDAY,
    OBSERVANCE,
    SEASONAL,
}

data class DailyCalendarMoment(
    val title: LocalizedText,
    val summary: LocalizedText,
    val type: DailyCalendarMomentType,
    val nameDays: List<LocalizedText> = emptyList(),
)

private fun text(bg: String, en: String) = LocalizedText(bg = bg, en = en)

private fun instrument(bg: String, en: String, imageAssetName: String? = null) = InstrumentInfo(
    name = text(bg, en),
    imageAssetName = imageAssetName,
)

private fun visibleCostumeElements(
    allElements: List<CostumeElementInfo>,
    vararg ids: String,
): List<CostumeElementInfo> {
    val visibleIds = ids.toSet()
    return allElements.filter { it.id in visibleIds }
}

val ethnoDiaryBlueprint = EthnoDiaryBlueprint(
    title = text("Етно дневник", "Ethno diary"),
    summary = text(
        bg = "Личен дневник със снимка, бележка и връзка към посетено място или регион.",
        en = "A personal diary with a photo, a note and a link to a visited place or region.",
    ),
    prompts = listOf(
        EthnoDiaryPrompt(
            title = text("Къде бях?", "Where was I?"),
            hint = text(
                bg = "Посочи музей, резерват, къща-музей или друг етнографски обект.",
                en = "Point to a museum, reserve, house museum or another ethnographic site.",
            ),
        ),
        EthnoDiaryPrompt(
            title = text("Какво ме впечатли?", "What impressed me?"),
            hint = text(
                bg = "Опиши носия, обичай, песен, шевица или атмосферата на мястото.",
                en = "Describe a costume, custom, song, embroidery motif or the atmosphere of the place.",
            ),
        ),
        EthnoDiaryPrompt(
            title = text("Какво искам да запомня?", "What do I want to remember?"),
            hint = text(
                bg = "Добави кратка лична бележка за преживяването.",
                en = "Add a short personal note about the experience.",
            ),
        ),
    ),
    supportsCameraPhoto = true,
)

val ethnoMapBlueprint = InteractiveMapBlueprint(
    assetPath = "maps/ethno_bulgaria_regions.png",
    fallbackTitle = text("Карта на етнографските области", "Map of the ethnographic regions"),
    fallbackSummary = text(
        bg = "Докосни област от картата, за да видиш нейното име и кратък етно акцент.",
        en = "Tap a region on the map to see its name and a short ethno highlight.",
    ),
    areas = listOf(
        MapRegionArea(
            regionId = EthnoRegionId.SHOPSKA,
            label = text("Шопска област", "Shopska region"),
            center = MapPoint(0.14f, 0.56f),
            touchPolygon = listOf(
                MapPoint(0.00f, 0.38f),
                MapPoint(0.17f, 0.36f),
                MapPoint(0.31f, 0.45f),
                MapPoint(0.27f, 0.71f),
                MapPoint(0.07f, 0.72f),
                MapPoint(0.00f, 0.66f),
            ),
        ),
        MapRegionArea(
            regionId = EthnoRegionId.THRACIAN,
            label = text("Тракийска област", "Thracian region"),
            center = MapPoint(0.55f, 0.59f),
            touchPolygon = listOf(
                MapPoint(0.31f, 0.44f),
                MapPoint(0.58f, 0.34f),
                MapPoint(0.97f, 0.42f),
                MapPoint(0.92f, 0.82f),
                MapPoint(0.56f, 1.00f),
                MapPoint(0.42f, 0.86f),
                MapPoint(0.30f, 0.70f),
            ),
        ),
        MapRegionArea(
            regionId = EthnoRegionId.SEVERNYASHKA,
            label = text("Северняшка област", "Severnyashka region"),
            center = MapPoint(0.30f, 0.34f),
            touchPolygon = listOf(
                MapPoint(0.00f, 0.08f),
                MapPoint(0.47f, 0.04f),
                MapPoint(0.57f, 0.12f),
                MapPoint(0.58f, 0.35f),
                MapPoint(0.31f, 0.44f),
                MapPoint(0.00f, 0.38f),
            ),
        ),
        MapRegionArea(
            regionId = EthnoRegionId.PIRIN,
            label = text("Пиринска област", "Pirin region"),
            center = MapPoint(0.17f, 0.84f),
            touchPolygon = listOf(
                MapPoint(0.00f, 0.66f),
                MapPoint(0.07f, 0.72f),
                MapPoint(0.26f, 0.71f),
                MapPoint(0.32f, 0.95f),
                MapPoint(0.10f, 0.99f),
                MapPoint(0.00f, 0.94f),
            ),
        ),
        MapRegionArea(
            regionId = EthnoRegionId.RHODOPE,
            label = text("Родопска област", "Rhodope region"),
            center = MapPoint(0.48f, 0.83f),
            touchPolygon = listOf(
                MapPoint(0.26f, 0.71f),
                MapPoint(0.43f, 0.71f),
                MapPoint(0.58f, 0.85f),
                MapPoint(0.54f, 0.99f),
                MapPoint(0.35f, 0.99f),
                MapPoint(0.24f, 0.86f),
            ),
        ),
        MapRegionArea(
            regionId = EthnoRegionId.DOBRUDZHA,
            label = text("Добруджанска област", "Dobrudzha region"),
            center = MapPoint(0.74f, 0.26f),
            touchPolygon = listOf(
                MapPoint(0.47f, 0.04f),
                MapPoint(0.95f, 0.06f),
                MapPoint(1.00f, 0.14f),
                MapPoint(0.97f, 0.42f),
                MapPoint(0.58f, 0.35f),
                MapPoint(0.57f, 0.12f),
            ),
        ),
    ),
)

val shopskaWomenCostumeElements = listOf(
    CostumeElementInfo(
        id = "women_shirt",
        type = CostumeElementType.SHIRT,
        title = text("Риза", "Shirt"),
        summary = text(
            bg = "Белият плат и украсените ръкави задават основата на женската шопска носия.",
            en = "The white fabric and decorated sleeves form the base of the women's Shopska costume.",
        ),
    ),
    CostumeElementInfo(
        id = "women_apron",
        type = CostumeElementType.APRON,
        title = text("Престилка", "Apron"),
        summary = text(
            bg = "Предната престилка е силен декоративен акцент и носи характерните за региона орнаменти.",
            en = "The front apron is a strong decorative accent carrying motifs typical of the region.",
        ),
    ),
    CostumeElementInfo(
        id = "women_jewelry",
        type = CostumeElementType.JEWELRY,
        title = text("Накити", "Jewelry"),
        summary = text(
            bg = "Богатите нанизи и метални елементи върху гърдите са сред най-разпознаваемите белези на женската шопска носия.",
            en = "The layered necklaces and metal details across the chest are among the most recognizable traits of the women's Shopska costume.",
        ),
    ),
    CostumeElementInfo(
        id = "women_ornament",
        type = CostumeElementType.ORNAMENT,
        title = text("Орнаменти", "Ornaments"),
        summary = text(
            bg = "Геометричните орнаменти по пазвата и ръкавите са сред най-разпознаваемите белези.",
            en = "The geometric ornaments across the chest and sleeves are among the most recognizable traits.",
        ),
    ),
)

val shopskaWomenCostumeHotspots = listOf(
    CostumeHotspot(elementId = "women_shirt", relativeX = 0.18f, relativeY = 0.43f),
    CostumeHotspot(elementId = "women_apron", relativeX = 0.50f, relativeY = 0.67f),
    CostumeHotspot(elementId = "women_jewelry", relativeX = 0.49f, relativeY = 0.24f),
    CostumeHotspot(elementId = "women_ornament", relativeX = 0.50f, relativeY = 0.78f),
)

val shopskaMenCostumeElements = listOf(
    CostumeElementInfo(
        id = "men_shirt",
        type = CostumeElementType.SHIRT,
        title = text("Риза", "Shirt"),
        summary = text(
            bg = "Мъжката риза е по-сдържана, но носи характерни декоративни линии и подчертани шевове.",
            en = "The men's shirt is more restrained, yet it carries characteristic decorative lines and visible seams.",
        ),
    ),
    CostumeElementInfo(
        id = "men_trousers",
        type = CostumeElementType.TROUSERS,
        title = text("Потури", "Baggy trousers"),
        summary = text(
            bg = "Потурите създават силуета, по който мъжката носия се разпознава най-бързо.",
            en = "The baggy trousers create the silhouette by which the men's costume is recognized most quickly.",
        ),
    ),
    CostumeElementInfo(
        id = "men_vest",
        type = CostumeElementType.VEST,
        title = text("Елече", "Vest"),
        summary = text(
            bg = "Елечето носи допълнителна структура и завършва празничния вид.",
            en = "The vest adds structure and completes the festive appearance.",
        ),
    ),
    CostumeElementInfo(
        id = "men_belt",
        type = CostumeElementType.BELTS,
        title = text("Колан и пояс", "Belt and sash"),
        summary = text(
            bg = "Коланът и поясът стабилизират облеклото и подчертават кръста.",
            en = "The belt and sash secure the clothing and emphasize the waist.",
        ),
    ),
    CostumeElementInfo(
        id = "men_shoes",
        type = CostumeElementType.LEATHER_SHOES,
        title = text("Цървули", "Traditional leather shoes"),
        summary = text(
            bg = "Цървулите завършват традиционния облик и са типична част от празничното облекло.",
            en = "The leather shoes complete the traditional appearance and are a typical part of festive attire.",
        ),
    ),
)

val shopskaMenCostumeHotspots = listOf(
    CostumeHotspot(elementId = "men_shirt", relativeX = 0.49f, relativeY = 0.16f),
    CostumeHotspot(elementId = "men_trousers", relativeX = 0.49f, relativeY = 0.71f),
    CostumeHotspot(elementId = "men_vest", relativeX = 0.49f, relativeY = 0.28f),
    CostumeHotspot(elementId = "men_belt", relativeX = 0.47f, relativeY = 0.45f),
)

val shopskaRegion = EthnoRegion(
    id = EthnoRegionId.SHOPSKA,
    name = text(
        bg = "Шопска област",
        en = "Shopska region",
    ),
    subtitle = text(
        bg = "Западнобългарска област с кукерски игри, двугласно пеене и силна локална идентичност",
        en = "A western Bulgarian region of kukeri rituals, two-part singing and a strong local identity",
    ),
    overview = text(
        bg = "Шопската област е един от най-разпознаваемите етнографски региони в България. Тя е свързана със силно чувство за местна идентичност, характерни носии, геометрична шевична украса, двугласно пеене и богати празнични обичаи.",
        en = "The Shopska region is one of the most recognizable ethnographic regions in Bulgaria. It is associated with a strong local identity, distinctive costumes, geometric embroidery, two-part singing and rich festive customs.",
    ),
    coverage = text(
        bg = "Обхваща културни средища около София, Перник, Самоков и съседни шопски територии.",
        en = "It covers cultural centers around Sofia, Pernik, Samokov and neighboring Shopska territories.",
    ),
    visualIdentity = text(
        bg = "Визуалният облик на региона се отличава с контрастни цветове, подчертани пояси, пафти, геометрични орнаменти и силно присъствие на празнични ритуали.",
        en = "The visual identity of the region stands out with contrasting colors, emphasized belts, ornate buckles, geometric ornaments and a strong presence of festive rituals.",
    ),
    recognizableFeatures = listOf(
        text("силно геометрични шевици", "strong geometric embroidery"),
        text("контрастни женски и мъжки носии", "contrasting women's and men's costumes"),
        text("кукерски и пролетни обичаи", "masquerade and spring customs"),
    ),
    costumes = listOf(
        CostumeInfo(
            title = text(
                bg = "Женска шопска носия",
                en = "Women’s Shopska costume",
            ),
            shortDescription = text(
                bg = "Празнична носия с ясно разграничени пластове, декоративна престилка и силно подчертана талия.",
                en = "A festive costume with clearly layered garments, a decorative apron and a strongly emphasized waistline.",
            ),
            elements = listOf(
                text("риза с богата украса по ръкавите", "a shirt with rich ornamentation on the sleeves"),
                text("сукман или горна дреха според местния вариант", "a sukman or outer garment depending on the local variant"),
                text("престилка", "apron"),
                text("пояс", "sash"),
                text("колани и текстилни украси", "belts and textile decorations"),
                text("накити и пафти", "jewelry and ornate buckles"),
                text("цървули или празнични обувки", "traditional leather shoes or festive footwear"),
                text("орнаменти по пазвата, ръкавите и подгъва", "ornaments across the chest, sleeves and hem"),
            ),
            category = CostumeCategory.WOMEN,
            imageAssetName = "costumes/shopska_women.png",
            elementDetails = visibleCostumeElements(
                shopskaWomenCostumeElements,
                "women_shirt",
                "women_apron",
                "women_jewelry",
                "women_ornament",
            ),
            hotspots = shopskaWomenCostumeHotspots,
        ),
        CostumeInfo(
            title = text(
                bg = "Мъжка шопска носия",
                en = "Men’s Shopska costume",
            ),
            shortDescription = text(
                bg = "По-сдържана като орнаментика, но със силно разпознаваем силует и подчертани елементи около кръста и горната дреха.",
                en = "More restrained in ornamentation, yet with a highly recognizable silhouette and emphasized details around the waist and outer garment.",
            ),
            elements = listOf(
                text("риза", "shirt"),
                text("потури", "baggy trousers"),
                text("пояс", "sash"),
                text("елече или горна връхна дреха", "vest or outer garment"),
                text("колан", "belt"),
                text("калпак или покривало за глава според повода", "a wool hat or head covering depending on the occasion"),
                text("вълнени чорапи", "wool socks"),
                text("цървули", "traditional leather shoes"),
                text("декоративни орнаменти по ризата и пояса", "decorative ornaments on the shirt and sash"),
            ),
            category = CostumeCategory.MEN,
            imageAssetName = "costumes/shopska_men.png",
            elementDetails = visibleCostumeElements(
                shopskaMenCostumeElements,
                "men_shirt",
                "men_trousers",
                "men_vest",
                "men_belt",
            ),
            hotspots = shopskaMenCostumeHotspots,
        ),
    ),
    embroideries = listOf(
        EmbroideryInfo(
            motif = text("Геометричен ромб", "Geometric diamond"),
            meaning = text(
                bg = "Свързва се с плодородие, стабилност и подреден свят.",
                en = "It is associated with fertility, stability and an ordered world.",
            ),
            colors = listOf(
                text("червено", "red"),
                text("черно", "black"),
                text("бяло", "white"),
            ),
            imageAssetName = "embroideries/shopska_geometric_diamond.png",
        ),
        EmbroideryInfo(
            motif = text("Кръстовиден знак", "Cross-shaped sign"),
            meaning = text(
                bg = "Използва се като защитен символ и знак за закрила.",
                en = "It is used as a protective symbol and a sign of safeguarding.",
            ),
            colors = listOf(
                text("червено", "red"),
                text("тъмнозелено", "dark green"),
            ),
            imageAssetName = "embroideries/shopska_cross_sign.png",
        ),
    ),
    traditions = listOf(
        TraditionInfo(
            title = text("Сурва и кукерски игри", "Surva and kukeri masquerade games"),
            season = text("зимен празничен цикъл", "winter festive cycle"),
            summary = text(
                bg = "Маскирани мъже с тежки звънци и ярка визия изпълняват ритуали за прогонване на злото и за пожелание на здраве и плодородие.",
                en = "Masked men with heavy bells and striking costumes perform rituals to drive away evil and wish for health and fertility.",
            ),
            holidayIds = listOf("surva"),
        ),
        TraditionInfo(
            title = text("Лазаруване", "Lazaruvane"),
            season = text("пролетен празничен цикъл", "spring festive cycle"),
            summary = text(
                bg = "Млади момичета обхождат домовете с песни и благословии, свързани с плодородие, здраве и преход към нов жизнен етап.",
                en = "Young girls visit homes with songs and blessings related to fertility, health and the transition into a new life stage.",
            ),
            holidayIds = listOf("lazarovden", "tsvetnitsa"),
        ),
        TraditionInfo(
            title = text("Коледуване", "Koledouvane"),
            season = text("зимен празничен цикъл", "winter festive cycle"),
            summary = text(
                bg = "Коледарски дружини обхождат къщите с наричания за благополучие, берекет и добра година.",
                en = "Christmas caroling groups visit homes with blessings for well-being, abundance and a good year.",
            ),
            holidayIds = listOf("budni_vecher", "koleda"),
        ),
    ),
    music = MusicInfo(
        songExamples = listOf(
            text("Шопско двугласно пеене", "Shopska two-part singing"),
            text("Празнична шопска хороводна песен", "Festive Shopska dance song"),
        ),
        instruments = listOf(
            instrument("двоянка", "dvoyanka", "instruments/shopska_dvoyanka.png"),
        ),
        audioClips = listOf(
            AudioClipInfo(
                title = text("Шопска песен", "Shopska song"),
                assetPath = "https://youtu.be/jtxF5vULqoU?list=RDjtxF5vULqoU",
                durationSeconds = 0,
                note = text(
                    bg = "Песен, която представя шопското звучене и певческата традиция.",
                    en = "A song that presents the Shopska sound and singing tradition.",
                ),
            ),
        ),
    ),
    places = listOf(
        CulturalPlaceInfo(
            name = text("Регионален исторически музей - София", "Regional History Museum - Sofia"),
            type = text("музей", "museum"),
            location = text("София", "Sofia"),
            summary = text(
                bg = "Добра отправна точка за културен контекст, градска история и връзката на Софийско със шопската традиция.",
                en = "A strong starting point for cultural context, urban history and the connection between the Sofia area and the Shopska tradition.",
            ),
            latitude = 42.6977,
            longitude = 23.3219,
            imageAssetName = "places/place_shopska_sofia_museum.png",
        ),
        CulturalPlaceInfo(
            name = text("Исторически музей - Самоков", "History Museum - Samokov"),
            type = text("музей", "museum"),
            location = text("Самоков", "Samokov"),
            summary = text(
                bg = "Място, през което може да се разгледа регионалната история, местният бит и художествените влияния в шопския културен ареал.",
                en = "A place where regional history, local everyday life and artistic influences within the Shopska cultural area can be explored.",
            ),
            latitude = 42.3370,
            longitude = 23.5528,
            imageAssetName = "places/place_shopska_samokov_museum.png",
        ),
        CulturalPlaceInfo(
            name = text(
                "Регионален исторически музей - Перник",
                "Regional History Museum - Pernik",
            ),
            type = text("музей", "museum"),
            location = text("Перник", "Pernik"),
            summary = text(
                bg = "Музеят представя историята, бита и традициите на Пернишкия край и е подходящо реално място за посещение в рамките на Шопската област.",
                en = "The museum presents the history, everyday life and traditions of the Pernik area and is a suitable real place to visit within the Shopska region.",
            ),
            latitude = 42.6057,
            longitude = 23.0368,
            imageAssetName = "places/place_shopska_pernik_museum.png",
        ),
        CulturalPlaceInfo(
            name = text("Регионален исторически музей - Кюстендил", "Regional History Museum - Kyustendil"),
            type = text("музей", "museum"),
            location = text("Кюстендил", "Kyustendil"),
            summary = text(
                bg = "Регионален културен център, свързан със западнобългарската традиция и локалната памет.",
                en = "A regional cultural center connected to western Bulgarian tradition and local memory.",
            ),
            latitude = 42.2831,
            longitude = 22.6911,
            imageAssetName = "places/place_shopska_kyustendil_museum.png",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            question = text(
                bg = "Кой обичай в Шопската област е свързан с маски, звънци и прогонване на злото?",
                en = "Which custom in the Shopska region is associated with masks, bells and driving away evil?",
            ),
            options = listOf(
                text("Лазаруване", "Lazaruvane"),
                text("Сурва", "Surva"),
                text("Коледуване", "Koledouvane"),
            ),
            correctAnswerIndex = 1,
        ),
        QuizQuestion(
            question = text(
                bg = "Кой елемент подчертава талията в традиционната носия?",
                en = "Which element emphasizes the waist in the traditional costume?",
            ),
            options = listOf(
                text("Пояс", "Sash"),
                text("Цървули", "Traditional leather shoes"),
                text("Калпак", "Wool hat"),
            ),
            correctAnswerIndex = 0,
        ),
        QuizQuestion(
            question = text(
                bg = "Кое описание най-добре подхожда на шопската шевична украса?",
                en = "Which description best matches Shopska embroidery decoration?",
            ),
            options = listOf(
                text("Предимно геометрична и контрастна", "Mostly geometric and high-contrast"),
                text("Изцяло без орнаменти", "Completely without ornaments"),
                text("Само в син цвят", "Only in blue"),
            ),
            correctAnswerIndex = 0,
        ),
        QuizQuestion(
            question = text(
                bg = "Кой град в шопската област е домакин на Международния фестивал на маскарадните игри?",
                en = "Which city in the Shopska region hosts the International Festival of Masquerade Games?",
            ),
            options = listOf(
                text("Перник", "Pernik"),
                text("Самоков", "Samokov"),
                text("Кюстендил", "Kyustendil"),
            ),
            correctAnswerIndex = 0,
        ),
        QuizQuestion(
            question = text(
                bg = "Кой инструмент се свързва със шопския звук и се чува в регионалните ансамбли?",
                en = "Which instrument is associated with the Shopska sound and is heard in regional ensembles?",
            ),
            options = listOf(
                text("Двоянка", "Dvoyanka"),
                text("Каба гайда", "Kaba gaida"),
                text("Гъдулка", "Gadulka"),
            ),
            correctAnswerIndex = 0,
        ),
    ),
    gamification = RegionGamificationInfo(
        stampTitle = text("Печат: Шопска област", "Stamp: Shopska region"),
        stampDescription = text(
            bg = "Печатът се отключва при пълно преминаване през съдържанието на региона и куиза.",
            en = "The stamp unlocks after completing the region content and the quiz.",
        ),
        badgeTitle = text("Значка: Шопски изследовател", "Badge: Shopska explorer"),
        badgeDescription = text(
            bg = "Значка за потребител, който е разгледал носиите, обичаите и културните места на региона.",
            en = "A badge for a user who has explored the region's costumes, customs and cultural places.",
        ),
        progressDescription = text(
            bg = "Регионът служи като шаблон за етно паспорта, значките и бъдещото отключване на останалите области.",
            en = "The region serves as the template for the Ethno passport, badges and the future unlocking flow for the remaining regions.",
        ),
    ),
)

val regionCatalog = listOf(
    EthnoRegionPreview(
        id = EthnoRegionId.SHOPSKA,
        name = text("Шопска област", "Shopska region"),
        highlight = text(
            bg = "Кукерски игри, двугласно пеене, богати носии и културни обекти от западните български земи.",
            en = "Kukeri rituals, two-part singing, rich costumes and cultural places from western Bulgaria.",
        ),
        availableRegion = shopskaRegion,
    ),
    EthnoRegionPreview(
        id = EthnoRegionId.THRACIAN,
        name = text("Тракийска област", "Thracian region"),
        highlight = text(
            bg = "Богато наследство от песни, носии и обреди, със силна връзка с плодородната южна част на България.",
            en = "A rich heritage of songs, costumes and rituals, with a strong bond to the fertile southern lands of Bulgaria.",
        ),
        availableRegion = thracianRegion,
    ),
    EthnoRegionPreview(
        id = EthnoRegionId.SEVERNYASHKA,
        name = text("Северняшка област", "Severnyashka region"),
        highlight = text(
            bg = "Изразителни носии, обичаи и музикални традиции от северните земи на България.",
            en = "Expressive costumes, customs and musical traditions from Bulgaria’s northern lands.",
        ),
        availableRegion = severnyashkaRegion,
    ),
    EthnoRegionPreview(
        id = EthnoRegionId.PIRIN,
        name = text("Пиринска област", "Pirin region"),
        highlight = text(
            bg = "Ярки костюми, планинска идентичност и богат празничен календар.",
            en = "Vivid costumes, a mountain identity and a rich festive calendar.",
        ),
        availableRegion = pirinRegion,
    ),
    EthnoRegionPreview(
        id = EthnoRegionId.RHODOPE,
        name = text("Родопска област", "Rhodope region"),
        highlight = text(
            bg = "Мистична музика, каба гайда и силна връзка между природата и традицията.",
            en = "Mystical music, kaba gaida and a strong link between nature and tradition.",
        ),
        availableRegion = rhodopeRegion,
    ),
    EthnoRegionPreview(
        id = EthnoRegionId.DOBRUDZHA,
        name = text("Добруджанска област", "Dobrudzha region"),
        highlight = text(
            bg = "Полски мотиви, характерна ритмика и земеделски обичаи с ясно разпознаваем стил.",
            en = "Field motifs, distinctive rhythm and agricultural customs with a recognizable style.",
        ),
        availableRegion = dobrudzhaRegion,
    ),
)

val featuredRegion: EthnoRegion = shopskaRegion

private fun fixedDateRule(month: Int, day: Int) = HolidayDateRule(
    ruleType = HolidayRuleType.FIXED_DATE,
    month = month,
    day = day,
)

private fun easterRelativeRule(offsetDays: Int) = HolidayDateRule(
    ruleType = HolidayRuleType.ORTHODOX_EASTER_OFFSET,
    easterOffsetDays = offsetDays,
)

val holidayCalendarEntries = listOf(
    HolidayCalendarEntry(
        id = "surva",
        title = text("Сурва", "Surva"),
        summary = text(
            bg = "Маскарадни дружини, звънци и благословии за здраве и плодородие дават силен ритуален старт на годината.",
            en = "Masquerade groups, bells and blessings for health and fertility give the year a strong ritual beginning.",
        ),
        rule = fixedDateRule(month = 1, day = 13),
        priority = 100,
    ),
    HolidayCalendarEntry(
        id = "trifon_zarezan",
        title = text("Трифон Зарезан", "Trifon Zarezan"),
        summary = text(
            bg = "Празникът свързва лозята, виното и пожеланията за плодородна година.",
            en = "The feast connects vineyards, wine and wishes for a fertile year.",
        ),
        rule = fixedDateRule(month = 2, day = 14),
        nameDays = listOf(
            text("Трифон", "Trifon"),
            text("Лозан", "Lozan"),
            text("Лозана", "Lozana"),
        ),
        priority = 90,
    ),
    HolidayCalendarEntry(
        id = "baba_marta",
        title = text("Баба Марта", "Baba Marta"),
        summary = text(
            bg = "Мартениците и белият с червено конец носят пожелания за здраве, късмет и ново начало.",
            en = "Martenitsi and the white-and-red thread carry wishes for health, luck and a fresh beginning.",
        ),
        rule = fixedDateRule(month = 3, day = 1),
        nameDays = listOf(
            text("Марта", "Marta"),
            text("Мартин", "Martin"),
            text("Мартина", "Martina"),
        ),
        priority = 95,
    ),
    HolidayCalendarEntry(
        id = "lazarovden",
        title = text("Лазаровден", "Lazarovden"),
        summary = text(
            bg = "Лазарските песни и благословии отбелязват пролетното обновление и прехода към зрелост.",
            en = "Lazar songs and blessings mark spring renewal and the transition into maturity.",
        ),
        rule = easterRelativeRule(offsetDays = -8),
        nameDays = listOf(
            text("Лазар", "Lazar"),
            text("Лазара", "Lazara"),
        ),
        priority = 110,
    ),
    HolidayCalendarEntry(
        id = "tsvetnitsa",
        title = text("Цветница", "Palm Sunday"),
        summary = text(
            bg = "Празникът съчетава венци, зеленина и пролетна символика на живота и разцвета.",
            en = "The holiday combines wreaths, greenery and spring symbolism of life and blossom.",
        ),
        rule = easterRelativeRule(offsetDays = -7),
        nameDays = listOf(
            text("Цветан", "Tsvetan"),
            text("Цветелина", "Tsvetelina"),
            text("Виолета", "Violeta"),
            text("Роза", "Roza"),
        ),
        priority = 120,
    ),
    HolidayCalendarEntry(
        id = "velikden",
        title = text("Великден", "Orthodox Easter"),
        summary = text(
            bg = "Боядисаните яйца, празничната трапеза и посещението на църква създават един от най-силните празнични моменти в годината.",
            en = "Painted eggs, the festive table and church visits create one of the strongest festive moments of the year.",
        ),
        rule = easterRelativeRule(offsetDays = 0),
        priority = 200,
    ),
    HolidayCalendarEntry(
        id = "gergyovden",
        title = text("Гергьовден", "St. George's Day"),
        summary = text(
            bg = "Гергьовден съчетава пролетни ритуали за здраве, зелени венци и празнична трапеза.",
            en = "St. George's Day combines spring rituals for health, green wreaths and a festive table.",
        ),
        rule = fixedDateRule(month = 5, day = 6),
        nameDays = listOf(
            text("Георги", "Georgi"),
            text("Гергана", "Gergana"),
        ),
        priority = 130,
    ),
    HolidayCalendarEntry(
        id = "enyovden",
        title = text("Еньовден", "Enyovden"),
        summary = text(
            bg = "Билките, росата и обредите за здраве правят Еньовден водещ летен обичай.",
            en = "Herbs, morning dew and health rituals make Enyovden a leading summer custom.",
        ),
        rule = fixedDateRule(month = 6, day = 24),
        nameDays = listOf(
            text("Еньо", "Enyo"),
            text("Яна", "Yana"),
            text("Янина", "Yanina"),
        ),
        priority = 110,
    ),
    HolidayCalendarEntry(
        id = "ilinden",
        title = text("Илинден", "Ilinden"),
        summary = text(
            bg = "Празникът е свързан с летни събори, общностна памет и планински традиции.",
            en = "The feast is linked to summer gatherings, communal memory and mountain traditions.",
        ),
        rule = fixedDateRule(month = 7, day = 20),
        nameDays = listOf(
            text("Илия", "Ilia"),
            text("Илияна", "Iliana"),
        ),
        priority = 105,
    ),
    HolidayCalendarEntry(
        id = "dimitrovden",
        title = text("Димитровден", "Dimitrovden"),
        summary = text(
            bg = "Димитровден бележи прехода към зимния цикъл и затваря стопанската година в традиционния календар.",
            en = "Dimitrovden marks the transition toward the winter cycle and closes the agrarian year in the traditional calendar.",
        ),
        rule = fixedDateRule(month = 10, day = 26),
        nameDays = listOf(
            text("Димитър", "Dimitar"),
            text("Димитрина", "Dimitrina"),
        ),
        priority = 110,
    ),
    HolidayCalendarEntry(
        id = "nikulden",
        title = text("Никулден", "Nikulden"),
        summary = text(
            bg = "Никулден е свързан с домашната трапеза, рибата и семейното празнуване.",
            en = "Nikulden is linked to the family table, fish dishes and home celebration.",
        ),
        rule = fixedDateRule(month = 12, day = 6),
        nameDays = listOf(
            text("Николай", "Nikolay"),
            text("Николина", "Nikolina"),
            text("Никол", "Nikol"),
        ),
        priority = 110,
    ),
    HolidayCalendarEntry(
        id = "budni_vecher",
        title = text("Бъдни вечер", "Christmas Eve"),
        summary = text(
            bg = "Семейната трапеза и очакването на коледарите правят Бъдни вечер център на зимната домашна традиция.",
            en = "The family table and the expectation of carolers make Christmas Eve the center of winter home tradition.",
        ),
        rule = fixedDateRule(month = 12, day = 24),
        priority = 150,
    ),
    HolidayCalendarEntry(
        id = "koleda",
        title = text("Коледа", "Christmas"),
        summary = text(
            bg = "Коледните песни и празничната домашна атмосфера създават усещане за общност и ново начало.",
            en = "Christmas songs and the festive home atmosphere create a sense of community and a new beginning.",
        ),
        rule = fixedDateRule(month = 12, day = 25),
        priority = 140,
    ),
)

val dailyObservanceEntries = listOf(
    HolidayCalendarEntry(
        id = "education_day",
        title = text("Международен ден на образованието", "International Day of Education"),
        summary = text(
            bg = "Денят насочва вниманието към знанието, достъпа до образование и ученето през целия живот.",
            en = "The day draws attention to knowledge, access to education and lifelong learning.",
        ),
        rule = fixedDateRule(month = 1, day = 24),
        priority = 60,
    ),
    HolidayCalendarEntry(
        id = "mother_language_day",
        title = text("Международен ден на майчиния език", "International Mother Language Day"),
        summary = text(
            bg = "Денят подчертава значението на езиковото разнообразие, културната памет и родната реч.",
            en = "The day emphasizes the importance of linguistic diversity, cultural memory and the native tongue.",
        ),
        rule = fixedDateRule(month = 2, day = 21),
        priority = 60,
    ),
    HolidayCalendarEntry(
        id = "womens_day",
        title = text("Международен ден на жената", "International Women's Day"),
        summary = text(
            bg = "Денят е посветен на уважението, признателността и приноса на жените в обществото и семейството.",
            en = "The day is dedicated to respect, gratitude and the contribution of women in society and family life.",
        ),
        rule = fixedDateRule(month = 3, day = 8),
        priority = 65,
    ),
    HolidayCalendarEntry(
        id = "water_day",
        title = text("Световен ден на водата", "World Water Day"),
        summary = text(
            bg = "Денят напомня за значението на чистата вода, природните ресурси и грижата за околната среда.",
            en = "The day reminds us of the importance of clean water, natural resources and care for the environment.",
        ),
        rule = fixedDateRule(month = 3, day = 22),
        priority = 60,
    ),
    HolidayCalendarEntry(
        id = "theatre_day",
        title = text("Световен ден на театъра", "World Theatre Day"),
        summary = text(
            bg = "Денят отбелязва живото изкуство на сцената и ролята му за културната памет и общността.",
            en = "The day celebrates the living art of the stage and its role in cultural memory and community life.",
        ),
        rule = fixedDateRule(month = 3, day = 27),
        priority = 55,
    ),
    HolidayCalendarEntry(
        id = "childrens_book_day",
        title = text("Международен ден на детската книга", "International Children's Book Day"),
        summary = text(
            bg = "Денят насърчава четенето, въображението и ранната среща на децата с книгите.",
            en = "The day encourages reading, imagination and children's early encounters with books.",
        ),
        rule = fixedDateRule(month = 4, day = 2),
        priority = 50,
    ),
    HolidayCalendarEntry(
        id = "health_day",
        title = text("Световен ден на здравето", "World Health Day"),
        summary = text(
            bg = "Денят акцентира върху грижата за здравето, профилактиката и личното благополучие.",
            en = "The day highlights healthcare, prevention and personal well-being.",
        ),
        rule = fixedDateRule(month = 4, day = 7),
        priority = 55,
    ),
    HolidayCalendarEntry(
        id = "earth_day",
        title = text("Ден на Земята", "Earth Day"),
        summary = text(
            bg = "Денят е посветен на природата, устойчивите навици и вниманието към планетата, в която живеем.",
            en = "The day is dedicated to nature, sustainable habits and care for the planet we live on.",
        ),
        rule = fixedDateRule(month = 4, day = 22),
        priority = 65,
    ),
    HolidayCalendarEntry(
        id = "book_day",
        title = text("Световен ден на книгата и авторското право", "World Book and Copyright Day"),
        summary = text(
            bg = "Денят насърчава четенето, уважението към авторите и връзката между книгите, знанието и въображението.",
            en = "The day encourages reading, respect for authors and the bond between books, knowledge and imagination.",
        ),
        rule = fixedDateRule(month = 4, day = 23),
        priority = 80,
    ),
    HolidayCalendarEntry(
        id = "europe_day",
        title = text("Ден на Европа", "Europe Day"),
        summary = text(
            bg = "Денят припомня идеята за сътрудничество, мир и културна свързаност между европейските общества.",
            en = "The day recalls the idea of cooperation, peace and cultural connection among European societies.",
        ),
        rule = fixedDateRule(month = 5, day = 9),
        priority = 55,
    ),
    HolidayCalendarEntry(
        id = "museum_day",
        title = text("Международен ден на музеите", "International Museum Day"),
        summary = text(
            bg = "Денят е посветен на музеите като пазители на културната памет, предметите и разказите на обществото.",
            en = "The day is dedicated to museums as keepers of cultural memory, objects and shared stories.",
        ),
        rule = fixedDateRule(month = 5, day = 18),
        priority = 70,
    ),
    HolidayCalendarEntry(
        id = "alphabet_day",
        title = text("Ден на светите братя Кирил и Методий", "Day of Saints Cyril and Methodius"),
        summary = text(
            bg = "Денят почита буквите, просветата и духовната връзка между език, книжовност и национална памет.",
            en = "The day honors letters, learning and the spiritual bond between language, literacy and national memory.",
        ),
        rule = fixedDateRule(month = 5, day = 24),
        priority = 85,
    ),
    HolidayCalendarEntry(
        id = "children_day",
        title = text("Международен ден на детето", "International Children's Day"),
        summary = text(
            bg = "Денят поставя акцент върху детството, грижата, играта и правото на спокойна и сигурна среда.",
            en = "The day puts the focus on childhood, care, play and the right to a safe and calm environment.",
        ),
        rule = fixedDateRule(month = 6, day = 1),
        priority = 75,
    ),
    HolidayCalendarEntry(
        id = "ocean_day",
        title = text("Световен ден на океаните", "World Oceans Day"),
        summary = text(
            bg = "Денят напомня за връзката между водата, климата, природата и отговорното отношение към моретата и океаните.",
            en = "The day reminds us of the link between water, climate, nature and responsible care for seas and oceans.",
        ),
        rule = fixedDateRule(month = 6, day = 8),
        priority = 50,
    ),
    HolidayCalendarEntry(
        id = "music_day",
        title = text("Световен ден на музиката", "World Music Day"),
        summary = text(
            bg = "Денят отдава почит на музиката като ежедневен спътник, културна памет и общ език между хората.",
            en = "The day pays tribute to music as a daily companion, cultural memory and a common language between people.",
        ),
        rule = fixedDateRule(month = 6, day = 21),
        priority = 60,
    ),
    HolidayCalendarEntry(
        id = "population_day",
        title = text("Световен ден на населението", "World Population Day"),
        summary = text(
            bg = "Денят насочва вниманието към обществото, развитието и качеството на живот на хората по света.",
            en = "The day draws attention to society, development and the quality of life of people around the world.",
        ),
        rule = fixedDateRule(month = 7, day = 11),
        priority = 45,
    ),
    HolidayCalendarEntry(
        id = "youth_day",
        title = text("Международен ден на младежта", "International Youth Day"),
        summary = text(
            bg = "Денят е посветен на младите хора, техните идеи, образованието и активното участие в обществото.",
            en = "The day is dedicated to young people, their ideas, education and active participation in society.",
        ),
        rule = fixedDateRule(month = 8, day = 12),
        priority = 55,
    ),
    HolidayCalendarEntry(
        id = "democracy_day",
        title = text("Международен ден на демокрацията", "International Day of Democracy"),
        summary = text(
            bg = "Денят подчертава значението на участието, диалога и отговорността в обществения живот.",
            en = "The day emphasizes the importance of participation, dialogue and responsibility in public life.",
        ),
        rule = fixedDateRule(month = 9, day = 15),
        priority = 45,
    ),
    HolidayCalendarEntry(
        id = "tourism_day",
        title = text("Световен ден на туризма", "World Tourism Day"),
        summary = text(
            bg = "Денят насърчава пътуването, опознаването на нови места и уважението към местната култура и наследство.",
            en = "The day encourages travel, discovering new places and respecting local culture and heritage.",
        ),
        rule = fixedDateRule(month = 9, day = 27),
        priority = 65,
    ),
    HolidayCalendarEntry(
        id = "older_persons_day",
        title = text("Международен ден на възрастните хора", "International Day of Older Persons"),
        summary = text(
            bg = "Денят е повод за уважение към опита, паметта и ролята на по-възрастните хора в семейството и обществото.",
            en = "The day is an occasion to honor the experience, memory and role of older people in family and society.",
        ),
        rule = fixedDateRule(month = 10, day = 1),
        priority = 45,
    ),
    HolidayCalendarEntry(
        id = "black_sea_day",
        title = text("Международен ден на Черно море", "International Black Sea Day"),
        summary = text(
            bg = "Денят напомня за природното богатство на Черно море и нуждата от грижа за крайбрежието и водата.",
            en = "The day reminds us of the natural wealth of the Black Sea and the need to care for the coast and its waters.",
        ),
        rule = fixedDateRule(month = 10, day = 31),
        priority = 60,
    ),
    HolidayCalendarEntry(
        id = "awakeners_day",
        title = text("Ден на народните будители", "Day of the National Awakeners"),
        summary = text(
            bg = "Денят почита книжовниците, просветителите и духовните водачи, оставили трайна следа в българската памет.",
            en = "The day honors writers, educators and spiritual leaders who left a lasting mark on Bulgarian memory.",
        ),
        rule = fixedDateRule(month = 11, day = 1),
        priority = 85,
    ),
    HolidayCalendarEntry(
        id = "tolerance_day",
        title = text("Международен ден на толерантността", "International Day for Tolerance"),
        summary = text(
            bg = "Денят насърчава уважението, приемането на различията и спокойния диалог между хората.",
            en = "The day encourages respect, acceptance of differences and calm dialogue between people.",
        ),
        rule = fixedDateRule(month = 11, day = 16),
        priority = 45,
    ),
    HolidayCalendarEntry(
        id = "disability_day",
        title = text("Международен ден на хората с увреждания", "International Day of Persons with Disabilities"),
        summary = text(
            bg = "Денят насочва вниманието към достъпността, подкрепата и равните възможности за всички хора.",
            en = "The day draws attention to accessibility, support and equal opportunities for all people.",
        ),
        rule = fixedDateRule(month = 12, day = 3),
        priority = 45,
    ),
    HolidayCalendarEntry(
        id = "human_rights_day",
        title = text("Ден на правата на човека", "Human Rights Day"),
        summary = text(
            bg = "Денят подчертава достойнството, свободата и основните права, които стоят в основата на човешкия живот.",
            en = "The day underlines dignity, freedom and the fundamental rights at the core of human life.",
        ),
        rule = fixedDateRule(month = 12, day = 10),
        priority = 45,
    ),
)

private fun calendarKey(month: Int, day: Int): Int = month * 100 + day

private fun CalendarTraditionSpotlight.matches(month: Int, day: Int): Boolean {
    val value = calendarKey(month, day)
    val start = calendarKey(startMonth, startDay)
    val end = calendarKey(endMonth, endDay)
    return value in start..end
}

private fun HolidayCalendarEntry.toTraditionSpotlight(): CalendarTraditionSpotlight {
    val bgNameDaySuffix = if (nameDays.isEmpty()) {
        ""
    } else {
        " Имен ден празнуват: ${nameDays.joinToString { it.bg }}."
    }
    val enNameDaySuffix = if (nameDays.isEmpty()) {
        ""
    } else {
        " Name day: ${nameDays.joinToString { it.en }}."
    }

    return CalendarTraditionSpotlight(
        title = title,
        body = LocalizedText(
            bg = summary.bg + bgNameDaySuffix,
            en = summary.en + enNameDaySuffix,
        ),
    )
}

val calendarTraditionSpotlights = listOf(
    CalendarTraditionSpotlight(
        title = text("Сурва и кукерски игри", "Surva and kukeri masquerade games"),
        body = text(
            bg = "Началото на годината е време за маскирани дружини, звънци и ритуали за здраве, плодородие и прогонване на злото.",
            en = "The beginning of the year is the season of masked groups, bells and rituals for health, fertility and the banishing of evil.",
        ),
        startMonth = 1,
        startDay = 1,
        endMonth = 1,
        endDay = 31,
    ),
    CalendarTraditionSpotlight(
        title = text("Трифон Зарезан", "Trifon Zarezan"),
        body = text(
            bg = "Февруари насочва вниманието към лозята, виното и благословиите за плодородна и благодатна година.",
            en = "February turns the spotlight toward the vineyards, wine and blessings for a fertile and prosperous year.",
        ),
        startMonth = 2,
        startDay = 1,
        endMonth = 2,
        endDay = 29,
    ),
    CalendarTraditionSpotlight(
        title = text("Баба Марта и мартеници", "Baba Marta and martenitsi"),
        body = text(
            bg = "Март носи мартеници, пожелания за здраве и обреди, които отбелязват прехода от зимата към пролетта.",
            en = "March brings martenitsi, wishes for health and customs that mark the transition from winter to spring.",
        ),
        startMonth = 3,
        startDay = 1,
        endMonth = 3,
        endDay = 31,
    ),
    CalendarTraditionSpotlight(
        title = text("Лазаруване и Цветница", "Lazaruvane and Palm Sunday"),
        body = text(
            bg = "Април е време за лазарски песни, венци и пролетни благословии, свързани с младостта, плодородието и обновлението.",
            en = "April is a time for Lazar songs, wreaths and spring blessings connected with youth, fertility and renewal.",
        ),
        startMonth = 4,
        startDay = 1,
        endMonth = 4,
        endDay = 20,
    ),
    CalendarTraditionSpotlight(
        title = text("Великденски и Гергьовденски обичаи", "Easter and St. George's Day customs"),
        body = text(
            bg = "Късната пролет събира великденски ритуали, домашни трапези и Гергьовденски обичаи за здраве, закрила и ново начало.",
            en = "Late spring brings together Easter rituals, family tables and St. George's Day customs for health, protection and a fresh start.",
        ),
        startMonth = 4,
        startDay = 21,
        endMonth = 5,
        endDay = 15,
    ),
    CalendarTraditionSpotlight(
        title = text("Еньовден и билките", "Enyovden and healing herbs"),
        body = text(
            bg = "В началото на лятото водещи са билките, росата и обредите за пречистване, здраве и лековита сила.",
            en = "At the beginning of summer the focus shifts to herbs, morning dew and rituals for purification, health and healing power.",
        ),
        startMonth = 5,
        startDay = 16,
        endMonth = 6,
        endDay = 30,
    ),
    CalendarTraditionSpotlight(
        title = text("Илинден и летни събори", "Ilinden and summer gatherings"),
        body = text(
            bg = "Летните събори и илинденските празници свързват общността с планината, труда и общата памет.",
            en = "Summer gatherings and Ilinden celebrations connect the community with the mountains, labor and shared memory.",
        ),
        startMonth = 7,
        startDay = 1,
        endMonth = 8,
        endDay = 31,
    ),
    CalendarTraditionSpotlight(
        title = text("Жътварски и есенни обичаи", "Harvest and autumn customs"),
        body = text(
            bg = "Есента акцентира върху жътва, благодарност, селски събори и споделен труд след плодородния сезон.",
            en = "Autumn highlights the harvest, gratitude, village fairs and shared work after the fertile season.",
        ),
        startMonth = 9,
        startDay = 1,
        endMonth = 10,
        endDay = 31,
    ),
    CalendarTraditionSpotlight(
        title = text("Домашна памет и есенни празници", "Family memory and late autumn feast days"),
        body = text(
            bg = "Ноември напомня за домашната памет, родовите връзки и есенните празници около семейното огнище.",
            en = "November draws attention to household memory, family ties and late autumn feast days around the home hearth.",
        ),
        startMonth = 11,
        startDay = 1,
        endMonth = 11,
        endDay = 30,
    ),
    CalendarTraditionSpotlight(
        title = text("Коледуване и Бъдни вечер", "Koledouvane and Christmas Eve"),
        body = text(
            bg = "Декември носи коледарски песни, благословии по домовете и силно усещане за общност и ново начало.",
            en = "December brings caroling songs, blessings from house to house and a strong sense of community and a new beginning.",
        ),
        startMonth = 12,
        startDay = 1,
        endMonth = 12,
        endDay = 31,
    ),
)

fun regionOfTheDay(
    regions: List<EthnoRegionPreview> = regionCatalog,
    calendar: Calendar = Calendar.getInstance(),
): EthnoRegionPreview {
    val safeRegions = regions.ifEmpty { regionCatalog }
    val dayIndex = (calendar.get(Calendar.DAY_OF_YEAR) - 1) % safeRegions.size
    return safeRegions[dayIndex]
}

fun traditionOfTheDay(
    holidayEntries: List<HolidayCalendarEntry> = holidayCalendarEntries,
    spotlights: List<CalendarTraditionSpotlight> = calendarTraditionSpotlights,
    calendar: Calendar = Calendar.getInstance(),
): CalendarTraditionSpotlight {
    holidaysForDate(holidayEntries, calendar).firstOrNull()?.let { holiday ->
        return holiday.toTraditionSpotlight()
    }

    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val safeSpotlights = spotlights.ifEmpty { calendarTraditionSpotlights }
    return safeSpotlights.firstOrNull { it.matches(month, day) }
        ?: safeSpotlights.first()
}

fun dailyCalendarMoment(
    holidayEntries: List<HolidayCalendarEntry> = holidayCalendarEntries,
    observanceEntries: List<HolidayCalendarEntry> = dailyObservanceEntries,
    spotlights: List<CalendarTraditionSpotlight> = calendarTraditionSpotlights,
    calendar: Calendar = Calendar.getInstance(),
): DailyCalendarMoment {
    holidaysForDate(holidayEntries, calendar).firstOrNull()?.let { holiday ->
        return DailyCalendarMoment(
            title = holiday.title,
            summary = holiday.summary,
            type = DailyCalendarMomentType.HOLIDAY,
            nameDays = holiday.nameDays,
        )
    }

    holidaysForDate(observanceEntries, calendar).firstOrNull()?.let { observance ->
        return DailyCalendarMoment(
            title = observance.title,
            summary = observance.summary,
            type = DailyCalendarMomentType.OBSERVANCE,
        )
    }

    val seasonalSpotlight = traditionOfTheDay(holidayEntries, spotlights, calendar)
    return DailyCalendarMoment(
        title = seasonalSpotlight.title,
        summary = seasonalSpotlight.body,
        type = DailyCalendarMomentType.SEASONAL,
    )
}

val roadmapModules = listOf(
    RoadmapModule(
        title = text("Културни места наблизо", "Nearby cultural places"),
        summary = text(
            bg = "Екран за откриване на музеи, къщи-музеи и архитектурни резервати около потребителя.",
            en = "A screen for discovering museums, house museums and architectural reserves near the user.",
        ),
    ),
    RoadmapModule(
        title = text("Етно дневник", "Ethno diary"),
        summary = text(
            bg = "Личен дневник със снимка, място и бележка за посещения, впечатления и традиции.",
            en = "A personal diary with a photo, place and note for visits, impressions and traditions.",
        ),
    ),
    RoadmapModule(
        title = text("Етно паспорт", "Ethno passport"),
        summary = text(
            bg = "Система с печати за посетени региони и обекти, която да показва напредъка на потребителя.",
            en = "A stamp system for visited regions and places that shows the user’s progress.",
        ),
    ),
    RoadmapModule(
        title = text("Значки и игрови елемент", "Badges and game layer"),
        summary = text(
            bg = "Значки за активност, отключване на съдържание и по-силно чувство за постепенно откриване на България.",
            en = "Badges for activity, content unlocking and a stronger sense of gradually discovering Bulgaria.",
        ),
    ),
    RoadmapModule(
        title = text("Настройки", "Settings"),
        summary = text(
            bg = "Управление на езика, темата и нотификациите според предпочитанията на потребителя.",
            en = "Controls for language, theme and notifications according to the user’s preferences.",
        ),
    ),
)

val deviceFeaturePlans = listOf(
    DeviceFeaturePlan(
        title = text("Местоположение", "Location"),
        summary = text(
            bg = "Показване на близки музеи, архитектурни резервати и къщи-музеи спрямо текущата позиция на потребителя.",
            en = "Show nearby museums, architectural reserves and house museums based on the user’s current position.",
        ),
    ),
    DeviceFeaturePlan(
        title = text("Камера", "Camera"),
        summary = text(
            bg = "Добавяне на снимка към етно дневника или към вече посетено културно място.",
            en = "Add a photo to the Ethno diary or to a cultural place that has already been visited.",
        ),
    ),
    DeviceFeaturePlan(
        title = text("Нотификации", "Notifications"),
        summary = text(
            bg = "Напомняния, нови културни обекти, тематични дни и бъдещи събития, свързани с традициите.",
            en = "Reminders, new cultural places, thematic days and upcoming events connected to traditions.",
        ),
    ),
    DeviceFeaturePlan(
        title = text("Мултимедия", "Multimedia"),
        summary = text(
            bg = "Добавяне на 1-2 характерни народни песни за всеки регион като аудио акцент към съдържанието.",
            en = "Add one or two characteristic folk songs for each region as an audio accent to the content.",
        ),
    ),
    DeviceFeaturePlan(
        title = text("Тъмна тема", "Dark theme"),
        summary = text(
            bg = "Поддръжка на светла и тъмна тема за по-добро потребителско изживяване и модерен завършек.",
            en = "Support for light and dark themes for a better user experience and a more modern finish.",
        ),
    ),
)
