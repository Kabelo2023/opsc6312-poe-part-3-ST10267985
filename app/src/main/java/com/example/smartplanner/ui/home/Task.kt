package com.example.smartplanner.ui.home

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

/** How a task repeats. */
enum class RepeatRule { NONE, DAILY, WEEKLY, CUSTOM } // CUSTOM = specific days

/** Optional priority — keep if you already use it elsewhere. */
enum class Priority { LOW, MEDIUM, HIGH }

/** Optional geo-reminder holder — kept for compatibility. */
data class GeoReminder(val lat: Double, val lon: Double, val radiusM: Float = 200f)

/**
 * Unified Task model.
 * - Keeps previous optional fields (dueAtTime, dependsOn, etc.) so old code compiles.
 * - Adds recurrence via (repeat, customDays).
 */
data class Task(
    val title: String,
    val tag: String,
    var done: Boolean,
    val dueDate: LocalDate,

    // Recurrence (new)
    val repeat: RepeatRule = RepeatRule.NONE,
    val customDays: Set<DayOfWeek> = emptySet(),   // only used when repeat = CUSTOM

    // Common/compat fields
    val id: String = UUID.randomUUID().toString(),
    val priority: Priority = Priority.MEDIUM,
    val estimateMin: Int = 30,
    val dueAtTime: LocalTime? = null,              // for reminders
    val location: GeoReminder? = null,             // geofence
    val attachments: List<String> = emptyList(),   // Uri.toString()
    val dependsOn: List<String> = emptyList()      // other task ids
)
