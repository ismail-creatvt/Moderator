package com.ismail.creatvt.moderator.home

import android.content.Context
import com.facebook.internal.Utility.arrayList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ismail.creatvt.moderator.customviews.data.BarData
import com.ismail.creatvt.moderator.models.CategoryStats
import com.ismail.creatvt.moderator.models.DayStats
import com.ismail.creatvt.moderator.utility.CATEGORIES_KEY
import com.ismail.creatvt.moderator.utility.STATS_KEY
import com.ismail.creatvt.moderator.utility.getDateFormatter
import java.util.*

fun getLastSevenDayValues(
    context: Context,
    category: String,
    listener: (ArrayList<BarData>) -> Unit,
    oldCategory: String? = null,
    eventListeners: HashMap<String, ValueEventListener>? = null
): HashMap<String, ValueEventListener> {
    val dateFormatter = context.resources.getDateFormatter()
    val lastSevenDaysArray = hashMapOf<String, DayStats>()
    val lastSevenDaysKeys = arrayListOf<String>()
    val lastSevenDaysEventListeners = eventListeners ?: hashMapOf()
    var i = 6
    while (i >= 0) {
        val date = Calendar.getInstance()
        date.add(Calendar.DATE, -i)
        lastSevenDaysKeys.add(dateFormatter.format(date.time))
        i--
    }
    val currentUser = FirebaseAuth.getInstance().currentUser
    var oldCategoryRef: DatabaseReference? = null
    if (oldCategory != null) {
        oldCategoryRef =
            FirebaseDatabase.getInstance().getReference(STATS_KEY).child(currentUser?.uid ?: "")
                .child(
                    CATEGORIES_KEY
                ).child(oldCategory)
    }
    val categoryRef =
        FirebaseDatabase.getInstance().getReference(STATS_KEY).child(currentUser?.uid ?: "").child(
            CATEGORIES_KEY
        ).child(category)
    lastSevenDaysKeys.forEach { key ->
        val l = lastSevenDaysEventListeners[key]
        if (l != null) {
            oldCategoryRef?.removeEventListener(l)
        }
        lastSevenDaysEventListeners[key] = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dayStatSnapshot: DataSnapshot) {
                val dayStats = dayStatSnapshot.getValue(DayStats::class.java)
                if (dayStats != null) {
                    lastSevenDaysArray[key] = dayStats
                }
                listener(getBarGraphData(lastSevenDaysKeys, lastSevenDaysArray))
            }

        }
        categoryRef.child("dayStats").child(key)
            .addValueEventListener(lastSevenDaysEventListeners[key]!!)
    }
    return lastSevenDaysEventListeners
}

fun getPieValues(
    category: String,
    listener: (Pair<Int, Int>) -> Unit,
    oldCategory: String? = null,
    eventListener: ValueEventListener? = null
): ValueEventListener {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var oldCategoryRef: DatabaseReference? = null
    if (oldCategory != null) {
        oldCategoryRef =
            FirebaseDatabase.getInstance().getReference(STATS_KEY).child(currentUser?.uid ?: "")
                .child(
                    CATEGORIES_KEY
                ).child(oldCategory)
    }
    val categoryRef =
        FirebaseDatabase.getInstance().getReference(STATS_KEY).child(currentUser?.uid ?: "").child(
            CATEGORIES_KEY
        ).child(category)
    if (eventListener != null) {
        oldCategoryRef?.removeEventListener(eventListener)
    }
    val l = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(categorySnapshot: DataSnapshot) {
            val categoryStats = categorySnapshot.getValue(CategoryStats::class.java)
            listener(Pair(categoryStats?.totalWins ?: 0, categoryStats?.totalLosses ?: 0))
        }
    }
    categoryRef.addValueEventListener(l)
    return l
}

fun getBarGraphData(
    keysArray: ArrayList<String>,
    dayStats: HashMap<String, DayStats>
): ArrayList<BarData> {
    val graphData = arrayList<BarData>()
    keysArray.forEach {
        graphData.add(BarData(dayStats[it]?.totalWins ?: 0, dayStats[it]?.totalLosses ?: 0))
    }
    return graphData
}