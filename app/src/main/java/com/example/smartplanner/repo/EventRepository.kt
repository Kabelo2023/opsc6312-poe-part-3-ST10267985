package com.example.smartplanner.repo

import com.example.smartplanner.data.model.Event
import com.example.smartplanner.data.remote.ApiService
import java.time.Instant
import java.time.temporal.ChronoUnit

class EventRepository(private val api: ApiService) {

    suspend fun getMyEvents(uid: String): List<Event> =
        api.getEvents(createdBy = uid)

    suspend fun createSample(uid: String): Event {
        val start = Instant.now()
        val end = start.plus(1, ChronoUnit.HOURS)

        val body = Event(
            title = "Sample meeting",
            startIso = start.toString(),
            endIso = end.toString(),
            location = "Office",
            createdBy = uid
        )
        return api.create(body)
    }
}
