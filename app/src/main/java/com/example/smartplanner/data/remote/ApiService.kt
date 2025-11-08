package com.example.smartplanner.data.remote

import com.example.smartplanner.data.model.Event
import com.example.smartplanner.data.model.TaskRemote
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ApiService {

    // GET https://<subdomain>.mockapi.io/events?createdBy=<uid>
    @GET("events")
    suspend fun getEvents(@Query("createdBy") createdBy: String): List<Event>

    // POST https://<subdomain>.mockapi.io/events
    @POST("events")
    suspend fun create(@Body body: Event): Event

    @GET("tasks")
    suspend fun getTasks(@Query("createdBy") createdBy: String): List<TaskRemote>

    @POST("tasks")
    suspend fun createTask(@Body body: TaskRemote): TaskRemote

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String)
}
