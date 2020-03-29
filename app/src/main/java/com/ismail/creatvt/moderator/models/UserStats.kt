package com.ismail.creatvt.moderator.models

import com.ismail.creatvt.moderator.utility.CATEGORY_ALL

data class UserStats(var categories: HashMap<String, CategoryStats> = hashMapOf()) {
    fun incrementData(isWin: Boolean, category: String, date: String) {
        if (!categories.containsKey(category)) {
            categories[category] = CategoryStats()
        }

        if (!categories.containsKey(CATEGORY_ALL)) {
            categories[CATEGORY_ALL] = CategoryStats()
        }

        incrementDayStat(isWin, category, date)
        incrementDayStat(isWin, CATEGORY_ALL, date)
    }

    private fun incrementDayStat(isWin: Boolean, category: String, date: String) {
        categories[category]?.apply {
            if (isWin) {
                totalWins = totalWins.plus(1)
            } else {
                totalLosses = totalLosses.plus(1)
            }
        }

        if (categories[category]?.dayStats == null) {
            categories[category]?.dayStats = hashMapOf()
        }
        if (!categories[category]?.dayStats!!.containsKey(date)) {
            categories[category]?.dayStats?.set(date, DayStats())
        }
        categories[category]?.dayStats?.get(date)?.apply {
            if (isWin) {
                totalWins = totalWins.plus(1)
            } else {
                totalLosses = totalLosses.plus(1)
            }
        }
    }
}

data class CategoryStats(
    var totalLosses: Int = 0,
    var totalWins: Int = 0,
    var dayStats: HashMap<String, DayStats> = hashMapOf()
)

data class DayStats(var totalWins: Int = 0, var totalLosses: Int = 0)
