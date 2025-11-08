package com.example.smartplanner.data.model

import com.squareup.moshi.Json

data class TaskRemote(
    @Json(name = "id") val id: String? = null,
    @Json(name = "title") val title: String,
    @Json(name = "tag") val tag: String,
    @Json(name = "done") val done: Boolean,
    @Json(name = "createdBy") val createdBy: String
)
