package com.example.smartplanner.ui.home

import java.time.LocalDate

data class WeekDay(
    val date: LocalDate,
    var selected: Boolean = false
)
