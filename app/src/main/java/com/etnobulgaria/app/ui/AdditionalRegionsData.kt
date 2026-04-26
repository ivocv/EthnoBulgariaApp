package com.etnobulgaria.app.ui

private fun t(bg: String, en: String) = LocalizedText(bg = bg, en = en)

private fun costumeElement(
    id: String,
    type: CostumeElementType,
    titleBg: String,
    titleEn: String,
    summaryBg: String,
    summaryEn: String,
) = CostumeElementInfo(
    id = id,
    type = type,
    title = t(titleBg, titleEn),
    summary = t(summaryBg, summaryEn),
)

private fun womenHotspots(
    prefix: String,
    shirtX: Float,
    shirtY: Float,
    apronX: Float,
    apronY: Float,
    jewelryX: Float? = null,
    jewelryY: Float? = null,
    ornamentX: Float,
    ornamentY: Float,
    shoesX: Float? = null,
    shoesY: Float? = null,
) = buildList {
    add(CostumeHotspot(elementId = "${prefix}_shirt", relativeX = shirtX, relativeY = shirtY))
    add(CostumeHotspot(elementId = "${prefix}_apron", relativeX = apronX, relativeY = apronY))
    if (jewelryX != null && jewelryY != null) {
        add(CostumeHotspot(elementId = "${prefix}_jewelry", relativeX = jewelryX, relativeY = jewelryY))
    }
    add(CostumeHotspot(elementId = "${prefix}_ornament", relativeX = ornamentX, relativeY = ornamentY))
    if (shoesX != null && shoesY != null) {
        add(CostumeHotspot(elementId = "${prefix}_shoes", relativeX = shoesX, relativeY = shoesY))
    }
}

private fun menHotspots(
    prefix: String,
    shirtX: Float,
    shirtY: Float,
    trousersX: Float,
    trousersY: Float,
    vestX: Float,
    vestY: Float,
    beltX: Float,
    beltY: Float,
    shoesX: Float? = null,
    shoesY: Float? = null,
) = buildList {
    add(CostumeHotspot(elementId = "${prefix}_shirt", relativeX = shirtX, relativeY = shirtY))
    add(CostumeHotspot(elementId = "${prefix}_trousers", relativeX = trousersX, relativeY = trousersY))
    add(CostumeHotspot(elementId = "${prefix}_vest", relativeX = vestX, relativeY = vestY))
    add(CostumeHotspot(elementId = "${prefix}_belt", relativeX = beltX, relativeY = beltY))
    if (shoesX != null && shoesY != null) {
        add(CostumeHotspot(elementId = "${prefix}_shoes", relativeX = shoesX, relativeY = shoesY))
    }
}

private fun visibleCostumeElements(
    allElements: List<CostumeElementInfo>,
    vararg ids: String,
): List<CostumeElementInfo> {
    val visibleIds = ids.toSet()
    return allElements.filter { it.id in visibleIds }
}

private fun audioClip(
    titleBg: String,
    titleEn: String,
    assetPath: String,
    durationSeconds: Int,
    noteBg: String,
    noteEn: String,
) = AudioClipInfo(
    title = t(titleBg, titleEn),
    assetPath = assetPath,
    durationSeconds = durationSeconds,
    note = t(noteBg, noteEn),
)

private fun instrument(nameBg: String, nameEn: String, imageAssetName: String? = null) = InstrumentInfo(
    name = t(nameBg, nameEn),
    imageAssetName = imageAssetName,
)

private fun regionGamification(
    stampBg: String,
    stampEn: String,
    badgeBg: String,
    badgeEn: String,
    badgeDescriptionBg: String,
    badgeDescriptionEn: String,
    progressBg: String,
    progressEn: String,
) = RegionGamificationInfo(
    stampTitle = t(stampBg, stampEn),
    stampDescription = t(
        "Печатът се отключва след преминаване през съдържанието и куиза на региона.",
        "The stamp unlocks after completing the region content and quiz.",
    ),
    badgeTitle = t(badgeBg, badgeEn),
    badgeDescription = t(badgeDescriptionBg, badgeDescriptionEn),
    progressDescription = t(progressBg, progressEn),
)

private fun museumPlace(
    nameBg: String,
    nameEn: String,
    locationBg: String,
    locationEn: String,
    summaryBg: String,
    summaryEn: String,
    latitude: Double? = null,
    longitude: Double? = null,
    imageAssetName: String? = null,
) = CulturalPlaceInfo(
    name = t(nameBg, nameEn),
    type = t("музей", "museum"),
    location = t(locationBg, locationEn),
    summary = t(summaryBg, summaryEn),
    latitude = latitude,
    longitude = longitude,
    imageAssetName = imageAssetName,
)

private fun reservePlace(
    nameBg: String,
    nameEn: String,
    locationBg: String,
    locationEn: String,
    summaryBg: String,
    summaryEn: String,
    latitude: Double? = null,
    longitude: Double? = null,
    imageAssetName: String? = null,
) = CulturalPlaceInfo(
    name = t(nameBg, nameEn),
    type = t("резерват", "reserve"),
    location = t(locationBg, locationEn),
    summary = t(summaryBg, summaryEn),
    latitude = latitude,
    longitude = longitude,
    imageAssetName = imageAssetName,
)

private fun houseMuseumPlace(
    nameBg: String,
    nameEn: String,
    locationBg: String,
    locationEn: String,
    summaryBg: String,
    summaryEn: String,
    latitude: Double? = null,
    longitude: Double? = null,
    imageAssetName: String? = null,
) = CulturalPlaceInfo(
    name = t(nameBg, nameEn),
    type = t("къща-музей", "house museum"),
    location = t(locationBg, locationEn),
    summary = t(summaryBg, summaryEn),
    latitude = latitude,
    longitude = longitude,
    imageAssetName = imageAssetName,
)

private fun quizQuestion(
    questionBg: String,
    questionEn: String,
    options: List<LocalizedText>,
    correctAnswerIndex: Int,
) = QuizQuestion(
    question = t(questionBg, questionEn),
    options = options,
    correctAnswerIndex = correctAnswerIndex,
)

private val thracianWomenElements = listOf(
    costumeElement(
        id = "thracian_women_shirt",
        type = CostumeElementType.SHIRT,
        titleBg = "Риза",
        titleEn = "Shirt",
        summaryBg = "Тракийската женска риза е богато извезана по ръкавите и пазвата и стои в основата на празничния силует.",
        summaryEn = "The Thracian women's shirt is richly embroidered on the sleeves and chest and forms the base of the festive silhouette.",
    ),
    costumeElement(
        id = "thracian_women_apron",
        type = CostumeElementType.APRON,
        titleBg = "Престилка",
        titleEn = "Apron",
        summaryBg = "Предната престилка носи растителни и слънчеви мотиви, типични за плодородната тракийска равнина.",
        summaryEn = "The front apron carries plant and solar motifs typical of the fertile Thracian plain.",
    ),
    costumeElement(
        id = "thracian_women_sash",
        type = CostumeElementType.SASH,
        titleBg = "Пояс",
        titleEn = "Sash",
        summaryBg = "Широкият пояс оформя талията и събира пластовете на носията в единен празничен образ.",
        summaryEn = "The wide sash shapes the waist and brings the costume layers into a unified festive appearance.",
    ),
    costumeElement(
        id = "thracian_women_jewelry",
        type = CostumeElementType.JEWELRY,
        titleBg = "Накити",
        titleEn = "Jewelry",
        summaryBg = "Накитът при шията е малък, но ясно видим акцент, който придава завършен празничен облик на тракийската носия.",
        summaryEn = "The jewelry at the neckline is a small yet clearly visible accent that completes the festive Thracian costume.",
    ),
    costumeElement(
        id = "thracian_women_ornament",
        type = CostumeElementType.ORNAMENT,
        titleBg = "Орнаменти",
        titleEn = "Ornaments",
        summaryBg = "Орнаментите съчетават лозови, розетни и житни знаци, свързани с изобилието и домашното благополучие.",
        summaryEn = "The ornaments combine vine, rosette and wheat signs associated with abundance and domestic well-being.",
    ),
)

private val thracianMenElements = listOf(
    costumeElement(
        id = "thracian_men_shirt",
        type = CostumeElementType.SHIRT,
        titleBg = "Риза",
        titleEn = "Shirt",
        summaryBg = "Мъжката тракийска риза е по-сдържана, но пази характерни шевици по яката и маншетите.",
        summaryEn = "The men's Thracian shirt is more restrained, yet keeps characteristic embroidery on the collar and cuffs.",
    ),
    costumeElement(
        id = "thracian_men_trousers",
        type = CostumeElementType.TROUSERS,
        titleBg = "Потури",
        titleEn = "Baggy trousers",
        summaryBg = "Потурите са удобен и разпознаваем елемент в мъжката празнична носия.",
        summaryEn = "The baggy trousers are a practical and recognizable part of the men's festive costume.",
    ),
    costumeElement(
        id = "thracian_men_vest",
        type = CostumeElementType.VEST,
        titleBg = "Елече",
        titleEn = "Vest",
        summaryBg = "Елечето рамкира горната част на облеклото и често носи по-фина украса.",
        summaryEn = "The vest frames the upper part of the attire and often carries finer decoration.",
    ),
    costumeElement(
        id = "thracian_men_belt",
        type = CostumeElementType.BELTS,
        titleBg = "Колан и пояс",
        titleEn = "Belt and sash",
        summaryBg = "Коланът и поясът подчертават кръста и служат като практичен и декоративен елемент.",
        summaryEn = "The belt and sash emphasize the waist and serve as both practical and decorative elements.",
    ),
    costumeElement(
        id = "thracian_men_shoes",
        type = CostumeElementType.LEATHER_SHOES,
        titleBg = "Цървули",
        titleEn = "Traditional leather shoes",
        summaryBg = "Цървулите завършват силуета и са естествена част от селския празничен облик.",
        summaryEn = "The leather shoes complete the silhouette and are a natural part of the village festive look.",
    ),
)

val thracianRegion = EthnoRegion(
    id = EthnoRegionId.THRACIAN,
    name = t("Тракийска област", "Thracian region"),
    subtitle = t(
        "Плодородна равнинна област с ярки носии, лозарски и жътварски традиции",
        "A fertile lowland region with vivid costumes and strong wine and harvest traditions",
    ),
    overview = t(
        "Тракийската област обхваща част от най-плодородните земи на България и съчетава богато земеделско наследство, празнични ритуали, цветни носии и жива музикална традиция.",
        "The Thracian region covers some of the most fertile lands in Bulgaria and combines rich agrarian heritage, festive rituals, colorful costumes and a vivid musical tradition.",
    ),
    coverage = t(
        "Обхваща културни средища като Пловдив, Казанлък, Чирпан, Пазарджик, Стара Загора и околните части на Горнотракийската низина и Средна гора.",
        "It covers cultural centers such as Plovdiv, Kazanlak, Chirpan, Pazardzhik, Stara Zagora and the surrounding parts of the Upper Thracian Plain and Sredna Gora.",
    ),
    visualIdentity = t(
        "Регионът се отличава с ярки престилки, растителни и слънчеви мотиви, богати накити и празничност, свързана с лозята, розите и плодородието.",
        "The region stands out with bright aprons, plant and solar motifs, rich jewelry and a festive spirit connected to vineyards, roses and fertility.",
    ),
    recognizableFeatures = listOf(
        t("богати лозарски и жътварски обичаи", "rich wine and harvest customs"),
        t("цветни престилки и растителни мотиви", "colorful aprons and plant motifs"),
        t("музика с плавна певческа линия и силен ритъм", "music with flowing singing lines and strong rhythm"),
    ),
    costumes = listOf(
        CostumeInfo(
            title = t("Женска тракийска носия", "Women's Thracian costume"),
            shortDescription = t(
                "Празнична женска носия с богато извезана риза, колоритна престилка, пояс и метални накити.",
                "A festive women's costume with a richly embroidered shirt, colorful apron, sash and metal jewelry.",
            ),
            elements = listOf(
                t("риза с извезани ръкави", "shirt with embroidered sleeves"),
                t("престилка", "apron"),
                t("пояс", "sash"),
                t("колани", "belts"),
                t("накити", "jewelry"),
                t("пафти", "buckles"),
                t("цървули или скарпини", "traditional shoes or skarpini"),
                t("орнаменти", "ornaments"),
            ),
            category = CostumeCategory.WOMEN,
            imageAssetName = "costumes/thracian_women.png",
            elementDetails = visibleCostumeElements(
                thracianWomenElements,
                "thracian_women_shirt",
                "thracian_women_apron",
                "thracian_women_jewelry",
                "thracian_women_ornament",
            ),
            hotspots = womenHotspots(
                prefix = "thracian_women",
                shirtX = 0.34f,
                shirtY = 0.28f,
                apronX = 0.50f,
                apronY = 0.58f,
                jewelryX = 0.53f,
                jewelryY = 0.18f,
                ornamentX = 0.50f,
                ornamentY = 0.63f,
            ),
        ),
        CostumeInfo(
            title = t("Мъжка тракийска носия", "Men's Thracian costume"),
            shortDescription = t(
                "Мъжката носия е по-сдържана като украса, но силно разпознаваема чрез потури, пояс и горна дреха.",
                "The men's costume is more restrained in decoration, yet strongly recognizable through its trousers, sash and outer garment.",
            ),
            elements = listOf(
                t("риза", "shirt"),
                t("потури", "baggy trousers"),
                t("пояс", "sash"),
                t("елече", "vest"),
                t("колан", "belt"),
                t("цървули", "traditional leather shoes"),
                t("орнаменти по ризата", "ornaments on the shirt"),
            ),
            category = CostumeCategory.MEN,
            imageAssetName = "costumes/thracian_men.png",
            elementDetails = visibleCostumeElements(
                thracianMenElements,
                "thracian_men_shirt",
                "thracian_men_trousers",
                "thracian_men_vest",
                "thracian_men_belt",
            ),
            hotspots = menHotspots(
                prefix = "thracian_men",
                shirtX = 0.50f,
                shirtY = 0.12f,
                trousersX = 0.50f,
                trousersY = 0.74f,
                vestX = 0.50f,
                vestY = 0.28f,
                beltX = 0.50f,
                beltY = 0.46f,
            ),
        ),
    ),
    embroideries = listOf(
        EmbroideryInfo(
            motif = t("Слънчева розета", "Solar rosette"),
            meaning = t("Носи символика на жизненост, закрила и благополучие.", "It carries symbolism of vitality, protection and prosperity."),
            colors = listOf(t("жълто", "yellow"), t("червено", "red"), t("черно", "black")),
            imageAssetName = "embroideries/thracian_solar_rosette.png",
        ),
        EmbroideryInfo(
            motif = t("Осмолъчна звезда", "Eight-pointed star"),
            meaning = t("Носи символика на подреден космос, слънчев цикъл и празнична хармония.", "It carries symbolism of an ordered cosmos, a solar cycle and festive harmony."),
            colors = listOf(t("жълто", "yellow"), t("червено", "red"), t("черно", "black")),
            imageAssetName = "embroideries/thracian_eight_pointed_star.png",
        ),
    ),
    traditions = listOf(
        TraditionInfo(
            title = t("Трифон Зарезан", "Trifon Zarezan"),
            season = t("зимен и лозарски празничен цикъл", "winter and wine-growing festive cycle"),
            summary = t("Ритуалното зарязване на лозята и празничното вино поставят акцент върху плодородието и земеделската памет на региона.", "The ritual pruning of vineyards and festive wine place emphasis on fertility and the region's agrarian memory."),
            holidayIds = listOf("trifon_zarezan"),
        ),
        TraditionInfo(
            title = t("Кукерски игри в Тракия", "Kukeri games in Thrace"),
            season = t("зимен преход към пролетта", "winter transition into spring"),
            summary = t("Маскираните дружини с хлопки и ярки костюми пазят жива идеята за прогонване на злото и обновление на общността.", "Masked groups with bells and striking costumes preserve the idea of driving away evil and renewing the community."),
        ),
        TraditionInfo(
            title = t("Жътварски обичаи", "Harvest customs"),
            season = t("лятно-есенен цикъл", "summer-autumn cycle"),
            summary = t("Обредните песни, венците от класове и общата трапеза събират селската общност около края на жътвата.", "Ritual songs, wreaths of grain and the shared meal bring the village community together at the end of the harvest."),
        ),
    ),
    music = MusicInfo(
        songExamples = listOf(t("Бре, Петрунко", "Bre, Petrunko"), t("Тракийска хороводна песен", "Thracian dance song")),
        instruments = listOf(
            instrument("джура гайда", "djura gaida", "instruments/thracian_djura_gaida.png"),
        ),
        audioClips = listOf(
            audioClip("Тракийска песен", "Thracian song", "https://youtu.be/tBGS2UuNPho?list=RDtBGS2UuNPho", 0, "Песен, която представя тракийското звучене.", "A song that presents the Thracian sound."),
        ),
    ),
    places = listOf(
        museumPlace("Регионален етнографски музей - Пловдив", "Regional Ethnographic Museum - Plovdiv", "Пловдив", "Plovdiv", "Една от най-силните институции за традиционна култура в Южна България с експозиции за Тракия, Родопите и Средна гора.", "One of the strongest institutions for traditional culture in southern Bulgaria, with exhibitions about Thrace, the Rhodopes and Sredna Gora.", latitude = 42.1496, longitude = 24.7545, imageAssetName = "places/place_thracian_plovdiv_ethnographic.png"),
        reservePlace("Старинен Пловдив", "Old Town Plovdiv", "Пловдив", "Plovdiv", "Архитектурният резерват пази възрожденска градска среда, занаяти и културна памет, тясно свързани с тракийската област.", "The architectural reserve preserves a Revival urban environment, crafts and cultural memory closely tied to the Thracian region.", latitude = 42.1493, longitude = 24.7518, imageAssetName = "places/place_thracian_old_town_plovdiv.png"),
        museumPlace("Исторически музей „Искра“", "Iskra History Museum", "Казанлък", "Kazanlak", "Музеят предлага силен контекст за културата на Розовата долина и развитието на района през вековете.", "The museum offers strong context for the culture of the Rose Valley and the development of the area across the centuries.", latitude = 42.6197, longitude = 25.3928, imageAssetName = "places/place_thracian_iskra_museum.png"),
        museumPlace("Музей на розата", "Museum of the Rose", "Казанлък", "Kazanlak", "Музеят представя розопроизводството, ритуалите около розобера и символиката на Розовата долина.", "The museum presents rose production, rose-picking rituals and the symbolism of the Rose Valley.", latitude = 42.6206, longitude = 25.3924, imageAssetName = "places/place_thracian_rose_museum.png"),
        houseMuseumPlace("Къща-музей „Пейо Яворов“", "Peyo Yavorov House Museum", "Чирпан", "Chirpan", "Къщата-музей свързва културната памет на Тракия с литературното наследство и градската възрожденска среда.", "The house museum links Thrace's cultural memory with literary heritage and the urban Revival environment.", latitude = 42.2018, longitude = 25.3289, imageAssetName = "places/place_thracian_peyo_yavorov_house.png"),
    ),
    quiz = listOf(
        quizQuestion("Кой празник е особено свързан с лозарската култура в Тракийската област?", "Which feast is especially linked to wine-growing culture in the Thracian region?", listOf(t("Трифон Зарезан", "Trifon Zarezan"), t("Еньовден", "Enyovden"), t("Никулден", "Nikulden")), 0),
        quizQuestion("Кой музей е важна културна точка за Тракийската област в Пловдив?", "Which museum is an important cultural point for the Thracian region in Plovdiv?", listOf(t("Регионален етнографски музей", "Regional Ethnographic Museum"), t("Музей на хумора", "Museum of Humor"), t("Морски музей", "Maritime Museum")), 0),
        quizQuestion("Кой мотив най-силно подчертава връзката на региона със земеделието?", "Which motif most strongly emphasizes the region's link to agriculture?", listOf(t("Житен клас", "Wheat ear"), t("Корабно въже", "Ship rope"), t("Снежен връх", "Snow peak")), 0),
        quizQuestion("Кой е характерен инструмент за тракийската музика?", "Which instrument is characteristic of Thracian music?", listOf(t("Джура гайда", "Djura gaida"), t("Банджо", "Banjo"), t("Пиано", "Piano")), 0),
        quizQuestion("С какво се разпознава женската тракийска носия?", "What makes the women's Thracian costume recognizable?", listOf(t("Цветна престилка и богата украса", "A colorful apron and rich decoration"), t("Напълно черно облекло", "An entirely black outfit"), t("Липса на накити", "A complete lack of jewelry")), 0),
    ),
    gamification = regionGamification(
        stampBg = "Печат: Тракийска област",
        stampEn = "Stamp: Thracian region",
        badgeBg = "Значка: Тракийски пазител",
        badgeEn = "Badge: Thracian keeper",
        badgeDescriptionBg = "Значка за потребител, който е опознал лозарските, жътварските и градските културни пластове на Тракия.",
        badgeDescriptionEn = "A badge for a user who has explored the wine, harvest and urban cultural layers of Thrace.",
        progressBg = "Този регион добавя земеделска и обредна линия към етно паспорта.",
        progressEn = "This region adds an agrarian and ritual layer to the Ethno passport.",
    ),
)

private val severnyashkaWomenElements = listOf(
    costumeElement("severnyashka_women_shirt", CostumeElementType.SHIRT, "Риза", "Shirt", "Северняшката риза често е с богата червено-черна украса по ръкавите и пазвата.", "The Severnyashka shirt often carries rich red-and-black decoration on the sleeves and chest."),
    costumeElement("severnyashka_women_apron", CostumeElementType.APRON, "Престилка", "Apron", "Престилката е ключов цветен акцент в иначе по-светлия северняшки силует.", "The apron is a key color accent within the otherwise lighter northern silhouette."),
    costumeElement("severnyashka_women_sash", CostumeElementType.SASH, "Пояс", "Sash", "Поясът събира носията и често добавя силен контрастен цвят.", "The sash gathers the costume and often adds a strong contrasting color."),
    costumeElement("severnyashka_women_buckles", CostumeElementType.BUCKLES, "Пафти", "Buckles", "Пафтите дават завършен празничен вид и подчертават центъра на облеклото.", "The buckles complete the festive appearance and emphasize the center of the attire."),
    costumeElement("severnyashka_women_ornament", CostumeElementType.ORNAMENT, "Орнаменти", "Ornaments", "Северняшките орнаменти съчетават канатица, дърво на живота и линейни ритми.", "Northern ornaments combine kanatitsa, the tree of life and linear rhythmic patterns."),
)

private val severnyashkaMenElements = listOf(
    costumeElement("severnyashka_men_shirt", CostumeElementType.SHIRT, "Риза", "Shirt", "Мъжката риза е светла и практична, с пестелива украса и силен функционален характер.", "The men's shirt is light and practical, with restrained decoration and a strong functional character."),
    costumeElement("severnyashka_men_trousers", CostumeElementType.TROUSERS, "Потури", "Baggy trousers", "Потурите оформят силуета и пазят връзката с всекидневния труд и движението.", "The baggy trousers shape the silhouette and preserve the link to everyday work and movement."),
    costumeElement("severnyashka_men_vest", CostumeElementType.VEST, "Елече", "Vest", "Елечето често е по-тъмно и рамкира светлата основа на мъжката носия.", "The vest is often darker and frames the light base of the men's costume."),
    costumeElement("severnyashka_men_belt", CostumeElementType.BELTS, "Колан", "Belt", "Коланът съчетава практичност и декоративен завършек.", "The belt combines practicality with a decorative finish."),
    costumeElement("severnyashka_men_shoes", CostumeElementType.LEATHER_SHOES, "Цървули", "Traditional leather shoes", "Цървулите остават естествен завършек на северняшката мъжка носия.", "The leather shoes remain the natural finish of the Severnyashka men's costume."),
)

val severnyashkaRegion = EthnoRegion(
    id = EthnoRegionId.SEVERNYASHKA,
    name = t("Северняшка област", "Severnyashka region"),
    subtitle = t("Северните земи на България с бели носии, живи танцови ритми и силни занаятчийски традиции", "The northern lands of Bulgaria with bright costumes, lively dance rhythms and strong craft traditions"),
    overview = t("Северняшката област обединява традиции от Дунавската равнина и старите занаятчийски центрове в централната северна част на страната. Регионът се разпознава с по-светли носии, активна хорова култура и силни градски и селски занаяти.", "The Severnyashka region brings together traditions from the Danubian Plain and the old craft centers of north-central Bulgaria. The region is recognized by its lighter costumes, active horo culture and strong urban and rural crafts."),
    coverage = t("Обхваща културни средища като Габрово, Велико Търново, Арбанаси, Русе, Плевен, Елена и околните северни територии.", "It covers cultural centers such as Gabrovo, Veliko Tarnovo, Arbanasi, Ruse, Pleven, Elena and the surrounding northern territories."),
    visualIdentity = t("Северняшката визия съчетава светъл основен плат, червено-черни шевици, престилки с ритмична украса и практични, но празнично поднесени елементи.", "The Severnyashka visual identity combines a light base fabric, red-and-black embroidery, rhythmically decorated aprons and practical yet festive elements."),
    recognizableFeatures = listOf(
        t("по-светъл силует с червено-черни акценти", "a lighter silhouette with red-and-black accents"),
        t("силна връзка със занаяти и градски чаршии", "a strong link to crafts and old market streets"),
        t("жива хорова и танцова култура", "a vivid song and dance culture"),
    ),
    costumes = listOf(
        CostumeInfo(
            title = t("Женска северняшка носия", "Women's Severnyashka costume"),
            shortDescription = t("Женската носия съчетава светъл плат, престилка и ясни цветни акценти в украсата.", "The women's costume combines light fabric, an apron and clearly defined color accents in the decoration."),
            elements = listOf(t("риза", "shirt"), t("престилка", "apron"), t("пояс", "sash"), t("колани", "belts"), t("накити", "jewelry"), t("пафти", "buckles"), t("цървули или скарпини", "traditional shoes or skarpini"), t("орнаменти", "ornaments")),
            category = CostumeCategory.WOMEN,
            imageAssetName = "costumes/severnyashka_women.png",
            elementDetails = visibleCostumeElements(
                severnyashkaWomenElements,
                "severnyashka_women_shirt",
                "severnyashka_women_apron",
                "severnyashka_women_ornament",
            ),
            hotspots = womenHotspots(
                prefix = "severnyashka_women",
                shirtX = 0.50f,
                shirtY = 0.19f,
                apronX = 0.50f,
                apronY = 0.56f,
                ornamentX = 0.50f,
                ornamentY = 0.52f,
            ),
        ),
        CostumeInfo(
            title = t("Мъжка северняшка носия", "Men's Severnyashka costume"),
            shortDescription = t("Мъжката носия е практична и подредена, с ясно очертан колан, потури и горна дреха.", "The men's costume is practical and orderly, with a clearly defined belt, trousers and outer garment."),
            elements = listOf(t("риза", "shirt"), t("потури", "baggy trousers"), t("пояс", "sash"), t("елече", "vest"), t("колан", "belt"), t("цървули", "traditional leather shoes"), t("орнаменти", "ornaments")),
            category = CostumeCategory.MEN,
            imageAssetName = "costumes/severnyashka_men.png",
            elementDetails = visibleCostumeElements(
                severnyashkaMenElements,
                "severnyashka_men_shirt",
                "severnyashka_men_trousers",
                "severnyashka_men_vest",
                "severnyashka_men_belt",
            ),
            hotspots = menHotspots(
                prefix = "severnyashka_men",
                shirtX = 0.50f,
                shirtY = 0.14f,
                trousersX = 0.50f,
                trousersY = 0.75f,
                vestX = 0.50f,
                vestY = 0.29f,
                beltX = 0.50f,
                beltY = 0.44f,
            ),
        ),
    ),
    embroideries = listOf(
        EmbroideryInfo(t("Канатица", "Kanatitsa"), t("Свързва се със защита и подредба на света в традиционната символика.", "It is linked to protection and the ordering of the world in traditional symbolism."), listOf(t("червено", "red"), t("черно", "black"), t("бяло", "white")), "embroideries/severnyashka_kanatitsa.png"),
        EmbroideryInfo(t("Дърво на живота", "Tree of life"), t("Носи идея за приемственост между поколенията и устойчивост на рода.", "It carries the idea of continuity between generations and the endurance of the family line."), listOf(t("зелено", "green"), t("червено", "red"), t("кафяво", "brown")), "embroideries/severnyashka_tree_of_life.png"),
    ),
    traditions = listOf(
        TraditionInfo(t("Коледуване", "Koledouvane"), t("зимен празничен цикъл", "winter festive cycle"), t("Коледарските дружини носят благословии за дом, здраве и плодородие и остават една от най-живите северняшки традиции.", "Christmas caroling groups bring blessings for the home, health and fertility and remain one of the liveliest northern traditions."), listOf("budni_vecher", "koleda")),
        TraditionInfo(t("Лазаруване", "Lazaruvane"), t("пролетен цикъл", "spring cycle"), t("Обикалянето на домовете с песни и благословии пази връзката между общността, младостта и надеждата за плодородие.", "Visiting homes with songs and blessings preserves the bond between the community, youth and the hope for fertility."), listOf("lazarovden", "tsvetnitsa")),
        TraditionInfo(t("Герман и Пеперуда", "German and Peperuda rain rituals"), t("пролетно-летен земеделски цикъл", "spring-summer agrarian cycle"), t("Обредите за дъжд съчетават песен, символични действия и общностна молитва за добра реколта.", "The rain rituals combine singing, symbolic actions and a communal prayer for a good harvest.")),
    ),
    music = MusicInfo(
        songExamples = listOf(t("Северняшка хороводна песен", "Northern dance song"), t("Дунавско хоро", "Danube horo")),
        instruments = listOf(
            instrument("гъдулка", "gadulka", "instruments/severnyashka_gadulka.png"),
        ),
        audioClips = listOf(
            audioClip("Северняшка песен", "Severnyashka song", "https://youtu.be/X7teeqVbQbA?list=RDX7teeqVbQbA", 0, "Песен, която представя северняшката песенна традиция.", "A song that presents the Severnyashka singing tradition."),
        ),
    ),
    places = listOf(
        museumPlace("Регионален етнографски музей на открито „Етър“", "Ethnographic Open Air Museum Etar", "Габрово", "Gabrovo", "Етър е сред най-силните места за занаяти, архитектура и бит от северните и балканските земи.", "Etar is one of the strongest places for crafts, architecture and everyday life from the northern and Balkan lands.", latitude = 42.8080, longitude = 25.3500, imageAssetName = "places/place_severnyashka_etar.png"),
        reservePlace("Самоводска чаршия", "Samovodska Charshia", "Велико Търново", "Veliko Tarnovo", "Старата чаршия съхранява атмосферата на възрожденския пазар и действащи занаятчийски работилници.", "The old market street preserves the atmosphere of a Revival bazaar and still-active craft workshops.", latitude = 43.0827, longitude = 25.6297, imageAssetName = "places/place_severnyashka_samovodska_charshia.png"),
        reservePlace("Архитектурно-музеен резерват Арбанаси", "Arbanasi Architectural and Museum Reserve", "Арбанаси", "Arbanasi", "Арбанаси пази богати възрожденски къщи, църкви и спокойна среда, характерна за старото северно българско наследство.", "Arbanasi preserves rich Revival houses, churches and a calm environment characteristic of old northern Bulgarian heritage.", latitude = 43.0986, longitude = 25.6700, imageAssetName = "places/place_severnyashka_arbanasi_reserve.png"),
        reservePlace("Архитектурно-исторически резерват Боженци", "Bozhentsi Architectural and Historical Reserve", "Боженци", "Bozhentsi", "Резерватът предлага запазена възрожденска среда и усещане за севернобългарски бит и търговска култура.", "The reserve offers a preserved Revival environment and a sense of northern Bulgarian everyday life and trade culture.", latitude = 42.8789, longitude = 25.4957, imageAssetName = "places/place_severnyashka_bozhentsi_reserve.png"),
        museumPlace("Регионален исторически музей - Русе", "Regional History Museum - Ruse", "Русе", "Ruse", "Музеят дава културен контекст за дунавските градове и северния български свят.", "The museum provides cultural context for the Danubian towns and the northern Bulgarian world.", latitude = 43.8480, longitude = 25.9541, imageAssetName = "places/place_severnyashka_ruse_museum.png"),
    ),
    quiz = listOf(
        quizQuestion("Кой открит музей е сред най-важните културни места за Северняшката област?", "Which open-air museum is among the most important cultural places for the Severnyashka region?", listOf(t("Етър", "Etar"), t("Рилски манастир", "Rila Monastery"), t("Мадарски конник", "Madara Rider")), 0),
        quizQuestion("Коя традиция е особено характерна за зимния празничен цикъл в региона?", "Which tradition is especially characteristic of the winter festive cycle in the region?", listOf(t("Коледуване", "Koledouvane"), t("Розобер", "Rose picking"), t("Старчевата", "Starchevata")), 0),
        quizQuestion("Кой мотив е характерен за северняшка шевица?", "Which motif is characteristic of Severnyashka embroidery?", listOf(t("Канатица", "Kanatitsa"), t("Коралов риф", "Coral reef"), t("Палмов лист", "Palm leaf")), 0),
        quizQuestion("С какво се разпознава Самоводската чаршия?", "What is Samovodska Charshia recognized for?", listOf(t("Занаятчийски работилници", "Craft workshops"), t("Ски писти", "Ski slopes"), t("Минерални извори", "Mineral springs")), 0),
        quizQuestion("Кой инструмент присъства в северняшката музикална традиция?", "Which instrument is part of the Severnyashka musical tradition?", listOf(t("Гъдулка", "Gadulka"), t("Арфа", "Harp"), t("Саксофон", "Saxophone")), 0),
    ),
    gamification = regionGamification(
        stampBg = "Печат: Северняшка област",
        stampEn = "Stamp: Severnyashka region",
        badgeBg = "Значка: Майстор на занаята",
        badgeEn = "Badge: Craft master",
        badgeDescriptionBg = "Значка за опознаване на северните занаяти, чаршии и обреди.",
        badgeDescriptionEn = "A badge for exploring northern crafts, market streets and customs.",
        progressBg = "Регионът добавя линия на занаяти, възрожденски резервати и севернобългарски музикални ритми към етно паспорта.",
        progressEn = "The region adds crafts, Revival reserves and northern Bulgarian musical rhythms to the Ethno passport.",
    ),
)

private val pirinWomenElements = listOf(
    costumeElement("pirin_women_shirt", CostumeElementType.SHIRT, "Риза", "Shirt", "Женската пиринска риза е богато украсена и често носи ярка цветна линия по ръкавите.", "The women's Pirin shirt is richly decorated and often carries a vivid color line along the sleeves."),
    costumeElement("pirin_women_skirt", CostumeElementType.SKIRT, "Поличка", "Skirt", "Поличката добавя подвижност и още един цветен пласт към женската пиринска носия.", "The skirt adds movement and another vivid layer to the women's Pirin costume."),
    costumeElement("pirin_women_apron", CostumeElementType.APRON, "Престилка", "Apron", "Престилката е силен акцент, който придава яркост и ритъм на пиринската носия.", "The apron is a strong accent that brings brightness and rhythm to the Pirin costume."),
    costumeElement("pirin_women_sash", CostumeElementType.SASH, "Пояс", "Sash", "Поясът събира многопластовия силует и подчертава талията.", "The sash gathers the layered silhouette and emphasizes the waist."),
    costumeElement("pirin_women_jewelry", CostumeElementType.JEWELRY, "Накити", "Jewelry", "Червените нанизи около шията са видим акцент, който подсилва празничния характер на пиринската носия.", "The red necklaces around the neckline are a visible accent that reinforces the festive character of the Pirin costume."),
    costumeElement("pirin_women_ornament", CostumeElementType.ORNAMENT, "Орнаменти", "Ornaments", "Орнаментите често съчетават цветни и спираловидни мотиви с планински характер.", "The ornaments often combine floral and spiral motifs with a mountain character."),
)

private val pirinMenElements = listOf(
    costumeElement("pirin_men_shirt", CostumeElementType.SHIRT, "Риза", "Shirt", "Мъжката риза е светла основа на костюма, често комбинирана с тъмна горна дреха.", "The men's shirt forms a light base of the costume, often combined with a darker outer garment."),
    costumeElement("pirin_men_trousers", CostumeElementType.TROUSERS, "Потури", "Baggy trousers", "Потурите съчетават удобство и характерен силует за югозападната област.", "The baggy trousers combine comfort with a characteristic silhouette for the southwestern region."),
    costumeElement("pirin_men_vest", CostumeElementType.VEST, "Елече", "Vest", "Елечето е често тъмно, с фина украса и ясен празничен завършек.", "The vest is often dark, with subtle ornament and a clear festive finish."),
    costumeElement("pirin_men_belt", CostumeElementType.BELTS, "Колан и пояс", "Belt and sash", "Коланът и поясът стабилизират носията и дават допълнителен визуален ритъм.", "The belt and sash stabilize the costume and add extra visual rhythm."),
    costumeElement("pirin_men_shoes", CostumeElementType.LEATHER_SHOES, "Цървули", "Traditional leather shoes", "Цървулите остават естествен завършек на празничната мъжка носия.", "The leather shoes remain the natural finishing touch of the festive men's costume."),
)

val pirinRegion = EthnoRegion(
    id = EthnoRegionId.PIRIN,
    name = t("Пиринска област", "Pirin region"),
    subtitle = t("Планинска култура с ярки носии, силна песенна традиция и богати зимни и пролетни обичаи", "A mountain culture with vivid costumes, strong singing traditions and rich winter and spring customs"),
    overview = t("Пиринската област съчетава висока планинска идентичност, силни певчески традиции, ярка орнаментика и жива празничност. Тук музиката, носията и общностният ритуал са неразделни части от културната памет.", "The Pirin region combines a strong mountain identity, powerful singing traditions, vivid ornamentation and a lively festive spirit. Here music, costume and communal ritual are inseparable parts of cultural memory."),
    coverage = t("Обхваща културни средища като Банско, Разлог, Мелник, Сандански, Благоевград, Петрич и съседните пирински селища.", "It covers cultural centers such as Bansko, Razlog, Melnik, Sandanski, Blagoevgrad, Petrich and neighboring Pirin settlements."),
    visualIdentity = t("Пиринският облик се разпознава по ярки престилки, тъмни горни дрехи, богати цветни орнаменти и силен сценичен и празничен характер.", "The Pirin look is recognized by its vivid aprons, darker outer garments, rich colorful ornaments and a strong festive and performative character."),
    recognizableFeatures = listOf(
        t("мощна женска и мъжка песенна традиция", "a powerful women's and men's singing tradition"),
        t("ярки цветове и контрастни престилки", "vivid colors and contrasting aprons"),
        t("силни маскарадни и пролетни празници", "strong masquerade and spring celebrations"),
    ),
    costumes = listOf(
        CostumeInfo(
            title = t("Женска пиринска носия", "Women's Pirin costume"),
            shortDescription = t("Женската пиринска носия е ярка, многопластова и богато украсена с цветни престилки и накити.", "The women's Pirin costume is vivid, layered and richly decorated with colorful aprons and jewelry."),
            elements = listOf(t("риза", "shirt"), t("поличка", "skirt"), t("престилка", "apron"), t("пояс", "sash"), t("колани", "belts"), t("накити", "jewelry"), t("пафти", "buckles"), t("цървули или скарпини", "traditional shoes or skarpini"), t("орнаменти", "ornaments")),
            category = CostumeCategory.WOMEN,
            imageAssetName = "costumes/pirin_women.png",
            elementDetails = visibleCostumeElements(
                pirinWomenElements,
                "pirin_women_shirt",
                "pirin_women_apron",
                "pirin_women_jewelry",
                "pirin_women_ornament",
            ),
            hotspots = womenHotspots(
                prefix = "pirin_women",
                shirtX = 0.22f,
                shirtY = 0.33f,
                apronX = 0.50f,
                apronY = 0.70f,
                jewelryX = 0.50f,
                jewelryY = 0.13f,
                ornamentX = 0.50f,
                ornamentY = 0.44f,
            ),
        ),
        CostumeInfo(
            title = t("Мъжка пиринска носия", "Men's Pirin costume"),
            shortDescription = t("Мъжката носия съчетава риза, потури, пояс и тъмна горна дреха в силно разпознаваем силует.", "The men's costume combines a shirt, baggy trousers, sash and a dark outer garment in a strongly recognizable silhouette."),
            elements = listOf(t("риза", "shirt"), t("потури", "baggy trousers"), t("пояс", "sash"), t("елече", "vest"), t("колан", "belt"), t("цървули", "traditional leather shoes"), t("орнаменти", "ornaments")),
            category = CostumeCategory.MEN,
            imageAssetName = "costumes/pirin_men.png",
            elementDetails = visibleCostumeElements(
                pirinMenElements,
                "pirin_men_shirt",
                "pirin_men_trousers",
                "pirin_men_vest",
                "pirin_men_belt",
            ),
            hotspots = menHotspots(
                prefix = "pirin_men",
                shirtX = 0.50f,
                shirtY = 0.15f,
                trousersX = 0.50f,
                trousersY = 0.77f,
                vestX = 0.50f,
                vestY = 0.29f,
                beltX = 0.50f,
                beltY = 0.43f,
            ),
        ),
    ),
    embroideries = listOf(
        EmbroideryInfo(t("Кукичест ромб", "Hooked diamond"), t("Свързва се със защита и устойчивост в семейното пространство.", "It is associated with protection and stability within the family space."), listOf(t("червено", "red"), t("черно", "black"), t("жълто", "yellow")), "embroideries/pirin_hooked_diamond.png"),
        EmbroideryInfo(t("Спираловидна лоза", "Spiral vine"), t("Подчертава жизненост, движение и плодородие.", "It emphasizes vitality, movement and fertility."), listOf(t("зелено", "green"), t("червено", "red"), t("златисто", "golden")), "embroideries/pirin_spiral_vine.png"),
    ),
    traditions = listOf(
        TraditionInfo(t("Старчевата", "Starchevata"), t("новогодишен и зимен цикъл", "New Year and winter cycle"), t("Маскарадният обичай в Разложко и околните селища събира мъже в богато облечени дружини за прогонване на злото.", "The masquerade custom in the Razlog area and nearby settlements gathers men in richly dressed groups to drive away evil.")),
        TraditionInfo(t("Гергьовден", "St. George's Day"), t("пролетен цикъл", "spring cycle"), t("Празникът носи зелени венци, трапеза и общностни ритуали за здраве и плодородие.", "The feast brings green wreaths, a festive table and communal rituals for health and fertility."), listOf("gergyovden")),
        TraditionInfo(t("Лазаруване", "Lazaruvane"), t("пролетен цикъл", "spring cycle"), t("Лазарските групи подчертават прехода към пролетта и обредната връзка между младост, песен и благословия.", "Lazar groups emphasize the transition to spring and the ritual bond between youth, song and blessing."), listOf("lazarovden", "tsvetnitsa")),
    ),
    music = MusicInfo(
        songExamples = listOf(t("Пиринска бавна песен", "Slow Pirin song"), t("Пиринско хоро", "Pirin horo")),
        instruments = listOf(
            instrument("тамбура", "tambura", "instruments/pirin_tambura.png"),
        ),
        audioClips = listOf(
            audioClip("Пиринска песен", "Pirin song", "https://youtu.be/7xtjw03dKYY?list=RD7xtjw03dKYY", 0, "Песен, която представя пиринското звучене.", "A song that presents the Pirin sound."),
        ),
    ),
    places = listOf(
        houseMuseumPlace("Велянова къща", "House of Velyan", "Банско", "Bansko", "Къщата е сред емблематичните възрожденски домове в Банско и пази типичен за Пирин интериор и стенописна украса.", "The house is among the emblematic Revival homes in Bansko and preserves a Pirin-style interior and mural decoration.", latitude = 41.8366, longitude = 23.4894, imageAssetName = "places/place_pirin_velyan_house.png"),
        houseMuseumPlace("Къща-музей „Неофит Рилски“", "Neofit Rilski House Museum", "Банско", "Bansko", "Тази къща-музей свързва местната култура на Пирин с просветното и възрожденското наследство.", "This house museum links Pirin's local culture with educational and Revival heritage.", latitude = 41.8377, longitude = 23.4885, imageAssetName = "places/place_pirin_neofit_rilski_house.png"),
        houseMuseumPlace("Кордопулова къща", "Kordopulova House", "Мелник", "Melnik", "Къщата е ключов културен знак за търговския живот, архитектурата и винарската традиция в района.", "The house is a key cultural sign of local trade, architecture and the wine tradition in the area.", latitude = 41.5230, longitude = 23.3934, imageAssetName = "places/place_pirin_kordopulova_house.png"),
        reservePlace("Архитектурен резерват Мелник", "Melnik Architectural Reserve", "Мелник", "Melnik", "Резерватът пази атмосферата на малкия пирински град, свързан с вино, търговия и възрожденска култура.", "The reserve preserves the atmosphere of the small Pirin town linked to wine, trade and Revival culture.", latitude = 41.5231, longitude = 23.3937, imageAssetName = "places/place_pirin_melnik_reserve.png"),
        museumPlace("Археологически музей - Сандански", "Archaeological Museum - Sandanski", "Сандански", "Sandanski", "Музеят е реална културна спирка в града и допълва представата за историята и наследството на Пиринския край.", "The museum is a real cultural stop in the town and expands the picture of the history and heritage of the Pirin region.", latitude = 41.5666, longitude = 23.2786, imageAssetName = "places/place_pirin_sandanski_museum.png"),
    ),
    quiz = listOf(
        quizQuestion("Кой обичай е силно свързан с Разложко и Пиринската област?", "Which custom is strongly connected to the Razlog area and the Pirin region?", listOf(t("Старчевата", "Starchevata"), t("Розобер", "Rose picking"), t("Джулай морнинг", "July Morning")), 0),
        quizQuestion("Кой инструмент е особено характерен за музиката в Пирин?", "Which instrument is especially characteristic of music in Pirin?", listOf(t("Тамбура", "Tambura"), t("Арфа", "Harp"), t("Кларинет", "Clarinet")), 0),
        quizQuestion("Кое културно място е свързано с Мелник?", "Which cultural place is connected to Melnik?", listOf(t("Кордопулова къща", "Kordopulova House"), t("Етър", "Etar"), t("Рибарска махала", "Ribarska Mahala")), 0),
        quizQuestion("Какво отличава женската пиринска носия?", "What distinguishes the women's Pirin costume?", listOf(t("Ярки цветове и богата престилка", "Vivid colors and a rich apron"), t("Изцяло бяла украса", "Entirely white decoration"), t("Липса на орнаменти", "A lack of ornaments")), 0),
        quizQuestion("Кой празник е сред ключовите пролетни акценти в региона?", "Which feast is among the key spring highlights in the region?", listOf(t("Гергьовден", "St. George's Day"), t("Никулден", "Nikulden"), t("Димитровден", "Dimitrovden")), 0),
    ),
    gamification = regionGamification(
        stampBg = "Печат: Пиринска област",
        stampEn = "Stamp: Pirin region",
        badgeBg = "Значка: Гласът на Пирин",
        badgeEn = "Badge: Voice of Pirin",
        badgeDescriptionBg = "Значка за потребител, който е преминал през песните, носиите и празниците на Пирин.",
        badgeDescriptionEn = "A badge for a user who has completed the songs, costumes and celebrations of Pirin.",
        progressBg = "Пирин добавя планинска, певческа и обредна ос към етно паспорта и подсилва югозападната идентичност в приложението.",
        progressEn = "Pirin adds a mountain, vocal and ritual axis to the Ethno passport and strengthens the app's southwestern identity.",
    ),
)

private val rhodopeWomenElements = listOf(
    costumeElement("rhodope_women_shirt", CostumeElementType.SHIRT, "Риза", "Shirt", "Женската родопска риза е основа на носията и често се съчетава с тъмна вълнена горна дреха.", "The women's Rhodope shirt forms the base of the costume and is often combined with a darker wool garment."),
    costumeElement("rhodope_women_apron", CostumeElementType.APRON, "Престилка", "Apron", "Престилката носи по-дълбоки тонове и по-сдържана, но отчетлива украса.", "The apron carries deeper tones and more restrained, yet distinct decoration."),
    costumeElement("rhodope_women_sash", CostumeElementType.SASH, "Пояс", "Sash", "Поясът стяга силуета и рамкира носията в характерния планински стил.", "The sash tightens the silhouette and frames the costume in its characteristic mountain style."),
    costumeElement("rhodope_women_buckles", CostumeElementType.BUCKLES, "Пафти и накити", "Buckles and jewelry", "Металните детайли стоят по-обрано, но придават тежест и завършеност на празничния облик.", "The metal details are more restrained, yet they lend weight and completeness to the festive appearance."),
    costumeElement("rhodope_women_ornament", CostumeElementType.ORNAMENT, "Орнаменти", "Ornaments", "Орнаментите в Родопите често следват линии, напомнящи планина, вода и дълбока връзка с природата.", "Ornaments in the Rhodopes often follow lines reminiscent of mountains, water and a deep bond with nature."),
)

private val rhodopeMenElements = listOf(
    costumeElement("rhodope_men_shirt", CostumeElementType.SHIRT, "Риза", "Shirt", "Мъжката риза е светла основа на носията и често стои под по-тежка тъмна горна дреха.", "The men's shirt is the light base of the costume and often sits under a heavier dark outer garment."),
    costumeElement("rhodope_men_trousers", CostumeElementType.TROUSERS, "Потури", "Baggy trousers", "Потурите са удобни и естествено вписани в планинския начин на живот.", "The baggy trousers are practical and naturally suited to the mountain way of life."),
    costumeElement("rhodope_men_vest", CostumeElementType.VEST, "Елече", "Vest", "Горната дреха е важна за силуета и носи отличителната тежест на родопския костюм.", "The outer garment is important to the silhouette and carries the characteristic weight of the Rhodope costume."),
    costumeElement("rhodope_men_belt", CostumeElementType.BELTS, "Колан", "Belt", "Коланът е практичен и стабилизира облеклото в планински условия.", "The belt is practical and stabilizes the clothing in mountain conditions."),
    costumeElement("rhodope_men_shoes", CostumeElementType.LEATHER_SHOES, "Цървули", "Traditional leather shoes", "Цървулите са логично продължение на функционалния, но тържествен планински костюм.", "The leather shoes are a natural continuation of the functional yet festive mountain costume."),
)

val rhodopeRegion = EthnoRegion(
    id = EthnoRegionId.RHODOPE,
    name = t("Родопска област", "Rhodope region"),
    subtitle = t("Планински регион с дълги песни, каба гайда и силно усещане за природа и обредност", "A mountain region of long songs, kaba gaida and a strong sense of nature and ritual life"),
    overview = t("Родопската област е една от най-разпознаваемите музикални и духовни области в България. Тя се свързва с каба гайда, протяжно пеене, дълбока връзка с планината и обреди, които пазят локалната памет.", "The Rhodope region is one of the most recognizable musical and spiritual regions in Bulgaria. It is associated with the kaba gaida, prolonged singing, a deep connection to the mountains and rituals that preserve local memory."),
    coverage = t("Обхваща културни средища като Смолян, Широка лъка, Златоград, Девин, Чепеларе, Могилица и съседните родопски долини.", "It covers cultural centers such as Smolyan, Shiroka Laka, Zlatograd, Devin, Chepelare, Mogilitsa and neighboring Rhodope valleys."),
    visualIdentity = t("Родопската визия е по-дълбока и тъмна като цветност, с плътни вълнени материи, умерена украса и силно присъствие на музикалния и природния контекст.", "The Rhodope visual identity is deeper and darker in tone, with dense wool fabrics, restrained decoration and a strong presence of musical and natural context."),
    recognizableFeatures = listOf(
        t("каба гайда и протяжно пеене", "kaba gaida and prolonged singing"),
        t("тъмни вълнени носии с умерена украса", "dark wool costumes with restrained decoration"),
        t("обичаи, свързани с планината и локалната общност", "customs tied to the mountains and the local community"),
    ),
    costumes = listOf(
        CostumeInfo(
            title = t("Женска родопска носия", "Women's Rhodope costume"),
            shortDescription = t("Женската носия е по-плътна и тъмна като колорит, с ясно усещане за планинска среда и устойчивост.", "The women's costume is denser and darker in color, with a clear sense of mountain environment and resilience."),
            elements = listOf(t("риза", "shirt"), t("престилка", "apron"), t("пояс", "sash"), t("колани", "belts"), t("накити", "jewelry"), t("пафти", "buckles"), t("цървули или скарпини", "traditional shoes or skarpini"), t("орнаменти", "ornaments")),
            category = CostumeCategory.WOMEN,
            imageAssetName = "costumes/rhodope_women.png",
            elementDetails = visibleCostumeElements(
                rhodopeWomenElements,
                "rhodope_women_shirt",
                "rhodope_women_apron",
                "rhodope_women_ornament",
            ),
            hotspots = womenHotspots(
                prefix = "rhodope_women",
                shirtX = 0.24f,
                shirtY = 0.30f,
                apronX = 0.50f,
                apronY = 0.53f,
                ornamentX = 0.50f,
                ornamentY = 0.57f,
            ),
        ),
        CostumeInfo(
            title = t("Мъжка родопска носия", "Men's Rhodope costume"),
            shortDescription = t("Мъжката носия комбинира светла основа и тъмна горна дреха в характерен планински силует.", "The men's costume combines a light base with a dark outer garment in a characteristic mountain silhouette."),
            elements = listOf(t("риза", "shirt"), t("потури", "baggy trousers"), t("пояс", "sash"), t("елече", "vest"), t("колан", "belt"), t("цървули", "traditional leather shoes"), t("орнаменти", "ornaments")),
            category = CostumeCategory.MEN,
            imageAssetName = "costumes/rhodope_men.png",
            elementDetails = visibleCostumeElements(
                rhodopeMenElements,
                "rhodope_men_shirt",
                "rhodope_men_trousers",
                "rhodope_men_vest",
                "rhodope_men_belt",
            ),
            hotspots = menHotspots(
                prefix = "rhodope_men",
                shirtX = 0.50f,
                shirtY = 0.11f,
                trousersX = 0.50f,
                trousersY = 0.72f,
                vestX = 0.50f,
                vestY = 0.25f,
                beltX = 0.50f,
                beltY = 0.44f,
            ),
        ),
    ),
    embroideries = listOf(
        EmbroideryInfo(t("Планинско цвете", "Mountain flower"), t("Носи усещане за красота и чистота сред суровата природа.", "It conveys beauty and purity amid the harsh natural environment."), listOf(t("бордо", "burgundy"), t("зелено", "green"), t("бяло", "white")), "embroideries/rhodope_mountain_flower.png"),
        EmbroideryInfo(t("Кръст в рамка", "Framed cross"), t("Символизира закрила, ред и духовна устойчивост.", "It symbolizes protection, order and spiritual endurance."), listOf(t("червено", "red"), t("черно", "black"), t("бяло", "white")), "embroideries/rhodope_framed_cross.png"),
    ),
    traditions = listOf(
        TraditionInfo(t("Родопска сватба", "Rhodope wedding ritual"), t("обреден семеен цикъл", "ritual family cycle"), t("Сватбеният обред съчетава песни, носии, благословии и много пластове локална памет.", "The wedding ritual combines songs, costumes, blessings and many layers of local memory.")),
        TraditionInfo(t("Песпонеделник", "Pesponedelnik"), t("зимен и предвеликденски цикъл", "winter and pre-Easter cycle"), t("Този маскараден обичай в Родопите пази общностна енергия, смях и ритуално обръщане на реда.", "This masquerade custom in the Rhodopes preserves communal energy, humor and ritual inversion of order.")),
        TraditionInfo(t("Еньовден", "Enyovden"), t("летен цикъл", "summer cycle"), t("Събирането на билки, росата и представите за лековита сила стоят в центъра на този летен празник.", "The gathering of herbs, morning dew and beliefs in healing power stand at the center of this summer feast."), listOf("enyovden")),
    ),
    music = MusicInfo(
        songExamples = listOf(t("Бела съм, бела, юначе", "Bela sam, bela, yunache"), t("Излел е Дельо хайдутин", "Izlel e Delyo haidutin")),
        instruments = listOf(
            instrument("каба гайда", "kaba gaida", "instruments/rhodope_kaba_gaida.png"),
        ),
        audioClips = listOf(
            audioClip("Родопска песен", "Rhodope song", "https://youtu.be/PgdwGH9q8YQ?list=RDPgdwGH9q8YQ", 0, "Песен, която представя родопската песенна линия.", "A song that presents the Rhodope song tradition."),
        ),
    ),
    places = listOf(
        museumPlace("Етнографски ареален комплекс - Златоград", "Ethnographic Areal Complex - Zlatograd", "Златоград", "Zlatograd", "Комплексът съчетава музей, работилници и жива градска среда, представящи родопския бит и занаяти.", "The complex combines a museum, workshops and a living urban environment presenting Rhodope everyday life and crafts.", latitude = 41.3802, longitude = 25.0926, imageAssetName = "places/place_rhodope_zlatograd_complex.png"),
        reservePlace("Архитектурно-фолклорен резерват Широка лъка", "Shiroka Laka Architectural and Folklore Reserve", "Широка лъка", "Shiroka Laka", "Резерватът пази каменна архитектура, музикална памет и силна връзка с родопското пеене.", "The reserve preserves stone architecture, musical memory and a strong bond to Rhodope singing.", latitude = 41.6714, longitude = 24.5801, imageAssetName = "places/place_rhodope_shiroka_laka_folklore_reserve.png"),
        museumPlace("Регионален исторически музей „Стою Шишков“", "Stoyu Shishkov Regional History Museum", "Смолян", "Smolyan", "Музеят дава контекст за историята, бита и културата на Средните Родопи.", "The museum provides context for the history, everyday life and culture of the Central Rhodopes.", latitude = 41.5764, longitude = 24.7013, imageAssetName = "places/place_rhodope_stoyu_shishkov_museum.png"),
        reservePlace("Архитектурен резерват Широка лъка", "Shiroka Laka Architectural Reserve", "Широка лъка", "Shiroka Laka", "Резерватът е живо и посещаемо място, където родопската архитектура, музика и бит могат да се разгледат в реална среда.", "The reserve is a living and visitable place where Rhodope architecture, music and everyday life can be explored in a real setting.", latitude = 41.6715, longitude = 24.5799, imageAssetName = "places/place_rhodope_shiroka_laka_architectural_reserve.png"),
        museumPlace("Етнографски музей - Широка лъка", "Ethnographic Museum - Shiroka Laka", "Широка лъка", "Shiroka Laka", "Музеят допълва резервата с интериори, носии и предмети от родопския бит.", "The museum complements the reserve with interiors, costumes and objects from Rhodope everyday life.", latitude = 41.6713, longitude = 24.5795, imageAssetName = "places/place_rhodope_shiroka_laka_museum.png"),
    ),
    quiz = listOf(
        quizQuestion("Кой инструмент е най-силно свързан с Родопската област?", "Which instrument is most strongly associated with the Rhodope region?", listOf(t("Каба гайда", "Kaba gaida"), t("Арфа", "Harp"), t("Банджо", "Banjo")), 0),
        quizQuestion("Кой фолклорен събор е символ на региона?", "Which folklore fair is a symbol of the region?", listOf(t("Роженски събор", "Rozhen National Folklore Fair"), t("Празник на розата", "Rose Festival"), t("Пирин Фолк", "Pirin Folk Festival")), 0),
        quizQuestion("Какво отличава родопската носия?", "What distinguishes the Rhodope costume?", listOf(t("По-дълбоки тонове и плътни материи", "Deeper tones and dense fabrics"), t("Само жълти цветове", "Only yellow colors"), t("Липса на пояс", "No sash at all")), 0),
        quizQuestion("Кой обичай носи силен семеен и ритуален заряд в региона?", "Which custom carries a strong family and ritual charge in the region?", listOf(t("Родопска сватба", "Rhodope wedding ritual"), t("Розобер", "Rose picking"), t("Гребане по Дунав", "Rowing on the Danube")), 0),
        quizQuestion("Кой комплекс в Златоград е важна културна точка за региона?", "Which complex in Zlatograd is an important cultural point for the region?", listOf(t("Етнографски ареален комплекс", "Ethnographic Areal Complex"), t("Морска гара", "Sea station"), t("Леден дворец", "Ice palace")), 0),
    ),
    gamification = regionGamification(
        stampBg = "Печат: Родопска област",
        stampEn = "Stamp: Rhodope region",
        badgeBg = "Значка: Гласът на Родопите",
        badgeEn = "Badge: Voice of the Rhodopes",
        badgeDescriptionBg = "Значка за потребител, който е преминал през песните, ритуалите и планинската култура на Родопите.",
        badgeDescriptionEn = "A badge for a user who has completed the songs, rituals and mountain culture of the Rhodopes.",
        progressBg = "Родопската област добавя дълбока музикална и природна линия към етно паспорта и подсилва емоционалния център на приложението.",
        progressEn = "The Rhodope region adds a deep musical and natural line to the Ethno passport and strengthens the emotional center of the app.",
    ),
)

private val dobrudzhaWomenElements = listOf(
    costumeElement("dobrudzha_women_shirt", CostumeElementType.SHIRT, "Риза", "Shirt", "Женската добруджанска риза е светла и често носи шевици, свързани със земята и плодородието.", "The women's Dobrudzha shirt is light and often carries embroidery tied to the land and fertility."),
    costumeElement("dobrudzha_women_apron", CostumeElementType.APRON, "Престилка", "Apron", "Престилката носи житни, слънчеви и растителни мотиви в по-стройна композиция.", "The apron carries wheat, solar and plant motifs in a more streamlined composition."),
    costumeElement("dobrudzha_women_sash", CostumeElementType.SASH, "Пояс", "Sash", "Поясът подчертава талията и дава цвят на светлия силует.", "The sash emphasizes the waist and adds color to the light silhouette."),
    costumeElement("dobrudzha_women_buckles", CostumeElementType.BUCKLES, "Пафти и накити", "Buckles and jewelry", "Металните елементи са по-умерени, но носят празничен акцент.", "The metal elements are more moderate, yet they provide a festive accent."),
    costumeElement("dobrudzha_women_ornament", CostumeElementType.ORNAMENT, "Орнаменти", "Ornaments", "Орнаментите в Добруджа често следват теми за жито, слънце и равнинен хоризонт.", "Ornaments in Dobrudzha often follow themes of wheat, sun and the flat horizon."),
)

private val dobrudzhaMenElements = listOf(
    costumeElement("dobrudzha_men_shirt", CostumeElementType.SHIRT, "Риза", "Shirt", "Мъжката риза е светла и практична, подхождаща на открития равнинен труд.", "The men's shirt is light and practical, suited to work in the open plain."),
    costumeElement("dobrudzha_men_trousers", CostumeElementType.TROUSERS, "Потури", "Baggy trousers", "Потурите са по-леки и функционални, но запазват празничния силует на региона.", "The baggy trousers are lighter and more functional, yet preserve the festive silhouette of the region."),
    costumeElement("dobrudzha_men_vest", CostumeElementType.VEST, "Елече", "Vest", "Елечето добавя структура и по-ясна рамка на мъжката носия.", "The vest adds structure and a clearer frame to the men's costume."),
    costumeElement("dobrudzha_men_belt", CostumeElementType.BELTS, "Колан", "Belt", "Коланът е практичен и стои естествено в по-работния добруджански силует.", "The belt is practical and fits naturally into the more work-oriented Dobrudzha silhouette."),
    costumeElement("dobrudzha_men_leg_wraps", CostumeElementType.LEG_WRAPS, "Калцуни", "Leg wraps", "Калцуните защитават крака при работа и допълват по-практичния добруджански мъжки силует.", "The leg wraps protect the legs during work and complete the more practical men's silhouette of Dobrudzha."),
    costumeElement("dobrudzha_men_shoes", CostumeElementType.LEATHER_SHOES, "Цървули", "Traditional leather shoes", "Цървулите завършват костюма без да отнемат от неговата практичност.", "The leather shoes complete the costume without taking away from its practicality."),
)

val dobrudzhaRegion = EthnoRegion(
    id = EthnoRegionId.DOBRUDZHA,
    name = t("Добруджанска област", "Dobrudzha region"),
    subtitle = t("Равнинен регион с житна символика, просторни танци и силно земеделско усещане", "A plainland region with wheat symbolism, spacious dances and a strong agrarian feel"),
    overview = t("Добруджанската област е тясно свързана с широките полета, земеделието и усещането за простор. Тя се разпознава чрез равнинен ритъм в музиката, мотиви за жито и слънце и обичаи, свързани с труда и плодородието.", "The Dobrudzha region is closely linked to wide fields, agriculture and a sense of spaciousness. It is recognized through a plainland rhythm in music, motifs of wheat and sun, and customs tied to labor and fertility."),
    coverage = t("Обхваща културни средища като Добрич, Тутракан, Силистра, Балчик, Каварна, Тервел и околните добруджански земи.", "It covers cultural centers such as Dobrich, Tutrakan, Silistra, Balchik, Kavarna, Tervel and the surrounding Dobrudzha lands."),
    visualIdentity = t("Визуалният облик на региона е по-лек и светъл, с житни, слънчеви и растителни мотиви, които следват отворения хоризонт на равнината.", "The visual identity of the region is lighter and brighter, with wheat, solar and plant motifs that follow the open horizon of the plain."),
    recognizableFeatures = listOf(
        t("житни и слънчеви мотиви", "wheat and solar motifs"),
        t("музика с просторен танцов ритъм", "music with a spacious dance rhythm"),
        t("обичаи, свързани с дъжд, жътва и плодородие", "customs tied to rain, harvest and fertility"),
    ),
    costumes = listOf(
        CostumeInfo(
            title = t("Женска добруджанска носия", "Women's Dobrudzha costume"),
            shortDescription = t("Женската носия е светла, подредена и носи орнаменти, които напомнят житни полета и слънчева символика.", "The women's costume is light and orderly, carrying ornaments that recall wheat fields and solar symbolism."),
            elements = listOf(t("риза", "shirt"), t("престилка", "apron"), t("пояс", "sash"), t("елече", "vest"), t("колани", "belts"), t("накити", "jewelry"), t("пафти", "buckles"), t("цървули или скарпини", "traditional shoes or skarpini"), t("орнаменти", "ornaments")),
            category = CostumeCategory.WOMEN,
            imageAssetName = "costumes/dobrudzha_women.png",
            elementDetails = visibleCostumeElements(
                dobrudzhaWomenElements,
                "dobrudzha_women_shirt",
                "dobrudzha_women_apron",
                "dobrudzha_women_ornament",
            ),
            hotspots = womenHotspots(
                prefix = "dobrudzha_women",
                shirtX = 0.25f,
                shirtY = 0.31f,
                apronX = 0.50f,
                apronY = 0.58f,
                ornamentX = 0.73f,
                ornamentY = 0.59f,
            ),
        ),
        CostumeInfo(
            title = t("Мъжка добруджанска носия", "Men's Dobrudzha costume"),
            shortDescription = t("Мъжката носия е практична и светла, със силно усещане за работа в открито земеделско пространство.", "The men's costume is practical and light, with a strong sense of work in an open agricultural landscape."),
            elements = listOf(t("риза", "shirt"), t("потури", "baggy trousers"), t("пояс", "sash"), t("елече", "vest"), t("колан", "belt"), t("калцуни", "leg wraps"), t("цървули", "traditional leather shoes"), t("орнаменти", "ornaments")),
            category = CostumeCategory.MEN,
            imageAssetName = "costumes/dobrudzha_men.png",
            elementDetails = visibleCostumeElements(
                dobrudzhaMenElements,
                "dobrudzha_men_shirt",
                "dobrudzha_men_trousers",
                "dobrudzha_men_vest",
                "dobrudzha_men_belt",
                "dobrudzha_men_shoes",
            ),
            hotspots = menHotspots(
                prefix = "dobrudzha_men",
                shirtX = 0.45f,
                shirtY = 0.12f,
                trousersX = 0.42f,
                trousersY = 0.64f,
                vestX = 0.40f,
                vestY = 0.29f,
                beltX = 0.44f,
                beltY = 0.45f,
                shoesX = 0.32f,
                shoesY = 0.94f,
            ),
        ),
    ),
    embroideries = listOf(
        EmbroideryInfo(t("Житен клас", "Wheat ear"), t("Подчертава земеделието и добруджанската идея за плодородна земя.", "It emphasizes agriculture and Dobrudzha's idea of fertile land."), listOf(t("златисто", "golden"), t("зелено", "green"), t("червено", "red")), "embroideries/dobrudzha_wheat_ear.png"),
        EmbroideryInfo(t("Птича двойка", "Pair of birds"), t("Символизира дом, семейство и хармония.", "It symbolizes home, family and harmony."), listOf(t("синьо", "blue"), t("червено", "red"), t("бяло", "white")), "embroideries/dobrudzha_pair_of_birds.png"),
    ),
    traditions = listOf(
        TraditionInfo(t("Лазаруване", "Lazaruvane"), t("пролетен цикъл", "spring cycle"), t("Лазарските песни и обхождането на домовете носят пожелание за здраве и плодородие в селската общност.", "Lazar songs and home visits bring wishes for health and fertility to the village community."), listOf("lazarovden", "tsvetnitsa")),
        TraditionInfo(t("Пеперуда", "Peperuda rain ritual"), t("пролетно-летен земеделски цикъл", "spring-summer agrarian cycle"), t("Обредът за дъжд пази земеделската чувствителност на региона и зависимостта от плодородната година.", "The rain ritual preserves the agrarian sensitivity of the region and its dependence on a fertile year.")),
        TraditionInfo(t("Жътварски обичаи", "Harvest customs"), t("лятно-есенен цикъл", "summer-autumn cycle"), t("Жътварските песни, венците и общата трапеза бележат края на най-важния трудов сезон в равнината.", "Harvest songs, wreaths and the shared meal mark the end of the most important labor season in the plain.")),
    ),
    music = MusicInfo(
        songExamples = listOf(t("Добруджанска ръченица", "Dobrudzha rachenitsa"), t("Добруджанска полска песен", "Dobrudzha field song")),
        instruments = listOf(
            instrument("кавал", "kaval", "instruments/dobrudzha_kaval.png"),
        ),
        audioClips = listOf(
            audioClip("Добруджанска песен", "Dobrudzha song", "https://youtu.be/CWPU7tIRMOs?list=PLSNYoFsAkEpPMNkuq1mkihYTZRAi9CtBF", 0, "Песен, която представя добруджанската песенна и танцова линия.", "A song that presents the Dobrudzha singing and dance style."),
        ),
    ),
    places = listOf(
        museumPlace("Архитектурно-етнографски комплекс „Стария Добрич“", "Old Dobrich Outdoor Museum of Architecture and Ethnography", "Добрич", "Dobrich", "Комплексът показва занаяти, градски бит и характерната добруджанска среда от края на XIX и началото на XX век.", "The complex presents crafts, urban everyday life and the characteristic Dobrudzha environment from the late 19th and early 20th centuries.", latitude = 43.5717, longitude = 27.8271, imageAssetName = "places/place_dobrudzha_old_dobrich.png"),
        houseMuseumPlace("Етнографска къща", "Ethnographic House", "Добрич", "Dobrich", "Къщата представя добруджански обичаи, носии и домашен интериор в автентична възрожденска среда.", "The house presents Dobrudzha customs, costumes and domestic interiors in an authentic Revival setting.", latitude = 43.5711, longitude = 27.8264, imageAssetName = "places/place_dobrudzha_ethnographic_house.png"),
        houseMuseumPlace("Къща и музей „Йордан Йовков“", "The House of Yordan Yovkov and Yordan Yovkov Museum", "Добрич", "Dobrich", "Мястото свързва културната памет на Добруджа с литературата и човешките образи от региона.", "The place links Dobrudzha's cultural memory with literature and the human figures of the region.", latitude = 43.5714, longitude = 27.8276, imageAssetName = "places/place_dobrudzha_yordan_yovkov_house.png"),
        museumPlace("Етнографски музей „Дунавски риболов и лодкостроене“", "Ethnographic Museum Danube Fishing and Boat Construction", "Тутракан", "Tutrakan", "Единственият по рода си музей по Дунав показва рибарския бит, лодките и занаята край реката.", "The only museum of its kind on the Danube presents fishing life, boats and river craft traditions.", latitude = 44.0518, longitude = 26.6115, imageAssetName = "places/place_dobrudzha_danube_fishing_museum.png"),
        reservePlace("Архитектурен комплекс „Рибарска махала“", "Architectural Complex Ribarska Mahala", "Тутракан", "Tutrakan", "Рибарската махала е запазен ансамбъл, който допълва добруджанската картина с дунавски поминък и жилищна среда.", "Ribarska Mahala is a preserved ensemble that complements the Dobrudzha picture with Danubian livelihoods and housing traditions.", latitude = 44.0509, longitude = 26.6110, imageAssetName = "places/place_dobrudzha_ribarska_mahala.png"),
    ),
    quiz = listOf(
        quizQuestion("Кой мотив най-силно свързваме с Добруджа?", "Which motif do we most strongly associate with Dobrudzha?", listOf(t("Житен клас", "Wheat ear"), t("Корал", "Coral"), t("Снежен връх", "Snow peak")), 0),
        quizQuestion("Кой музей в Тутракан е важна културна точка за региона?", "Which museum in Tutrakan is an important cultural point for the region?", listOf(t("Етнографски музей „Дунавски риболов и лодкостроене“", "Ethnographic Museum Danube Fishing and Boat Construction"), t("Космически музей", "Space Museum"), t("Музей на розата", "Rose Museum")), 0),
        quizQuestion("Кой обичай е свързан с молба за дъжд в добруджанската традиция?", "Which custom is linked to asking for rain in Dobrudzha tradition?", listOf(t("Пеперуда", "Peperuda"), t("Розобер", "Rose picking"), t("Пирин Фолк", "Pirin Folk")), 0),
        quizQuestion("Кой град е ключов за културните музеи на Добруджа?", "Which town is key for Dobrudzha's cultural museums?", listOf(t("Добрич", "Dobrich"), t("Банско", "Bansko"), t("Смолян", "Smolyan")), 0),
        quizQuestion("Какво отличава добруджанската музикална линия?", "What distinguishes the Dobrudzha musical line?", listOf(t("Просторен танцов ритъм", "A spacious dance rhythm"), t("Само бавни църковни песнопения", "Only slow church chants"), t("Липса на танцова музика", "A lack of dance music")), 0),
    ),
    gamification = regionGamification(
        stampBg = "Печат: Добруджанска област",
        stampEn = "Stamp: Dobrudzha region",
        badgeBg = "Значка: Пазител на равнината",
        badgeEn = "Badge: Keeper of the plain",
        badgeDescriptionBg = "Значка за потребител, който е преминал през полските мотиви, обичаите и добруджанските музикални ритми.",
        badgeDescriptionEn = "A badge for a user who has completed the field motifs, customs and musical rhythms of Dobrudzha.",
        progressBg = "Добруджа добавя аграрна и дунавска линия към етно паспорта и прави картината на България по-пълна.",
        progressEn = "Dobrudzha adds an agrarian and Danubian line to the Ethno passport and makes the picture of Bulgaria more complete.",
    ),
)
