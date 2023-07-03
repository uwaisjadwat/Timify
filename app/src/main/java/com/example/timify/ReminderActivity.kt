package com.example.timify
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class ReminderActivity : AppCompatActivity() {

    // Get a reference to your Firebase database
    private val database = FirebaseDatabase.getInstance()
    private val remindersRef = database.getReference("reminders")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        val editMessage = findViewById<EditText>(R.id.editMessage)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val btnCreateReminder = findViewById<Button>(R.id.btnCreateReminder)

        btnCreateReminder.setOnClickListener {
            val message = editMessage.text.toString()
            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val createdDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val sendDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            val userId = "user1" // Replace with actual user ID

            // Create a new reminder entry in Firebase
            val newReminderRef = remindersRef.push()
            newReminderRef.child("createdDate").setValue(createdDate)
            newReminderRef.child("sendDate").setValue(sendDate)
            newReminderRef.child("message").setValue(message)
            newReminderRef.child("userId").setValue(userId)

            // Clear input fields
            editMessage.text.clear()
            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        }
    }
}