package com.example.smartplanner

import com.example.smartplanner.ui.home.*
import com.example.smartplanner.feature.occursOn
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class RecurrenceUtilsTest {

    @Test fun daily_occurs_every_day() {
        val start = LocalDate.of(2025, 1, 1)
        val t = Task("Daily", "tag", false, start, recurrence = Recurrence(RepeatRule.DAILY, 1))
        assertTrue(occursOn(t, start.plusDays(3)))
        assertTrue(occursOn(t, start.plusDays(15)))
    }

    @Test fun weekly_only_selected_days() {
        val start = LocalDate.of(2025, 1, 1) // Wed
        val r = Recurrence(RepeatRule.WEEKLY, 1, setOf(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
        val t = Task("Weekly", "tag", false, start, recurrence = r)
        assertTrue(occursOn(t, start.plusDays(2)))   // Fri
        assertFalse(occursOn(t, start.plusDays(1)))  // Thu
    }
}
