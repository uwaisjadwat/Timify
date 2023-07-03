package com.example.timify

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class Graphs : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{

    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle


    private lateinit var chart: BarChart

    private lateinit var startDateButton: Button

    private lateinit var endDateButton: Button

    private var selectedStartDate: Date? = null

    private var selectedEndDate: Date? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_graphs)


        handleUncaughtException()


        chart = findViewById(R.id.chart)

        startDateButton = findViewById(R.id.startDateButton)

        endDateButton = findViewById(R.id.endDateButton)


        startDateButton.setOnClickListener {

            showDatePickerDialog(true)

        }


        endDateButton.setOnClickListener {

            showDatePickerDialog(false)

        }


        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {

            generateFilteredEntries(userId)

        }


        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)



        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()



        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val navigationView: NavigationView = findViewById(R.id.TestNav)
        navigationView.setNavigationItemSelectedListener(this)



    }


    private fun setupChart(filteredEntries: List<BarEntry>) {

        if (filteredEntries.isEmpty()) {

// Handle the case where entries list is empty

// For example, display a message or show an empty chart

            return

        }


        val dataSet = BarDataSet(filteredEntries, "Hours Worked")

        dataSet.color = Color.BLUE


        val xAxis = chart.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.granularity = 1f

        xAxis.valueFormatter = object : IndexAxisValueFormatter() {

            override fun getFormattedValue(value: Float, axis: AxisBase?): String {

                val index = value.toInt()

                return (filteredEntries[index].data as TimeSheetEntry).date

            }

        }


        val barData = BarData(dataSet)

        chart.data = barData


// Customize the appearance of the bars

        dataSet.valueFormatter = object : ValueFormatter() {

            override fun getBarLabel(barEntry: BarEntry?): String {

                val data = barEntry?.data as? TimeSheetEntry

                val hoursWorked = data?.hoursWorked ?: 0f

                val minGoal = data?.minGoal ?: 0f

                val maxGoal = data?.maxGoal ?: 0f

                return "Hours: $hoursWorked, Min Goal: $minGoal, Max Goal: $maxGoal"

            }

        }


// Calculate the overall minimum and maximum goal values

        var overallMinGoal = Float.MAX_VALUE

        var overallMaxGoal = Float.MIN_VALUE

        for (entry in filteredEntries) {

            val timeSheetEntry = entry.data as TimeSheetEntry

            if (timeSheetEntry.minGoal < overallMinGoal) {

                overallMinGoal = timeSheetEntry.minGoal

            }

            if (timeSheetEntry.maxGoal > overallMaxGoal) {

                overallMaxGoal = timeSheetEntry.maxGoal

            }

        }


// Set custom axis minimum and maximum values

        val leftAxis = chart.axisLeft

        leftAxis.axisMinimum = overallMinGoal - 1f // Adjust the offset as needed

        leftAxis.axisMaximum = overallMaxGoal + 1f // Adjust the offset as needed


// Remove existing limit lines

        leftAxis.removeAllLimitLines()


// Add limit lines for each day in the selected period

        for (entry in filteredEntries) {

            val timeSheetEntry = entry.data as TimeSheetEntry

            val limitLine = LimitLine(timeSheetEntry.minGoal, "Min Goal: ${timeSheetEntry.minGoal}")

            limitLine.lineColor = Color.RED

            limitLine.lineWidth = 1f

            leftAxis.addLimitLine(limitLine)


            val maxLimitLine = LimitLine(timeSheetEntry.maxGoal, "Max Goal: ${timeSheetEntry.maxGoal}")

            maxLimitLine.lineColor = Color.GREEN

            maxLimitLine.lineWidth = 1f

            leftAxis.addLimitLine(maxLimitLine)

        }


        chart.invalidate()

    }


    private fun generateFilteredEntries(userId: String) {

        val startDate = selectedStartDate // Get the start date from the selected range

        val endDate = selectedEndDate // Get the end date from the selected range


        val database = FirebaseDatabase.getInstance()

        val timeSheetRef = database.getReference("timesheets").child(userId)


        val filteredEntries = mutableListOf<BarEntry>()

        timeSheetRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (childSnapshot in dataSnapshot.children) {

                    val entry = childSnapshot.getValue(TimeSheetEntry::class.java)

                    if (entry != null) {

                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(entry.date)

                        if (currentDate != null && isDateInRange(currentDate, startDate, endDate)) {

                            val barEntry = BarEntry(filteredEntries.size.toFloat(), entry.hoursWorked, entry) // Pass the TimeSheetEntry as data

                            filteredEntries.add(barEntry)

                        }

                    }

                }


                setupChart(filteredEntries)

            }


            override fun onCancelled(databaseError: DatabaseError) {

// Handle the error if the data retrieval is canceled

            }

        })

    }


    private fun isDateInRange(date: Date, startDate: Date?, endDate: Date?): Boolean {

        if (startDate != null && date.before(startDate)) {

            return false

        }

        if (endDate != null && date.after(endDate)) {

            return false

        }

        return true

    }


    private fun showDatePickerDialog(isStartDate: Boolean) {

        val calendar = Calendar.getInstance()

        val initialDate = if (isStartDate) selectedStartDate else selectedEndDate

        if (initialDate != null) {

            calendar.time = initialDate

        }


        val datePickerDialog = DatePickerDialog(

            this,

            { _, year, month, dayOfMonth ->

                val selectedDate = createDate(year, month, dayOfMonth)

                if (isStartDate) {

                    selectedStartDate = selectedDate

                    startDateButton.text = formatDate(selectedDate)

                } else {

                    selectedEndDate = selectedDate

                    endDateButton.text = formatDate(selectedDate)

                }

                val userId = FirebaseAuth.getInstance().currentUser?.uid

                if (userId != null) {

                    generateFilteredEntries(userId)

                }

            },

            calendar.get(Calendar.YEAR),

            calendar.get(Calendar.MONTH),

            calendar.get(Calendar.DAY_OF_MONTH)

        )


        datePickerDialog.show()

    }


    private fun createDate(year: Int, month: Int, dayOfMonth: Int): Date {

        val calendar = Calendar.getInstance()

        calendar.set(year, month, dayOfMonth)

        return calendar.time

    }


    private fun formatDate(date: Date): String {

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return format.format(date)

    }


    data class TimeSheetEntry(

        val date: String,

        val hoursWorked: Float,

        val minGoal: Float,

        val maxGoal: Float

    )


    private fun handleUncaughtException() {

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->

            Log.e("MainActivity", "Uncaught Exception", throwable)

            finish() // Close the activity to prevent the app from crashing completely

        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Activity1 -> {
                val accountIntent = Intent(this, HomePage::class.java)
                startActivity(accountIntent)
            }
            R.id.Activity2 -> {
                val settingsIntent = Intent(this, Create_Entry::class.java)
                startActivity(settingsIntent)
            }
            R.id.Activity3 -> {
                val settingsIntent = Intent(this, ViewTimesheet::class.java)
                startActivity(settingsIntent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



}