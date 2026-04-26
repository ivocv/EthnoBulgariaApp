package com.etnobulgaria.app.ui

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.etnobulgaria.app.R
import com.etnobulgaria.app.data.EthnoContentRepository
import com.etnobulgaria.app.ui.theme.EthnoBulgariaTheme
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

private enum class AppSection(val titleRes: Int) {
    HOME(R.string.nav_home),
    MAP(R.string.nav_map),
    MORE(R.string.nav_more),
}

@Composable
fun EthnoBulgariaApp(
    currentThemeMode: AppThemeMode,
    onThemeModeSelected: (AppThemeMode) -> Unit,
) {
    val baseContext = LocalContext.current
    val appContext = remember(baseContext) { baseContext.applicationContext }
    val activityResultRegistryOwner = checkNotNull(LocalActivityResultRegistryOwner.current)
    val (appLanguage, onLanguageSelected) = rememberAppLanguagePreference(baseContext)
    val contentSnapshot = remember(appContext) { EthnoContentRepository.loadSnapshot(appContext) }
    val regions = remember(contentSnapshot.regionCatalog) {
        contentSnapshot.regionCatalog.mapNotNull { it.availableRegion }
    }
    var passportState by remember(appContext) {
        mutableStateOf(EthnoPassportRepository.loadState(appContext))
    }
    var dailyQuestionScoreState by remember(appContext) {
        mutableStateOf(DailyQuestionRepository.loadState(appContext))
    }
    val localizedContext = remember(baseContext, appLanguage) {
        baseContext.createLocalizedContext(appLanguage)
    }
    var currentSection by remember { mutableStateOf(AppSection.HOME) }
    var selectedRegion by remember { mutableStateOf<EthnoRegion?>(null) }
    var isQuizOpen by remember { mutableStateOf(false) }
    var selectedClip by remember { mutableStateOf<AudioClipInfo?>(null) }
    val today = remember { Calendar.getInstance() }
    val regionOfToday = remember(
        contentSnapshot.regionCatalog,
        today.get(Calendar.YEAR),
        today.get(Calendar.DAY_OF_YEAR),
    ) {
        regionOfTheDay(contentSnapshot.regionCatalog, today)
    }
    val traditionSpotlight = remember(
        contentSnapshot.holidayEntries,
        contentSnapshot.calendarSpotlights,
        today.get(Calendar.YEAR),
        today.get(Calendar.DAY_OF_YEAR),
    ) {
        traditionOfTheDay(
            contentSnapshot.holidayEntries,
            contentSnapshot.calendarSpotlights,
            today,
        )
    }
    val dailyMoment = remember(
        contentSnapshot.holidayEntries,
        contentSnapshot.observanceEntries,
        contentSnapshot.calendarSpotlights,
        today.get(Calendar.YEAR),
        today.get(Calendar.DAY_OF_YEAR),
    ) {
        dailyCalendarMoment(
            contentSnapshot.holidayEntries,
            contentSnapshot.observanceEntries,
            contentSnapshot.calendarSpotlights,
            today,
        )
    }
    val dailyQuestion = remember(
        contentSnapshot.dailyQuestionBank,
        today.get(Calendar.YEAR),
        today.get(Calendar.DAY_OF_YEAR),
    ) {
        landmarkQuestionOfTheDay(contentSnapshot.dailyQuestionBank, today)
    }
    val openRegion: (EthnoRegion) -> Unit = { region ->
        passportState = EthnoPassportRepository.markRegionViewed(appContext, region.id)
        selectedRegion = region
        isQuizOpen = false
        selectedClip = null
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalActivityResultRegistryOwner provides activityResultRegistryOwner,
    ) {
        Scaffold(
            topBar = {
                selectedRegion?.let { region ->
                    RegionTopBar(
                        title = if (selectedClip != null) {
                            selectedClip?.title?.asString().orEmpty()
                        } else if (isQuizOpen) {
                            stringResource(R.string.quiz_screen_title, region.name.asString())
                        } else {
                            region.name.asString()
                        },
                        onBack = {
                            if (selectedClip != null) {
                                selectedClip = null
                            } else if (isQuizOpen) {
                                isQuizOpen = false
                            } else {
                                selectedRegion = null
                            }
                        },
                    )
                }
            },
            bottomBar = {
                if (selectedRegion == null) {
                    AppBottomBar(
                        currentSection = currentSection,
                        onSectionSelected = { currentSection = it },
                    )
                }
            },
        ) { innerPadding ->
            selectedRegion?.let { region ->
                if (selectedClip != null) {
                    RegionMediaScreen(
                        clip = selectedClip!!,
                        innerPadding = innerPadding,
                    )
                } else if (isQuizOpen) {
                    RegionQuizScreen(
                        region = region,
                        innerPadding = innerPadding,
                        onQuizCompleted = { score, questionCount ->
                            passportState = EthnoPassportRepository.recordQuizResult(
                                context = appContext,
                                regionId = region.id,
                                score = score,
                                questionCount = questionCount,
                            )
                        },
                    )
                } else {
                    RegionDetailsScreen(
                        region = region,
                        innerPadding = innerPadding,
                        passportState = passportState,
                        onOpenQuiz = { isQuizOpen = true },
                        onOpenClip = { clip -> selectedClip = clip },
                    )
                }
            } ?: when (currentSection) {
                AppSection.HOME -> HomeScreen(
                    innerPadding = innerPadding,
                    today = today,
                    dailyMoment = dailyMoment,
                    dailyQuestion = dailyQuestion,
                    dailyQuestionScoreState = dailyQuestionScoreState,
                    archiveEntries = contentSnapshot.archiveEntries,
                    regionOfTheDay = regionOfToday,
                    traditionOfTheDay = traditionSpotlight,
                    passportState = passportState,
                    regions = regions,
                    onOpenRegion = openRegion,
                    onOpenMap = { currentSection = AppSection.MAP },
                    onDailyQuestionAnswered = { isCorrect ->
                        dailyQuestionScoreState = DailyQuestionRepository.recordAnswer(
                            context = appContext,
                            dateKey = dailyQuestion.dateKey,
                            isCorrect = isCorrect,
                        )
                    },
                )

                AppSection.MAP -> MapScreen(
                    innerPadding = innerPadding,
                    regionPreviews = contentSnapshot.regionCatalog,
                    onOpenRegion = openRegion,
                )

                AppSection.MORE -> MoreScreen(
                    innerPadding = innerPadding,
                    currentLanguage = appLanguage,
                    onLanguageSelected = onLanguageSelected,
                    currentThemeMode = currentThemeMode,
                    onThemeModeSelected = onThemeModeSelected,
                    dailyQuestionScoreState = dailyQuestionScoreState,
                    archiveEntries = contentSnapshot.archiveEntries,
                    passportState = passportState,
                    regions = regions,
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    innerPadding: PaddingValues,
    today: Calendar,
    dailyMoment: DailyCalendarMoment,
    dailyQuestion: DailyLandmarkQuestion,
    dailyQuestionScoreState: DailyQuestionScoreState,
    archiveEntries: List<EthnoArchiveEntry>,
    regionOfTheDay: EthnoRegionPreview,
    traditionOfTheDay: CalendarTraditionSpotlight,
    passportState: EthnoPassportState,
    regions: List<EthnoRegion>,
    onOpenRegion: (EthnoRegion) -> Unit,
    onOpenMap: () -> Unit,
    onDailyQuestionAnswered: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val regionOfTheDayName = regionOfTheDay.name.asString()
    val todayLabel = remember(today.timeInMillis, context.resources.configuration) {
        formatHomeDate(context = context, timeInMillis = today.timeInMillis)
    }
    var isDailyMomentOpen by rememberSaveable(
        today.get(Calendar.YEAR),
        today.get(Calendar.DAY_OF_YEAR),
    ) {
        mutableStateOf(false)
    }
    val regionOfTheDayAvailabilityNote = if (regionOfTheDay.availableRegion == null) {
        stringResource(R.string.home_region_of_day_coming_soon)
    } else {
        ""
    }
    val todayBody = when (dailyMoment.type) {
        DailyCalendarMomentType.HOLIDAY -> {
            if (dailyMoment.nameDays.isNotEmpty()) {
                stringResource(
                    R.string.home_today_body_holiday_nameday,
                    dailyMoment.title.asString(),
                )
            } else {
                stringResource(
                    R.string.home_today_body_holiday,
                    dailyMoment.title.asString(),
                )
            }
        }

        DailyCalendarMomentType.OBSERVANCE -> stringResource(
            R.string.home_today_body_observance,
            dailyMoment.title.asString(),
        )

        DailyCalendarMomentType.SEASONAL -> stringResource(
            R.string.home_today_body_seasonal,
            dailyMoment.title.asString(),
        )
    }
    val dailyMomentTitle = remember(
        context.resources.configuration,
        dailyMoment.title.bg,
        dailyMoment.title.en,
    ) {
        dailyMoment.title.asString(context)
    }
    val dailyMomentSummary = remember(
        context.resources.configuration,
        dailyMoment.summary.bg,
        dailyMoment.summary.en,
    ) {
        dailyMoment.summary.asString(context)
    }
    val dailyMomentSourceLabel = when (dailyMoment.type) {
        DailyCalendarMomentType.HOLIDAY -> stringResource(R.string.home_today_source_holiday)
        DailyCalendarMomentType.OBSERVANCE -> stringResource(R.string.home_today_source_observance)
        DailyCalendarMomentType.SEASONAL -> stringResource(R.string.home_today_source_seasonal)
    }
    val dailyMomentSourceTitle = stringResource(R.string.home_today_source_title)
    val dailyMomentNameDaysTitle = stringResource(R.string.home_today_namedays_title)
    val dailyMomentNameDaysEmpty = stringResource(R.string.home_today_namedays_empty)
    val dailyMomentCloseLabel = stringResource(R.string.action_close)
    val dailyMomentNameDayText = remember(
        context.resources.configuration,
        dailyMoment.nameDays,
    ) {
        dailyMoment.nameDays.joinToString(separator = ", ") { it.asString(context) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            HomeBrandHeader()
        }
        item {
            SectionTitle(
                title = stringResource(R.string.home_focus_title),
                subtitle = stringResource(R.string.home_focus_subtitle),
            )
        }
        item {
            HomeActionCard(
                title = stringResource(R.string.home_today_card_title, todayLabel),
                body = todayBody,
                accent = MaterialTheme.colorScheme.primary,
                actionLabel = stringResource(R.string.home_today_action),
                onAction = { isDailyMomentOpen = true },
            )
        }
        item {
            DailyQuestionCard(
                question = dailyQuestion,
                scoreState = dailyQuestionScoreState,
                archiveEntries = archiveEntries,
                onAnswer = onDailyQuestionAnswered,
            )
        }
        item {
            EthnoArchiveHomeCard(
                scoreState = dailyQuestionScoreState,
                archiveEntries = archiveEntries,
            )
        }
        item {
            HomeActionCard(
                title = stringResource(R.string.home_region_of_day_title),
                body = stringResource(
                    R.string.home_region_of_day_body,
                    regionOfTheDayName,
                    regionOfTheDay.highlight.asString(),
                    regionOfTheDayAvailabilityNote,
                ),
                accent = MaterialTheme.colorScheme.primary,
                actionLabel = if (regionOfTheDay.availableRegion != null) {
                    stringResource(R.string.action_open_region_named, regionOfTheDayName)
                } else {
                    stringResource(R.string.action_open_map)
                },
                onAction = {
                    regionOfTheDay.availableRegion?.let(onOpenRegion) ?: onOpenMap()
                },
            )
        }
        item {
            HighlightCard(
                title = stringResource(R.string.home_tradition_of_day_title),
                body = stringResource(
                    R.string.home_tradition_of_day_body,
                    traditionOfTheDay.title.asString(),
                    traditionOfTheDay.body.asString(),
                ),
                accent = MaterialTheme.colorScheme.secondary,
            )
        }
        item {
            HomeProgressCard(
                passportState = passportState,
                regions = regions,
            )
        }
        item {
            HomeActionCard(
                title = stringResource(R.string.home_map_title),
                body = stringResource(R.string.home_map_body),
                accent = MaterialTheme.colorScheme.tertiary,
                actionLabel = stringResource(R.string.action_open_map),
                onAction = onOpenMap,
            )
        }
    }

    if (isDailyMomentOpen) {
        DailyMomentDialog(
            dateLabel = todayLabel,
            title = dailyMomentTitle,
            summary = dailyMomentSummary,
            sourceTitle = dailyMomentSourceTitle,
            sourceLabel = dailyMomentSourceLabel,
            nameDaysTitle = dailyMomentNameDaysTitle,
            nameDayText = dailyMomentNameDayText,
            nameDaysEmpty = dailyMomentNameDaysEmpty,
            showNameDayEmpty = dailyMoment.type == DailyCalendarMomentType.HOLIDAY &&
                dailyMoment.nameDays.isEmpty(),
            closeLabel = dailyMomentCloseLabel,
            onDismiss = { isDailyMomentOpen = false },
        )
    }
}

@Composable
private fun HomeBrandHeader() {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Image(
                painter = painterResource(id = R.drawable.logo_ethno_bulgaria),
                contentDescription = stringResource(R.string.logo_content_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
                    .clip(RoundedCornerShape(22.dp)),
            )
        }
    }
}

@Composable
private fun MapScreen(
    innerPadding: PaddingValues,
    regionPreviews: List<EthnoRegionPreview>,
    onOpenRegion: (EthnoRegion) -> Unit,
) {
    var selectedRegionPreview by remember { mutableStateOf<EthnoRegionPreview?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            MapPlaceholderCard(
                regionPreviews = regionPreviews,
                onSelectRegion = { selectedRegionPreview = it },
            )
        }
        selectedRegionPreview?.let { preview ->
            item {
                SectionTitle(
                    title = stringResource(R.string.map_selected_title),
                    subtitle = stringResource(R.string.map_selected_subtitle),
                )
            }
            item {
                RegionPreviewCard(
                    region = preview,
                    onOpen = { preview.availableRegion?.let(onOpenRegion) },
                )
            }
        }
        item {
            SectionTitle(
                title = stringResource(R.string.map_regions_title),
                subtitle = stringResource(R.string.map_regions_subtitle),
            )
        }
        items(
            items = regionPreviews,
            key = { it.id },
        ) { region ->
            RegionPreviewCard(
                region = region,
                onOpen = { region.availableRegion?.let(onOpenRegion) },
            )
        }
    }
}

@Composable
private fun RegionDetailsScreen(
    region: EthnoRegion,
    innerPadding: PaddingValues,
    passportState: EthnoPassportState,
    onOpenQuiz: () -> Unit,
    onOpenClip: (AudioClipInfo) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item { RegionHeroCard(region) }
        item { RegionProgressCard(region, passportState.progressFor(region.id)) }
        item {
            SectionTitle(
                title = stringResource(R.string.section_overview),
                subtitle = region.subtitle.asString(),
            )
        }
        item { OverviewCard(region) }
        item {
            SectionTitle(
                title = stringResource(R.string.section_costumes),
                subtitle = stringResource(R.string.section_costumes_subtitle),
            )
        }
        items(region.costumes) { costume -> CostumeCard(costume) }
        item {
            SectionTitle(
                title = stringResource(R.string.section_embroidery),
                subtitle = stringResource(R.string.section_embroidery_subtitle),
            )
        }
        items(region.embroideries) { embroidery -> EmbroideryCard(embroidery) }
        item {
            SectionTitle(
                title = stringResource(R.string.section_traditions),
                subtitle = stringResource(R.string.section_traditions_subtitle),
            )
        }
        items(region.traditions) { tradition -> TraditionCard(tradition) }
        item {
            SectionTitle(
                title = stringResource(R.string.section_music),
                subtitle = stringResource(R.string.section_music_subtitle),
            )
        }
        item { MusicCard(music = region.music, onOpenClip = onOpenClip) }
        item {
            SectionTitle(
                title = stringResource(R.string.section_places),
                subtitle = stringResource(R.string.section_places_subtitle),
            )
        }
        items(region.places) { place -> PlaceCard(place) }
        item {
            SectionTitle(
                title = stringResource(R.string.section_quiz),
                subtitle = stringResource(R.string.section_quiz_subtitle),
            )
        }
        item {
            QuizLaunchCard(
                regionName = region.name.asString(),
                questionCount = region.quiz.size,
                onOpenQuiz = onOpenQuiz,
            )
        }
    }
}

@Composable
private fun RegionQuizScreen(
    region: EthnoRegion,
    innerPadding: PaddingValues,
    onQuizCompleted: (score: Int, questionCount: Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            SectionTitle(
                title = stringResource(R.string.section_quiz),
                subtitle = stringResource(R.string.quiz_screen_subtitle, region.name.asString()),
            )
        }
        item {
            QuizCard(
                questions = region.quiz,
                onQuizCompleted = { score -> onQuizCompleted(score, region.quiz.size) },
            )
        }
    }
}

@Composable
private fun RegionMediaScreen(
    clip: AudioClipInfo,
    innerPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            SectionTitle(
                title = clip.title.asString(),
                subtitle = "",
            )
        }
        item {
            InfoCard {
                InAppMusicPlayer(clip = clip)
            }
        }
    }
}

@Composable
private fun MoreScreen(
    innerPadding: PaddingValues,
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    currentThemeMode: AppThemeMode,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    dailyQuestionScoreState: DailyQuestionScoreState,
    archiveEntries: List<EthnoArchiveEntry>,
    passportState: EthnoPassportState,
    regions: List<EthnoRegion>,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SectionTitle(
            title = stringResource(R.string.more_title),
            subtitle = stringResource(R.string.more_subtitle),
        )
        SettingsCard(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelected,
            currentThemeMode = currentThemeMode,
            onThemeModeSelected = onThemeModeSelected,
        )
        EthnoPassportCard(
            passportState = passportState,
            regions = regions,
        )
        EthnoArchiveCard(
            scoreState = dailyQuestionScoreState,
            archiveEntries = archiveEntries,
        )
        SectionTitle(
            title = stringResource(R.string.more_live_features_title),
            subtitle = "",
        )
        NearbyPlacesCard(regions = regions)
        EthnoDiaryCard()
        HolidayNotificationsCard()
    }
}

@Composable
private fun HeroCard(
    regionName: String,
    onOpenFirstRegion: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.hero_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.hero_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(18.dp))
            FilledTonalButton(onClick = onOpenFirstRegion) {
                Text(text = stringResource(R.string.action_open_region_named, regionName))
            }
        }
    }
}

@Composable
private fun SettingsCard(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    currentThemeMode: AppThemeMode,
    onThemeModeSelected: (AppThemeMode) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(R.string.settings_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.settings_language_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = currentLanguage == AppLanguage.BG,
                    onClick = { onLanguageSelected(AppLanguage.BG) },
                    label = { Text(stringResource(R.string.language_bulgarian)) },
                )
                FilterChip(
                    selected = currentLanguage == AppLanguage.EN,
                    onClick = { onLanguageSelected(AppLanguage.EN) },
                    label = { Text(stringResource(R.string.language_english)) },
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.settings_theme_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = currentThemeMode == AppThemeMode.LIGHT,
                    onClick = { onThemeModeSelected(AppThemeMode.LIGHT) },
                    label = { Text(stringResource(R.string.theme_light)) },
                )
                FilterChip(
                    selected = currentThemeMode == AppThemeMode.DARK,
                    onClick = { onThemeModeSelected(AppThemeMode.DARK) },
                    label = { Text(stringResource(R.string.theme_dark)) },
                )
            }
        }
    }
}

@Composable
private fun EthnoPassportCard(
    passportState: EthnoPassportState,
    regions: List<EthnoRegion>,
) {
    val regionIds = regions.map { it.id }
    val stampCount = passportState.stampCount(regionIds)
    val badgeCount = passportState.badgeCount(regionIds)
    val overallPercent = (passportState.overallProgress(regionIds) * 100).toInt()

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.passport_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(R.string.passport_summary),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { passportState.overallProgress(regionIds) },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(
                    R.string.passport_counts,
                    stampCount,
                    regionIds.size,
                    badgeCount,
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.passport_progress_total, overallPercent),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            regions.forEach { region ->
                PassportRegionCard(
                    region = region,
                    progress = passportState.progressFor(region.id),
                )
            }
        }
    }
}

@Composable
private fun PassportRegionCard(
    region: EthnoRegion,
    progress: RegionPassportProgress,
) {
    val stampTitle = region.gamification?.stampTitle?.asString()
    val badgeTitle = region.gamification?.badgeTitle?.asString()
    val badgeText = if (
        progress.badgeUnlocked &&
        progress.bestQuizScore != null &&
        progress.questionCount > 0
    ) {
        stringResource(
            R.string.passport_badge_unlocked,
            progress.bestQuizScore,
            progress.questionCount,
        )
    } else {
        stringResource(R.string.passport_badge_locked)
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = region.name.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            stampTitle?.let { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            badgeTitle?.let { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            LinearProgressIndicator(
                progress = { progress.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.passport_region_progress, progress.progressPercent),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = if (progress.stampUnlocked) {
                    stringResource(R.string.passport_stamp_unlocked)
                } else {
                    stringResource(R.string.passport_stamp_locked)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = badgeText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun RegionHeroCard(region: EthnoRegion) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = region.name.asString(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = region.subtitle.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = region.overview.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RegionProgressCard(
    region: EthnoRegion,
    progress: RegionPassportProgress,
) {
    val regionName = region.name.asString()
    val progressTitle = region.gamification?.stampTitle?.asString()
        ?: stringResource(R.string.progress_title, regionName)
    val progressBody = region.gamification?.progressDescription?.asString()
        ?: stringResource(R.string.progress_body, regionName)
    val badgeTitle = region.gamification?.badgeTitle?.asString()
    val badgeStateText = if (
        progress.badgeUnlocked &&
        progress.bestQuizScore != null &&
        progress.questionCount > 0
    ) {
        stringResource(
            R.string.passport_badge_unlocked,
            progress.bestQuizScore,
            progress.questionCount,
        )
    } else {
        stringResource(R.string.passport_badge_locked)
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = progressTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = progressBody,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            badgeTitle?.let { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            LinearProgressIndicator(
                progress = { progress.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.passport_region_progress, progress.progressPercent),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = if (progress.stampUnlocked) {
                    stringResource(R.string.passport_stamp_unlocked)
                } else {
                    stringResource(R.string.passport_stamp_locked)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = badgeStateText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun OverviewCard(region: EthnoRegion) {
    InfoCard {
        InfoRow(
            title = stringResource(R.string.overview_coverage),
            body = region.coverage.asString(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        InfoRow(
            title = stringResource(R.string.overview_identity),
            body = region.visualIdentity.asString(),
        )
        if (region.recognizableFeatures.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.overview_recognizable),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            region.recognizableFeatures.forEach { feature ->
                BulletItem(text = feature.asString())
            }
        }
    }
}

@Composable
private fun CostumeCard(costume: CostumeInfo) {
    InfoCard {
        Text(text = costume.title.asString(), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = costume.shortDescription.asString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(14.dp))
        InteractiveCostumeFigure(costume = costume)
        Spacer(modifier = Modifier.height(14.dp))
        costume.elements.forEach { element ->
            BulletItem(text = element.asString())
        }
    }
}

@Composable
private fun EmbroideryCard(embroidery: EmbroideryInfo) {
    val context = LocalContext.current
    val drawableResId = rememberDrawableResId(embroidery.imageAssetName)
    val motifTitle = remember(
        context.resources.configuration,
        embroidery.motif.bg,
        embroidery.motif.en,
    ) {
        embroidery.motif.asString(context)
    }
    var isImageDialogOpen by rememberSaveable(
        embroidery.motif.bg,
        embroidery.motif.en,
    ) {
        mutableStateOf(false)
    }
    val colorsLabel = embroidery.colors
        .takeIf { it.isNotEmpty() }
        ?.joinToString(separator = ", ") { it.asString(context) }

    InfoCard {
        Text(
            text = motifTitle,
            style = MaterialTheme.typography.titleMedium,
            color = if (drawableResId != null) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = if (drawableResId != null) {
                Modifier.clickable { isImageDialogOpen = true }
            } else {
                Modifier
            },
        )
        if (drawableResId != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.embroidery_tap_hint),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = embroidery.meaning.asString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        colorsLabel?.let { colors ->
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.embroidery_colors, colors),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    if (drawableResId != null && isImageDialogOpen) {
        Dialog(onDismissRequest = { isImageDialogOpen = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        text = motifTitle,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Image(
                        painter = painterResource(id = drawableResId),
                        contentDescription = motifTitle,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .clip(RoundedCornerShape(20.dp)),
                    )
                    FilledTonalButton(
                        onClick = { isImageDialogOpen = false },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(text = stringResource(R.string.action_close))
                    }
                }
            }
        }
    }
}

@Composable
private fun TraditionCard(tradition: TraditionInfo) {
    InfoCard {
        Text(text = tradition.title.asString(), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = tradition.season.asString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = tradition.summary.asString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MusicCard(
    music: MusicInfo,
    onOpenClip: (AudioClipInfo) -> Unit,
) {
    val context = LocalContext.current
    var selectedInstrument by remember { mutableStateOf<InstrumentInfo?>(null) }
    val visibleInstruments = remember(music.instruments) {
        music.instruments.filter { it.imageAssetName != null }.ifEmpty { music.instruments }
    }

    InfoCard {
        Text(text = stringResource(R.string.music_songs_title), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(10.dp))
        music.songExamples.forEach { song -> BulletItem(text = song.asString()) }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.music_instruments_title), style = MaterialTheme.typography.titleMedium)
        if (visibleInstruments.any { it.imageAssetName != null }) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.instrument_tap_hint),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        visibleInstruments.forEach { instrument ->
            InstrumentLine(
                instrument = instrument,
                onOpenPreview = { selectedInstrument = it },
            )
        }
        if (music.audioClips.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.music_audio_title), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(10.dp))
            music.audioClips.forEach { clip ->
                Text(
                    text = clip.title.asString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = clip.note.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(10.dp))
                FilledTonalButton(
                    onClick = { onOpenClip(clip) },
                    enabled = !clip.assetPath.isNullOrBlank(),
                ) {
                    Text(text = stringResource(R.string.action_open_song))
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }

    selectedInstrument?.let { instrument ->
        val drawableResId = rememberDrawableResId(instrument.imageAssetName)
        val instrumentTitle = remember(
            context.resources.configuration,
            instrument.name.bg,
            instrument.name.en,
        ) {
            instrument.name.asString(context)
        }
        if (drawableResId != null) {
            Dialog(onDismissRequest = { selectedInstrument = null }) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = instrumentTitle,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Image(
                            painter = painterResource(id = drawableResId),
                            contentDescription = instrumentTitle,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 360.dp)
                                .clip(RoundedCornerShape(20.dp)),
                        )
                        FilledTonalButton(
                            onClick = { selectedInstrument = null },
                            modifier = Modifier.align(Alignment.End),
                        ) {
                            Text(text = stringResource(R.string.action_close))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstrumentLine(
    instrument: InstrumentInfo,
    onOpenPreview: (InstrumentInfo) -> Unit,
) {
    val drawableResId = rememberDrawableResId(instrument.imageAssetName)
    Text(
        text = "\u2022 ${instrument.name.asString()}",
        style = MaterialTheme.typography.bodyLarge,
        color = if (drawableResId != null) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = if (drawableResId != null) {
            Modifier
                .fillMaxWidth()
                .clickable { onOpenPreview(instrument) }
                .padding(bottom = 6.dp)
        } else {
            Modifier.padding(bottom = 6.dp)
        },
    )
}

@Composable
private fun PlaceCard(place: CulturalPlaceInfo) {
    val context = LocalContext.current
    val drawableResId = rememberDrawableResId(place.imageAssetName)
    val placeTitle = remember(
        context.resources.configuration,
        place.name.bg,
        place.name.en,
    ) {
        place.name.asString(context)
    }
    var isImageDialogOpen by rememberSaveable(
        place.name.bg,
        place.name.en,
    ) {
        mutableStateOf(false)
    }

    InfoCard {
        Text(
            text = placeTitle,
            style = MaterialTheme.typography.titleLarge,
            color = if (drawableResId != null) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = if (drawableResId != null) {
                Modifier.clickable { isImageDialogOpen = true }
            } else {
                Modifier
            },
        )
        if (drawableResId != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.place_tap_hint),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${place.type.asString()} | ${place.location.asString()}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = place.summary.asString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    if (drawableResId != null && isImageDialogOpen) {
        Dialog(onDismissRequest = { isImageDialogOpen = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        text = placeTitle,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Image(
                        painter = painterResource(id = drawableResId),
                        contentDescription = placeTitle,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .clip(RoundedCornerShape(20.dp)),
                    )
                    FilledTonalButton(
                        onClick = { isImageDialogOpen = false },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(text = stringResource(R.string.action_close))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizCard(
    questions: List<QuizQuestion>,
    onQuizCompleted: (score: Int) -> Unit,
) {
    val selectedAnswers = remember { mutableStateMapOf<Int, Int>() }
    var showResult by remember { mutableStateOf(false) }
    val allAnswered = selectedAnswers.size == questions.size
    val score = questions.indices.count { index ->
        selectedAnswers[index] == questions[index].correctAnswerIndex
    }

    InfoCard {
        Text(
            text = stringResource(R.string.quiz_intro),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))

        questions.forEachIndexed { questionIndex, question ->
            Text(text = question.question.asString(), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(10.dp))
            question.options.forEachIndexed { optionIndex, option ->
                FilterChip(
                    selected = selectedAnswers[questionIndex] == optionIndex,
                    onClick = { selectedAnswers[questionIndex] = optionIndex },
                    label = { Text(option.asString()) },
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        FilledTonalButton(
            onClick = {
                showResult = true
                onQuizCompleted(score)
            },
            enabled = allAnswered,
        ) {
            Text(text = stringResource(R.string.quiz_check_answers))
        }

        if (!allAnswered) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.quiz_complete_all),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (showResult) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.quiz_score, score, questions.size),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun QuizLaunchCard(
    regionName: String,
    questionCount: Int,
    onOpenQuiz: () -> Unit,
) {
    InfoCard {
        Text(
            text = stringResource(R.string.quiz_launch_body, regionName),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.quiz_launch_meta, questionCount),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(onClick = onOpenQuiz) {
            Text(text = stringResource(R.string.action_open_quiz))
        }
    }
}

@Composable
private fun HomeProgressCard(
    passportState: EthnoPassportState,
    regions: List<EthnoRegion>,
) {
    val regionIds = regions.map { it.id }
    val stampCount = passportState.stampCount(regionIds)
    val badgeCount = passportState.badgeCount(regionIds)
    val overallPercent = (passportState.overallProgress(regionIds) * 100).toInt()

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.home_passport_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(R.string.home_passport_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { passportState.overallProgress(regionIds) },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(
                    R.string.passport_counts,
                    stampCount,
                    regionIds.size,
                    badgeCount,
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.passport_progress_total, overallPercent),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    body: String,
    accent: Color,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accent),
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FilledTonalButton(onClick = onAction) {
                Text(text = actionLabel)
            }
        }
    }
}

@Composable
private fun DailyQuestionCard(
    question: DailyLandmarkQuestion,
    scoreState: DailyQuestionScoreState,
    archiveEntries: List<EthnoArchiveEntry>,
    onAnswer: (Boolean) -> Unit,
) {
    val drawableResId = rememberDrawableResId(question.imageAssetName)
    val hasAnsweredToday = scoreState.hasAnswered(question.dateKey)
    val archiveProgress = remember(scoreState.totalPoints, archiveEntries) {
        ethnoArchiveProgress(
            entries = archiveEntries,
            totalPoints = scoreState.totalPoints,
        )
    }
    var selectedAnswerIndex by rememberSaveable(question.dateKey) { mutableStateOf<Int?>(null) }
    val correctAnswer = question.correctAnswer.asString()
    val resultText = when {
        !hasAnsweredToday -> null
        scoreState.answeredCorrectFor(question.dateKey) || selectedAnswerIndex == question.correctAnswerIndex ->
            stringResource(R.string.home_daily_question_correct)

        else -> stringResource(R.string.home_daily_question_wrong, correctAnswer)
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary),
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = stringResource(R.string.home_daily_question_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = stringResource(
                    R.string.home_daily_question_subtitle,
                    scoreState.totalPoints,
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = if (archiveProgress.nextEntry != null) {
                    stringResource(
                        R.string.home_daily_question_unlock_hint,
                        archiveProgress.nextEntry.title.asString(),
                        archiveProgress.pointsToNextUnlock ?: 0,
                    )
                } else {
                    stringResource(R.string.home_daily_question_unlock_complete)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            if (drawableResId != null) {
                Image(
                    painter = painterResource(id = drawableResId),
                    contentDescription = stringResource(R.string.home_daily_question_image_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp)),
                )
            } else {
                Text(
                    text = stringResource(R.string.home_daily_question_image_missing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Text(
                text = stringResource(R.string.home_daily_question_prompt),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(
                    R.string.home_daily_question_region,
                    question.regionName.asString(),
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            question.options.forEachIndexed { index, option ->
                FilledTonalButton(
                    onClick = {
                        selectedAnswerIndex = index
                        onAnswer(index == question.correctAnswerIndex)
                    },
                    enabled = !hasAnsweredToday,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = option.asString())
                }
            }
            resultText?.let { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (scoreState.answeredCorrectFor(question.dateKey)) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            }
            Text(
                text = stringResource(R.string.home_daily_question_points, scoreState.totalPoints),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EthnoArchiveHomeCard(
    scoreState: DailyQuestionScoreState,
    archiveEntries: List<EthnoArchiveEntry>,
) {
    val progress = remember(scoreState.totalPoints, archiveEntries) {
        ethnoArchiveProgress(
            entries = archiveEntries,
            totalPoints = scoreState.totalPoints,
        )
    }
    val latestUnlocked = progress.latestUnlockedEntry

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.archive_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(
                    R.string.archive_home_summary,
                    progress.unlockedCount,
                    progress.totalCount,
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { progress.progressToNextUnlock() },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = if (progress.nextEntry != null) {
                    stringResource(
                        R.string.archive_next_unlock,
                        progress.nextEntry.title.asString(),
                        progress.pointsToNextUnlock ?: 0,
                    )
                } else {
                    stringResource(R.string.archive_all_unlocked)
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            latestUnlocked?.let { entry ->
                Text(
                    text = stringResource(R.string.archive_latest_unlocked, entry.title.asString()),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = entry.summary.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun EthnoArchiveCard(
    scoreState: DailyQuestionScoreState,
    archiveEntries: List<EthnoArchiveEntry>,
) {
    val progress = remember(scoreState.totalPoints, archiveEntries) {
        ethnoArchiveProgress(
            entries = archiveEntries,
            totalPoints = scoreState.totalPoints,
        )
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.archive_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(
                    R.string.archive_more_summary,
                    progress.unlockedCount,
                    progress.totalCount,
                    scoreState.totalPoints,
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { progress.progressToNextUnlock() },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = if (progress.nextEntry != null) {
                    stringResource(
                        R.string.archive_next_unlock,
                        progress.nextEntry.title.asString(),
                        progress.pointsToNextUnlock ?: 0,
                    )
                } else {
                    stringResource(R.string.archive_all_unlocked)
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            archiveEntries.forEach { entry ->
                EthnoArchiveEntryCard(
                    entry = entry,
                    unlocked = scoreState.totalPoints >= entry.unlockPoints,
                )
            }
        }
    }
}

@Composable
private fun EthnoArchiveEntryCard(
    entry: EthnoArchiveEntry,
    unlocked: Boolean,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            },
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = entry.title.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (unlocked) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
            Text(
                text = if (unlocked) {
                    stringResource(R.string.archive_entry_unlocked, entry.unlockPoints)
                } else {
                    stringResource(R.string.archive_entry_locked, entry.unlockPoints)
                },
                style = MaterialTheme.typography.labelLarge,
                color = if (unlocked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            if (unlocked) {
                Text(
                    text = entry.summary.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                entry.highlights.forEach { item ->
                    Text(
                        text = "• ${item.asString()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.archive_entry_locked_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DailyMomentDialog(
    dateLabel: String,
    title: String,
    summary: String,
    sourceTitle: String,
    sourceLabel: String,
    nameDaysTitle: String,
    nameDayText: String,
    nameDaysEmpty: String,
    showNameDayEmpty: Boolean,
    closeLabel: String,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
                InfoRow(
                    title = sourceTitle,
                    body = sourceLabel,
                )
                if (nameDayText.isNotBlank()) {
                    InfoRow(
                        title = nameDaysTitle,
                        body = nameDayText,
                    )
                } else if (showNameDayEmpty) {
                    InfoRow(
                        title = nameDaysTitle,
                        body = nameDaysEmpty,
                    )
                }
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FilledTonalButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(text = closeLabel)
                }
            }
        }
    }
}

@Composable
private fun HighlightCard(title: String, body: String, accent: Color) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accent),
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatHomeDate(context: android.content.Context, timeInMillis: Long): String {
    val formatter = SimpleDateFormat(
        "d MMMM",
        context.readAppLanguagePreference().locale,
    )
    return formatter.format(Date(timeInMillis))
}

@Composable
private fun RegionChips(
    regions: List<EthnoRegionPreview>,
    onOpenRegion: (EthnoRegion) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            regions.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    rowItems.forEach { region ->
                        val isAvailable = region.availableRegion != null
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 88.dp)
                                .clickable(
                                    enabled = isAvailable,
                                    onClick = { region.availableRegion?.let(onOpenRegion) },
                                ),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAvailable) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
                                },
                            ),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(88.dp)
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = region.name.asString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    color = if (isAvailable) {
                                        MaterialTheme.colorScheme.onSurface
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                )
                            }
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MapPlaceholderCard(
    regionPreviews: List<EthnoRegionPreview>,
    onSelectRegion: (EthnoRegionPreview) -> Unit,
) {
    InteractiveMapCard(
        regionPreviews = regionPreviews,
        onSelectRegion = onSelectRegion,
    )
}

@Composable
private fun RegionPreviewCard(
    region: EthnoRegionPreview,
    onOpen: () -> Unit,
) {
    val isAvailable = region.availableRegion != null

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = region.name.asString(), style = MaterialTheme.typography.titleLarge)
            Text(
                text = region.highlight.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            FilledTonalButton(
                onClick = onOpen,
                enabled = isAvailable,
            ) {
                Text(
                    text = if (isAvailable) {
                        stringResource(R.string.action_open_region)
                    } else {
                        stringResource(R.string.action_coming_soon)
                    },
                )
            }
        }
    }
}

@Composable
private fun RegionTopBar(
    title: String,
    onBack: () -> Unit,
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilledTonalButton(onClick = onBack) {
                Text(text = stringResource(R.string.action_back))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun AppBottomBar(
    currentSection: AppSection,
    onSectionSelected: (AppSection) -> Unit,
) {
    Surface(shadowElevation = 10.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AppSection.entries.forEach { section ->
                val selected = section == currentSection
                val background = if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
                val contentColor = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(background)
                        .clickable { onSectionSelected(section) }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(section.titleRes),
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    body: String,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = content,
        )
    }
}

@Composable
private fun InfoRow(title: String, body: String) {
    Text(text = title, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun BulletItem(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EthnoBulgariaAppPreview() {
    EthnoBulgariaTheme {
        EthnoBulgariaApp(
            currentThemeMode = AppThemeMode.LIGHT,
            onThemeModeSelected = {},
        )
    }
}
