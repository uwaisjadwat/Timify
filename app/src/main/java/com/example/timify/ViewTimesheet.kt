package com.example.timify


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class ViewTimesheet : AppCompatActivity() {



    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TimifyAdapter
    private lateinit var dataList: List<Task>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_timesheet)



        recyclerView = findViewById(R.id.recyclerView)
        recyclerView = findViewById(R.id.recyclerView)



// Create sample data for demonstration
        dataList = listOf(
            Task("1", "Category A", "2023-06-01", "09:00", "13:00", "Description 1", null),
            Task("2", "Category B", "2023-06-01", "14:00", "18:00", "Description 2", null),
            Task("3", "Category A", "2023-06-02", "10:00", "12:00", "Description 3", null),
            Task("4", "Category C", "2023-06-02", "13:00", "17:30", "Description 4", null),
            Task("5", "Category B", "2023-06-03", "08:00", "12:30", "Description 5", null),
            Task("6", "Category A", "2023-06-03", "14:00", "18:00", "Description 6", null),
            Task("7", "Category B", "2023-06-04", "09:30", "11:30", "Description 7", null)
        )



// Create and set the adapter
        adapter = TimifyAdapter(dataList)
        recyclerView.adapter = adapter



// Set the layout manager
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}


