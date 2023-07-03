package com.example.timify

import java.util.*

data class Task(
    val category: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val minDailyGoal: String,
    val maxDailyGoal: String

)