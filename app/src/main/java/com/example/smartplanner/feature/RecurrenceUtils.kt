package com.example.smartplanner.feature

import com.example.smartplanner.ui.home.*
import java.time.*

/** True if a task is considered scheduled for [date] given its base dueDate and recurrence. */
fun occursOn(task: Task, date: LocalDate): Boolean {
    val r = task.recurrence ?: return task.dueDate == date
    if (r.rule == RepeatRule.NONE) return task.dueDate == date
    if (r.until != null && date.isAfter(r.until)) return false
    if (date.isBefore(task.dueDate)) return false

    return when (r.rule) {
        RepeatRule.DAILY -> {
            val days = java.time.temporal.ChronoUnit.DAYS.between(task.dueDate, date).toInt()
            days % r.interval == 0
        }
        RepeatRule.WEEKLY -> {
            val weeks = java.time.temporal.ChronoUnit.WEEKS.between(task.dueDate, date).toInt()
            val matchesSet = r.daysOfWeek.isEmpty() || r.daysOfWeek.contains(date.dayOfWeek)
            weeks % r.interval == 0 && matchesSet
        }
        RepeatRule.MONTHLY -> {
            val months = java.time.temporal.ChronoUnit.MONTHS.between(
                task.dueDate.withDayOfMonth(1),
                date.withDayOfMonth(1)
            ).toInt()
            months % r.interval == 0 && date.dayOfMonth == task.dueDate.dayOfMonth
        }
        RepeatRule.WEEKDAY -> date.dayOfWeek !in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        RepeatRule.NONE -> task.dueDate == date
    }
}
