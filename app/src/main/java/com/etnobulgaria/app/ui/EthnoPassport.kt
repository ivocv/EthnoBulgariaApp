package com.etnobulgaria.app.ui

import android.content.Context
import com.etnobulgaria.app.data.EthnoSQLiteHelper

private const val BADGE_MIN_SCORE = 4

data class RegionPassportStatus(
    val viewed: Boolean = false,
    val bestQuizScore: Int? = null,
    val questionCount: Int = 0,
)

data class EthnoPassportState(
    val regions: Map<EthnoRegionId, RegionPassportStatus>,
) {
    fun regionStatus(regionId: EthnoRegionId): RegionPassportStatus {
        return regions[regionId] ?: RegionPassportStatus()
    }

    fun stampCount(regionIds: List<EthnoRegionId>): Int {
        return regionIds.count { regionStatus(it).viewed }
    }

    fun badgeCount(regionIds: List<EthnoRegionId>): Int {
        return regionIds.count { progressFor(it).badgeUnlocked }
    }

    fun overallProgress(regionIds: List<EthnoRegionId>): Float {
        if (regionIds.isEmpty()) return 0f
        val total = regionIds.sumOf { progressFor(it).progressPercent }
        return total / (regionIds.size * 100f)
    }

    fun progressFor(regionId: EthnoRegionId): RegionPassportProgress {
        val status = regionStatus(regionId)
        val stampUnlocked = status.viewed
        val badgeUnlocked = (status.bestQuizScore ?: 0) >= BADGE_MIN_SCORE
        val progressPercent = when {
            badgeUnlocked -> 100
            stampUnlocked -> 50
            else -> 0
        }
        return RegionPassportProgress(
            stampUnlocked = stampUnlocked,
            badgeUnlocked = badgeUnlocked,
            progressPercent = progressPercent,
            bestQuizScore = status.bestQuizScore,
            questionCount = status.questionCount,
        )
    }
}

data class RegionPassportProgress(
    val stampUnlocked: Boolean,
    val badgeUnlocked: Boolean,
    val progressPercent: Int,
    val bestQuizScore: Int?,
    val questionCount: Int,
)

object EthnoPassportRepository {
    fun loadState(context: Context): EthnoPassportState {
        return EthnoSQLiteHelper.getInstance(context).loadPassportState()
    }

    fun markRegionViewed(context: Context, regionId: EthnoRegionId): EthnoPassportState {
        return EthnoSQLiteHelper.getInstance(context).markRegionViewed(regionId)
    }

    fun recordQuizResult(
        context: Context,
        regionId: EthnoRegionId,
        score: Int,
        questionCount: Int,
    ): EthnoPassportState {
        return EthnoSQLiteHelper.getInstance(context).recordQuizResult(
            regionId = regionId,
            score = score,
            questionCount = questionCount,
        )
    }
}
