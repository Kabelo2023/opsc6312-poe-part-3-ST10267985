package com.example.smartplanner.data.model

import com.squareup.moshi.Json

/**
 * Matches MockAPI resource fields exactly.
 */
data class Event(
    @Json(name = "id") val id: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "startIso") val startIso: String,
    @Json(name = "endIso") val endIso: String,
    @Json(name = "location") val location: String,
    @Json(name = "createdBy") val createdBy: String
)
