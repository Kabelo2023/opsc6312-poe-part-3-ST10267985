package com.example.smartplanner.feature

import com.example.smartplanner.ui.home.RepeatRule
import com.example.smartplanner.ui.home.Task
import java.time.LocalDate

/** True if [task] should appear on [date] given its repeat rule. */
fun occursOn(task: Task, date: LocalDate): Boolean = when (task.repeat) {
    RepeatRule.NONE   -> date == task.dueDate
    RepeatRule.DAILY  -> !date.isBefore(task.dueDate)
    RepeatRule.WEEKLY -> !date.isBefore(task.dueDate) && date.dayOfWeek == task.dueDate.dayOfWeek
    RepeatRule.CUSTOM -> !date.isBefore(task.dueDate) && task.customDays.contains(date.dayOfWeek)
}
