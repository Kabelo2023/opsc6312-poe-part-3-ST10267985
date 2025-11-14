package com.example.smartplanner

import com.example.smartplanner.feature.occursOn
import com.example.smartplanner.ui.home.RepeatRule
import com.example.smartplanner.ui.home.Task
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class RecurrenceUtilsTest {

    @Test
    fun daily_occurs_every_day_from_start() {
        val start = LocalDate.of(2025, 1, 1) // Wed
        val t = Task(
            title = "Daily",
            tag = "tag",
            done = false,
            dueDate = start,
            repeat = RepeatRule.DAILY
        )

        // On the start date and after = true
        assertTrue(occursOn(t, start))
        assertTrue(occursOn(t, start.plusDays(3)))
        assertTrue(occursOn(t, start.plusDays(15)))

        // Before the start date = false
        assertFalse(occursOn(t, start.minusDays(1)))
    }

    @Test
    fun weekly_matches_same_weekday_only() {
        val start = LocalDate.of(2025, 1, 1) // Wednesday
        val t = Task(
            title = "Weekly",
            tag = "tag",
            done = false,
            dueDate = start,
            repeat = RepeatRule.WEEKLY
        )

        assertTrue(occursOn(t, start))                    // Wed
        assertTrue(occursOn(t, start.plusWeeks(1)))       // Next Wed
        assertFalse(occursOn(t, start.plusDays(1)))       // Thu
        assertFalse(occursOn(t, start.plusDays(2)))       // Fri
    }

    @Test
    fun custom_only_selected_days_after_start() {
        val start = LocalDate.of(2025, 1, 1) // Wednesday
        val t = Task(
            title = "Custom",
            tag = "tag",
            done = false,
            dueDate = start,
            repeat = RepeatRule.CUSTOM,
            customDays = setOf(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        )

        assertTrue(occursOn(t, start))                    // Wed
        assertTrue(occursOn(t, start.plusDays(2)))        // Fri
        assertFalse(occursOn(t, start.plusDays(1)))       // Thu
        assertFalse(occursOn(t, start.minusDays(1)))      // Before start
    }
}
