package com.example.smartplanner.ui.home

import java.time.*

data class Task(
    val title: String,
    val tag: String,
    val done: Boolean,
    val dueDate: LocalDate,

    // —— new optional fields (defaulted so old code keeps compiling) ——
    val id: String = java.util.UUID.randomUUID().toString(),
    val priority: Priority = Priority.MEDIUM,
    val estimateMin: Int = 30,
    val dueAtTime: LocalTime? = null,                       // for reminders
    val recurrence: Recurrence? = null,                     // repeat rules
    val location: GeoReminder? = null,                      // geofence
    val attachments: List<String> = emptyList(),            // Uri.toString()
    val dependsOn: List<String> = emptyList()               // other task ids
)

enum class Priority { LOW, MEDIUM, HIGH }

data class Recurrence(
    val rule: RepeatRule = RepeatRule.NONE,
    val interval: Int = 1,
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val until: LocalDate? = null
)

enum class RepeatRule { NONE, DAILY, WEEKLY, MONTHLY, WEEKDAY }

data class GeoReminder(val lat: Double, val lon: Double, val radiusM: Float = 200f)
