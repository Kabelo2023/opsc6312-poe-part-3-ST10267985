package com.example.smartplanner.util
import com.example.smartplanner.ui.home.Task

/** Simple in-memory handoff between screens (keeps Home logic untouched). */
object TaskBus { var tasks: List<Task> = emptyList() }
