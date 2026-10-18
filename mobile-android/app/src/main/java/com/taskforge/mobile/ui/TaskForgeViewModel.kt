package com.taskforge.mobile.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.taskforge.mobile.data.ApiClient
import com.taskforge.mobile.data.AuthResponse
import com.taskforge.mobile.data.LoginRequest
import com.taskforge.mobile.data.RegisterRequest
import com.taskforge.mobile.data.SessionStore
import com.taskforge.mobile.data.TaskItem
import com.taskforge.mobile.data.TaskRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class TaskForgeViewModel(application: Application) : AndroidViewModel(application) {

    private val session = SessionStore(application)
    private val api = ApiClient.create { session.token }

    var loggedIn by mutableStateOf(session.token != null)
        private set
    var username by mutableStateOf(session.username ?: "")
        private set

    var tasks by mutableStateOf<List<TaskItem>>(emptyList())
        private set
    var statusFilter by mutableStateOf<String?>(null)
        private set

    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        if (loggedIn) loadTasks()
    }

    fun clearError() {
        error = null
    }

    // ---- Auth -------------------------------------------------------------------------

    fun login(username: String, password: String) = authenticate("Login failed.") {
        api.login(LoginRequest(username.trim(), password))
    }

    fun register(username: String, email: String, password: String) =
        authenticate("Registration failed.") {
            api.register(RegisterRequest(username.trim(), email.trim(), password))
        }

    private fun authenticate(fallback: String, call: suspend () -> AuthResponse) {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                val auth = call()
                session.save(auth)
                username = auth.username
                loggedIn = true
                loadTasks()
            } catch (e: Exception) {
                error = describe(e, fallback)
            } finally {
                loading = false
            }
        }
    }

    fun logout() {
        session.clear()
        loggedIn = false
        username = ""
        tasks = emptyList()
        statusFilter = null
        error = null
    }

    // ---- Tasks ------------------------------------------------------------------------

    fun setFilter(status: String?) {
        statusFilter = status
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            loading = true
            try {
                tasks = api.tasks(statusFilter).content
                error = null
            } catch (e: Exception) {
                handleTaskError(e, "Failed to load tasks.")
            } finally {
                loading = false
            }
        }
    }

    fun saveTask(existingId: Long?, request: TaskRequest, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                if (existingId == null) api.createTask(request) else api.updateTask(existingId, request)
                onDone()
                loadTasks()
            } catch (e: Exception) {
                handleTaskError(e, "Could not save the task.")
            }
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            try {
                api.deleteTask(id)
                loadTasks()
            } catch (e: Exception) {
                handleTaskError(e, "Could not delete the task.")
            }
        }
    }

    // ---- Errors -----------------------------------------------------------------------

    private fun handleTaskError(e: Exception, fallback: String) {
        // An expired/invalid token means the session is over: send the user back to login.
        if (e is HttpException && (e.code() == 401 || e.code() == 403)) {
            logout()
            error = "Your session expired. Please log in again."
        } else {
            error = describe(e, fallback)
        }
    }

    private fun describe(e: Exception, fallback: String): String = when (e) {
        is IOException -> "Cannot reach the server. Is the backend running?"
        is HttpException -> parseServerMessage(e) ?: fallback
        else -> fallback
    }

    private fun parseServerMessage(e: HttpException): String? = try {
        val body = e.response()?.errorBody()?.string()
        val json = Gson().fromJson(body, JsonObject::class.java)
        when {
            json.has("message") -> json.get("message").asString
            json.has("errors") -> json.getAsJsonObject("errors").entrySet().firstOrNull()?.value?.asString
            else -> null
        }
    } catch (ignored: Exception) {
        null
    }
}
