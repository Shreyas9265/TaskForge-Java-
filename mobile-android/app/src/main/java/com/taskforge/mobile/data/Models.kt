package com.taskforge.mobile.data

data class RegisterRequest(val username: String, val email: String, val password: String)

data class LoginRequest(val username: String, val password: String)

data class AuthResponse(
    val token: String,
    val username: String,
    val email: String,
    val role: String
)

data class TaskItem(
    val id: Long,
    val title: String,
    val description: String?,
    val status: String,
    val priority: String,
    val dueDate: String?
)

data class TaskRequest(
    val title: String,
    val description: String?,
    val status: String,
    val priority: String,
    val dueDate: String?
)

/** Mirrors the subset of Spring Data's Page JSON that the app uses. */
data class PageResponse<T>(val content: List<T>, val totalPages: Int)

object TaskStatus {
    val all = listOf("TODO", "IN_PROGRESS", "DONE")
}

object TaskPriority {
    val all = listOf("LOW", "MEDIUM", "HIGH")
}

fun String.pretty(): String = replace('_', ' ')
