package com.example.timify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.*

class WeeklySummaryFragment : Fragment() {
    // Declare your variables here
    private lateinit var progressBarMax: ProgressBar
    private lateinit var progressBarMin: ProgressBar
    private lateinit var txtTotalHours: TextView
    // ... other variables

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_weekly_summary, container, false)

        // Initialize your views
        progressBarMax = view.findViewById(R.id.progress_bar_max)
        progressBarMin = view.findViewById(R.id.progress_bar_min)
        txtTotalHours = view.findViewById(R.id.txt_total_hours)
        // ... initialize other views

        // Fetch the user's data from Firebase and calculate the monthly summary
        fetchUserDataAndCalculateMonthlySummary()

        return view
    }

    private fun fetchUserDataAndCalculateMonthlySummary() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        // Assume you have a Firebase reference to your database
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")

        // Fetch the user's monthly information from the database
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val monthlyHoursList = mutableListOf<Int>()
                var maxGoal = 0
                var minGoal = Int.MAX_VALUE

                // Calculate the monthly summary and update the goals
                for (daySnapshot in snapshot.children) {
                    val hours = daySnapshot.child("hours").getValue(Int::class.java) ?: 0
                    monthlyHoursList.add(hours)

                    maxGoal = maxOf(maxGoal, hours)
                    minGoal = minOf(minGoal, hours)
                }

                // Calculate the total hours for the month
                val totalHours = monthlyHoursList.sum()

                // Calculate the progress values for the progress bars
                val maxProgress = (totalHours.toDouble() / maxGoal * 100).toInt()
                val minProgress = (totalHours.toDouble() / minGoal * 100).toInt()

                // Update the progress bars and text views with the calculated values
                progressBarMax.progress = maxProgress
                progressBarMin.progress = minProgress
                txtTotalHours.text = getString(R.string.total_hours_format, totalHours)
                // ... update other views
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any error that occurred while fetching the data
            }
        })
    }
}