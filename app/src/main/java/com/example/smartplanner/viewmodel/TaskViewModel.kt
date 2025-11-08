package com.example.smartplanner.viewmodel

import androidx.lifecycle.*
import com.example.smartplanner.data.model.TaskRemote
import com.example.smartplanner.data.remote.ApiClient
import com.example.smartplanner.repo.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = TaskRepository(ApiClient.create())

    private val _tasks = MutableLiveData<List<TaskRemote>>(emptyList())
    val tasks: LiveData<List<TaskRemote>> = _tasks

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun load() {
        val uid = auth.currentUser?.uid ?: return run { _message.value = "Not logged in" }
        viewModelScope.launch {
            try {
                _tasks.value = repo.getMyTasks(uid)
                _message.value = "Loaded ${_tasks.value!!.size} task(s)"
            } catch (t: Throwable) {
                _message.value = "Task load failed (using local only)"
            }
        }
    }

    fun add(title: String, tag: String, onLocalFallback: (TaskRemote) -> Unit) {
        val uid = auth.currentUser?.uid ?: return run { _message.value = "Not logged in" }
        viewModelScope.launch {
            try {
                val created = repo.create(uid, title, tag)
                _tasks.value = (_tasks.value ?: emptyList()) + created
                _message.value = "Task created"
            } catch (_: Throwable) {
                // fallback: local item (no id)
                val local = TaskRemote(null, title, tag, false, uid)
                onLocalFallback(local)
                _message.value = "Task saved locally (API unavailable)"
            }
        }
    }

    fun remove(id: String?) {
        if (id == null) return
        viewModelScope.launch {
            try { repo.delete(id) } catch (_: Throwable) { /* ignore */ }
            _tasks.value = _tasks.value?.filterNot { it.id == id }
        }
    }
}
