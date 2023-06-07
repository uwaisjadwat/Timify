package com.example.timify

data class Task(
    val id: String,
    val category: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val photoUrl: String?

)