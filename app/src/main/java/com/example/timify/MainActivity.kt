package com.example.timify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val remindersRef = database.getReference("reminders")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Replace "user1" with the actual user ID of the logged-in user
        val userId = "user1"

        // Retrieve the reminders for the user
        val userRemindersQuery = remindersRef.orderByChild("userId").equalTo(userId)
        userRemindersQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val today = getCurrentDate() // Get the current date

                // Loop through the reminders
                for (reminderSnapshot in dataSnapshot.children) {
                    val sendDate = reminderSnapshot.child("sendDate").getValue(String::class.java)

                    // Check if the reminder's send date matches today's date
                    if (sendDate == today) {
                        val message = reminderSnapshot.child("message").getValue(String::class.java)
                        // Display the reminder to the user, e.g., show a notification
                        showReminderNotification(message)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors during data retrieval
            }
        })


        loginButton() //calls the login method
        setupActivityLink() //calls the method that converts the register button into a hyperlink text
    }

    // <!--takes to the home page-->
    private fun loginButton() {
        val username = findViewById<EditText>(R.id.edt_username)
        val password = findViewById<EditText>(R.id.edt_password)
        val loginBtn = findViewById<Button>(R.id.btnLogin)


        //when the login button is clicked these lines of code run
        loginBtn.setOnClickListener {


            //if the username and the password is not entered it shows this error message
            if (username.text.isEmpty() && password.text.isEmpty()) {
                Toast.makeText(this, "Username and Password cannot be empty", Toast.LENGTH_SHORT)
                    .show()


                // if either the username or password is not entered this message shows
            } else if (username.text.isEmpty() || password.text.isEmpty()) {
                Toast.makeText(this, "Please fill in login details", Toast.LENGTH_SHORT).show()


            } else {
                //else it would run this can look for the user if they are in the database
                val auth: FirebaseAuth = Firebase.auth
                val enteredUsername = username.text.toString()
                val enteredPassword = password.text.toString()


                //looks for the user in the database
                auth.signInWithEmailAndPassword(enteredUsername, enteredPassword)
                    .addOnCompleteListener(this) { task ->

                        //if the user is successful
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                            username.setText("") //clears the username edit text
                            password.setText("") //clears the password edit text


                            //this pushes the user id to the next activity
                            val user: FirebaseUser? = auth.currentUser
                            val uuid: String? = user?.uid
                            if (uuid != null) {
                                val switchActivityIntent = Intent(this, HomePage::class.java)
                                switchActivityIntent.putExtra("UUID_EXTRA", uuid)
                                startActivity(switchActivityIntent)
                                finish() // prevents the user from coming back to the login page
                            }


                        } else {
                            //if the user is not found
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                        }
                    }
                    //displays the error if the user is not on the database
                    .addOnFailureListener(this) { exception ->
                        Toast.makeText(this@MainActivity, exception.message, Toast.LENGTH_LONG)
                            .show()
                    }
            }
        }
    }


    //method for the register text hyper link
    fun setupActivityLink() {
        val linkTextView = findViewById<TextView>(R.id.txt_signUp)
        linkTextView.setTextColor(Color.BLUE) // sets the text to blue

        //when the hyperlink text is clicked
        linkTextView.setOnClickListener {
            val switchActivityIntent =
                Intent(this, RegisterPage::class.java) // navigates to to the register page
            startActivity(switchActivityIntent)
        }
    }


    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun showReminderNotification(message: String?) {
        // Create a notification channel if targeting Android Oreo (API level 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "reminder_channel"
            val channelName = "Reminder Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create an intent to open the app when the notification is clicked
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // Build the notification
        val builder = NotificationCompat.Builder(this, "reminder_channel")
            .setSmallIcon(R.drawable.notification_icon) //change to where the icon is located in the drawable files
            .setContentTitle("Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Show the notification

        val notificationManager =
            NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager. notify (0, builder.build())
    }

}






