package com.example.smartplanner.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartplanner.data.model.Event
import com.example.smartplanner.data.remote.ApiClient
import com.example.smartplanner.repo.EventRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SchedulerViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val repo = EventRepository(ApiClient.create())

    private val _events = MutableLiveData<List<Event>>(emptyList())
    val events: LiveData<List<Event>> = _events

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun loadEvents() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _message.value = "Not logged in"
            return
        }
        viewModelScope.launch {
            try {
                val list = repo.getMyEvents(uid)
                _events.value = list
                _message.value = "Loaded ${list.size} event(s)"
                Log.i("SmartPlanner", "Events: $list")
            } catch (e: Exception) {
                Log.e("SmartPlanner", "loadEvents failed", e)
                _message.value = "Load failed: ${e.localizedMessage}"
            }
        }
    }

    fun createSample() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _message.value = "Not logged in"
            return
        }
        viewModelScope.launch {
            try {
                val created = repo.createSample(uid)
                _message.value = "Created: ${created.title}"
                loadEvents()
            } catch (e: Exception) {
                Log.e("SmartPlanner", "createSample failed", e)
                _message.value = "Create failed: ${e.localizedMessage}"
            }
        }
    }
}
