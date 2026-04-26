package com.etnobulgaria.app.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.etnobulgaria.app.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.ArrayDeque
import kotlin.math.roundToInt

@Composable
internal fun InteractiveCostumeFigure(costume: CostumeInfo) {
    val drawableResId = rememberDrawableResId(costume.imageAssetName)
    var selectedElementId by remember(costume.title.bg, costume.title.en) {
        mutableStateOf(costume.elementDetails.firstOrNull()?.id ?: costume.hotspots.firstOrNull()?.elementId)
    }
    val selectedElement = costume.elementDetails.firstOrNull { it.id == selectedElementId }
        ?: costume.elementDetails.firstOrNull()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.costume_interaction_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = stringResource(R.string.costume_interaction_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (drawableResId != null) {
            val painter = painterResource(id = drawableResId)
            val aspectRatio = remember(drawableResId) {
                val intrinsicSize = painter.intrinsicSize
                if (intrinsicSize.width > 0f && intrinsicSize.height > 0f) {
                    intrinsicSize.width / intrinsicSize.height
                } else {
                    0.68f
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Image(
                    painter = painter,
                    contentDescription = costume.title.asString(),
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize(),
                )

                costume.hotspots.forEach { hotspot ->
                    val isSelected = hotspot.elementId == selectedElement?.id
                    val indicatorSize = 18.dp
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (maxWidth * hotspot.relativeX) - indicatorSize / 2,
                                y = (maxHeight * hotspot.relativeY) - indicatorSize / 2,
                            )
                            .size(indicatorSize)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.86f)
                                },
                            )
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { selectedElementId = hotspot.elementId },
                    )
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Text(
                    text = stringResource(R.string.costume_image_missing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        selectedElement?.let { element ->
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.56f)),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = element.title.asString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = element.summary.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
internal fun InteractiveMapCard(
    regionPreviews: List<EthnoRegionPreview>,
    onSelectRegion: (EthnoRegionPreview) -> Unit,
) {
    val context = LocalContext.current
    val drawableResId = rememberDrawableResId(ethnoMapBlueprint.assetPath)
    val availableRegions = remember(regionPreviews) {
        regionPreviews.associateBy { it.id }
    }
    val painter = drawableResId?.let { painterResource(id = it) }
    val hitTestModel = remember(context, drawableResId) {
        drawableResId?.let { createMapHitTestModel(context, it, ethnoMapBlueprint.areas) }
    }
    val aspectRatio = remember(drawableResId, painter) {
        val intrinsicSize = painter?.intrinsicSize
        if (intrinsicSize != null && intrinsicSize.width > 0f && intrinsicSize.height > 0f) {
            intrinsicSize.width / intrinsicSize.height
        } else {
            1.28f
        }
    }

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = ethnoMapBlueprint.fallbackTitle.asString(),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = ethnoMapBlueprint.fallbackSummary.asString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f))
                    .pointerInput(availableRegions, hitTestModel) {
                        detectTapGestures { tapOffset ->
                            val x = tapOffset.x / size.width.toFloat()
                            val y = tapOffset.y / size.height.toFloat()
                            val tappedRegionId = hitTestModel?.resolveRegion(x, y)
                                ?: ethnoMapBlueprint.areas.firstOrNull { area ->
                                pointInPolygon(MapPoint(x, y), area.touchPolygon)
                                }?.regionId
                            tappedRegionId?.let { regionId ->
                                availableRegions[regionId]?.let(onSelectRegion)
                            }
                        }
                    },
            ) {
                if (painter != null) {
                    Image(
                        painter = painter,
                        contentDescription = stringResource(R.string.map_regions_title),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.matchParentSize(),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.24f)),
                    )
                }
            }

            if (drawableResId == null) {
                Text(
                    text = stringResource(R.string.map_asset_missing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = stringResource(R.string.map_interactive_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
internal fun InAppMusicPlayer(clip: AudioClipInfo) {
    val videoId = remember(clip.assetPath) { clip.assetPath?.let(::toYouTubeVideoId) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (videoId != null) {
            InAppYouTubeFrame(videoId = videoId)
        } else {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Text(
                    text = stringResource(R.string.music_embed_unavailable),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Composable
internal fun rememberDrawableResId(assetPath: String?): Int? {
    val context = LocalContext.current
    return remember(context, assetPath) {
        val resourceName = assetPath
            ?.substringAfterLast('/')
            ?.substringBeforeLast('.')
            ?.lowercase()
        resourceName
            ?.takeIf { it.isNotBlank() }
            ?.let { context.resources.getIdentifier(it, "drawable", context.packageName) }
            ?.takeIf { it != 0 }
    }
}

private fun pointInPolygon(point: MapPoint, polygon: List<MapPoint>): Boolean {
    if (polygon.size < 3) return false

    var isInside = false
    var previousIndex = polygon.lastIndex

    polygon.indices.forEach { index ->
        val current = polygon[index]
        val previous = polygon[previousIndex]

        val intersects = ((current.y > point.y) != (previous.y > point.y)) &&
            (point.x < (previous.x - current.x) * (point.y - current.y) / ((previous.y - current.y) + 0.00001f) + current.x)

        if (intersects) {
            isInside = !isInside
        }
        previousIndex = index
    }

    return isInside
}

private data class MapHitTestModel(
    val width: Int,
    val height: Int,
    val assignments: IntArray,
    val regionIds: List<EthnoRegionId>,
) {
    fun resolveRegion(normalizedX: Float, normalizedY: Float): EthnoRegionId? {
        if (width <= 0 || height <= 0) return null

        val x = (normalizedX.coerceIn(0f, 1f) * (width - 1)).roundToInt()
        val y = (normalizedY.coerceIn(0f, 1f) * (height - 1)).roundToInt()
        val directIndex = assignments[(y * width) + x]
        if (directIndex >= 0) {
            return regionIds[directIndex]
        }

        for (radius in 1..18) {
            val minX = (x - radius).coerceAtLeast(0)
            val maxX = (x + radius).coerceAtMost(width - 1)
            val minY = (y - radius).coerceAtLeast(0)
            val maxY = (y + radius).coerceAtMost(height - 1)

            for (scanX in minX..maxX) {
                val topIndex = assignments[(minY * width) + scanX]
                if (topIndex >= 0) return regionIds[topIndex]

                val bottomIndex = assignments[(maxY * width) + scanX]
                if (bottomIndex >= 0) return regionIds[bottomIndex]
            }

            for (scanY in (minY + 1) until maxY) {
                val leftIndex = assignments[(scanY * width) + minX]
                if (leftIndex >= 0) return regionIds[leftIndex]

                val rightIndex = assignments[(scanY * width) + maxX]
                if (rightIndex >= 0) return regionIds[rightIndex]
            }
        }

        return null
    }
}

private data class MapFloodNode(
    val pixelIndex: Int,
    val areaIndex: Int,
)

private fun createMapHitTestModel(
    context: Context,
    drawableResId: Int,
    areas: List<MapRegionArea>,
): MapHitTestModel? {
    val bitmap = BitmapFactory.decodeResource(
        context.resources,
        drawableResId,
        BitmapFactory.Options().apply { inScaled = false },
    ) ?: return null

    return try {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= 0 || height <= 0) {
            return null
        }

        val assignments = IntArray(width * height) { -1 }
        val queue = ArrayDeque<MapFloodNode>()

        areas.forEachIndexed { areaIndex, area ->
            val seed = findNearestTraversableSeed(
                bitmapWidth = width,
                bitmapHeight = height,
                centerX = (area.center.x * (width - 1)).roundToInt(),
                centerY = (area.center.y * (height - 1)).roundToInt(),
                pixelAt = { x, y -> bitmap.getPixel(x, y) },
            ) ?: return@forEachIndexed

            val seedIndex = (seed.second * width) + seed.first
            if (assignments[seedIndex] == -1) {
                assignments[seedIndex] = areaIndex
                queue.addLast(MapFloodNode(seedIndex, areaIndex))
            }
        }

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val pixelIndex = node.pixelIndex
            val areaIndex = node.areaIndex

            if (assignments[pixelIndex] != areaIndex) continue

            val x = pixelIndex % width
            val y = pixelIndex / width

            expandNeighbor(width, height, x - 1, y, areaIndex, assignments, queue) { px, py ->
                !isMapBarrier(bitmap.getPixel(px, py))
            }
            expandNeighbor(width, height, x + 1, y, areaIndex, assignments, queue) { px, py ->
                !isMapBarrier(bitmap.getPixel(px, py))
            }
            expandNeighbor(width, height, x, y - 1, areaIndex, assignments, queue) { px, py ->
                !isMapBarrier(bitmap.getPixel(px, py))
            }
            expandNeighbor(width, height, x, y + 1, areaIndex, assignments, queue) { px, py ->
                !isMapBarrier(bitmap.getPixel(px, py))
            }
        }

        MapHitTestModel(
            width = width,
            height = height,
            assignments = assignments,
            regionIds = areas.map { it.regionId },
        )
    } finally {
        bitmap.recycle()
    }
}

private fun expandNeighbor(
    width: Int,
    height: Int,
    x: Int,
    y: Int,
    areaIndex: Int,
    assignments: IntArray,
    queue: ArrayDeque<MapFloodNode>,
    isTraversable: (Int, Int) -> Boolean,
) {
    if (x !in 0 until width || y !in 0 until height) return

    val neighborIndex = (y * width) + x
    if (assignments[neighborIndex] != -1) return
    if (!isTraversable(x, y)) return

    assignments[neighborIndex] = areaIndex
    queue.addLast(MapFloodNode(neighborIndex, areaIndex))
}

private fun findNearestTraversableSeed(
    bitmapWidth: Int,
    bitmapHeight: Int,
    centerX: Int,
    centerY: Int,
    pixelAt: (Int, Int) -> Int,
): Pair<Int, Int>? {
    val clampedX = centerX.coerceIn(0, bitmapWidth - 1)
    val clampedY = centerY.coerceIn(0, bitmapHeight - 1)

    if (!isMapBarrier(pixelAt(clampedX, clampedY))) {
        return clampedX to clampedY
    }

    for (radius in 1..24) {
        val minX = (clampedX - radius).coerceAtLeast(0)
        val maxX = (clampedX + radius).coerceAtMost(bitmapWidth - 1)
        val minY = (clampedY - radius).coerceAtLeast(0)
        val maxY = (clampedY + radius).coerceAtMost(bitmapHeight - 1)

        for (x in minX..maxX) {
            if (!isMapBarrier(pixelAt(x, minY))) return x to minY
            if (!isMapBarrier(pixelAt(x, maxY))) return x to maxY
        }
        for (y in (minY + 1) until maxY) {
            if (!isMapBarrier(pixelAt(minX, y))) return minX to y
            if (!isMapBarrier(pixelAt(maxX, y))) return maxX to y
        }
    }

    return null
}

private fun isMapBarrier(color: Int): Boolean {
    val alpha = AndroidColor.alpha(color)
    if (alpha < 24) return true

    val red = AndroidColor.red(color).toFloat() / 255f
    val green = AndroidColor.green(color).toFloat() / 255f
    val blue = AndroidColor.blue(color).toFloat() / 255f
    val maxChannel = maxOf(red, green, blue)
    val minChannel = minOf(red, green, blue)
    val saturation = if (maxChannel == 0f) 0f else (maxChannel - minChannel) / maxChannel
    val luminance = (0.2126f * red) + (0.7152f * green) + (0.0722f * blue)

    return (saturation < 0.16f && luminance < 0.84f) || luminance < 0.28f
}

@Composable
private fun InAppYouTubeFrame(videoId: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var listenerAttached by remember { mutableStateOf(false) }
    var currentYouTubePlayer by remember {
        mutableStateOf<com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer?>(null)
    }
    var loadedVideoId by remember { mutableStateOf<String?>(null) }
    val playerView = remember(context) {
        YouTubePlayerView(context)
    }

    DisposableEffect(lifecycleOwner, playerView) {
        lifecycleOwner.lifecycle.addObserver(playerView)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(playerView)
            playerView.release()
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            factory = {
                if (!listenerAttached) {
                    playerView.addYouTubePlayerListener(
                        object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                                currentYouTubePlayer = youTubePlayer
                                if (loadedVideoId != videoId) {
                                    youTubePlayer.loadVideo(videoId, 0f)
                                    loadedVideoId = videoId
                                }
                            }
                        },
                    )
                    listenerAttached = true
                }
                playerView
            },
            update = {
                currentYouTubePlayer?.let { youTubePlayer ->
                    if (loadedVideoId != videoId) {
                        youTubePlayer.loadVideo(videoId, 0f)
                        loadedVideoId = videoId
                    }
                }
            },
        )
    }
}

private fun toYouTubeVideoId(url: String): String? {
    val uri = Uri.parse(url)
    return when {
        uri.host?.contains("youtu.be") == true -> uri.lastPathSegment
        uri.host?.contains("youtube.com") == true -> uri.getQueryParameter("v")
        else -> null
    }
}
