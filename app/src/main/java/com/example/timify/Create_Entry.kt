package com.example.timify

import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*


class Create_Entry : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {




    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var datePicker: DatePicker
    lateinit var editTextDescription: EditText
    lateinit var buttonAddPhoto: Button
    lateinit var editTextMinDailyGoal: EditText
    lateinit var editTextMaxDailyGoal: EditText
    lateinit var imageView:ImageView

    lateinit var timePickerStartTime: EditText
    lateinit var timePickerEndTime: EditText






    //val storage = FirebaseStorage.getInstance()
    //val storageRef = storage.reference



    companion object{
        val IMAGE_REQUSET_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_entry)


        val actionBar = supportActionBar
        actionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        actionBar?.setCustomView(R.layout.custom_actionbar)
        actionBar?.title = "Timify"



        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)



        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()



        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val navigationView: NavigationView = findViewById(R.id.TestNav)
        navigationView.setNavigationItemSelectedListener(this)





        datePicker = findViewById<DatePicker>(R.id.datePicker)

        editTextDescription = findViewById<EditText>(R.id.editTextDescription)
        buttonAddPhoto = findViewById<Button>(R.id.buttonAddPhoto)
        editTextMinDailyGoal = findViewById<EditText>(R.id.editTextMinDailyGoal)
        editTextMaxDailyGoal = findViewById<EditText>(R.id.editTextMaxDailyGoal)



        val timePickerStartTime = findViewById<EditText>(R.id.timePickerStartTime)
        timePickerStartTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val calendar = Calendar.getInstance()
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                val currentMinute = calendar.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(this@Create_Entry,
                    { timePicker, hourOfDay, minutes ->
                        val amPm = if (hourOfDay >= 12) {
                            "PM"
                        } else {
                            "AM"
                        }
                        timePickerStartTime.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm)
                    }, currentHour, currentMinute, false
                )
                timePickerDialog!!.show()
            }
        })

        val timePickerEndTime = findViewById<EditText>(R.id.timePickerEndTime)
        timePickerEndTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val calendar = Calendar.getInstance()
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                val currentMinute = calendar.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(this@Create_Entry,
                    { timePicker, hourOfDay, minutes ->
                        val amPm = if (hourOfDay >= 12) {
                            "PM"
                        } else {
                            "AM"
                        }
                        timePickerEndTime.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm)
                    }, currentHour, currentMinute, false
                )
                timePickerDialog!!.show()
            }
        })


        imageView = findViewById<ImageView>(R.id.imageViewPhoto)




        val buttonSave = findViewById<Button>(R.id.buttonSave)



        buttonSave.setOnClickListener {
            val selectedCategory = findViewById<EditText>(R.id.addCategory)
            val selectedDate = formatDate(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val selectedStartTime = timePickerStartTime
            val selectedEndTime = timePickerEndTime
            val enteredDescription = editTextDescription.text.toString()
            val enteredMinDailyGoal = editTextMinDailyGoal.text.toString()
            val enteredMaxDailyGoal = editTextMaxDailyGoal.text.toString()

            sendDataToDatabase()

            Toast.makeText(this, "Entry Created !! ", Toast.LENGTH_LONG).show()
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            // Store the values or perform any desired actions
        }


        // Adds the photo to realtime db
        buttonAddPhoto.setOnClickListener {
            pickImageGallery()
        }
    }




    //new code below

    private fun sendDataToDatabase() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")

        val selectedCategory = findViewById<EditText>(R.id.addCategory)
        val selectedDate = formatDate(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        val selectedStartTime = timePickerStartTime
        val selectedEndTime = timePickerEndTime
        val enteredDescription = editTextDescription.text.toString()
        val enteredMinDailyGoal = editTextMinDailyGoal.text.toString()
        val enteredMaxDailyGoal = editTextMaxDailyGoal.text.toString()

        val entryData = hashMapOf(
            "category" to selectedCategory.text.toString(),
            "date" to selectedDate,
            "startTime" to selectedStartTime,
            "endTime" to selectedEndTime,
            "description" to enteredDescription,
            "minDailyGoal" to enteredMinDailyGoal,
            "maxDailyGoal" to enteredMaxDailyGoal
        )

        databaseReference.push().setValue(entryData)
            .addOnSuccessListener {
                Toast.makeText(this, "Entry Created!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to create entry: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }



    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }



    private fun formatTime(hour: Int, minute: Int): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return timeFormat.format(calendar.time)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Activity1 -> {
                val intent1 = Intent(this, HomePage::class.java)
                startActivity(intent1)
            }
            R.id.Activity2 -> {
                val intent2 = Intent(this, Create_Entry::class.java)
                startActivity(intent2)
            }
            R.id.Activity3 -> {
                // Handle logout action
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Create_Entry.IMAGE_REQUSET_CODE)
    }



    // Handling the result when the user selects an image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Create_Entry.IMAGE_REQUSET_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageUri = data?.data
            imageView.setImageURI(data?.data)

            if (imageUri != null) {
                uploadImageToFirebaseStorage(imageUri)
            }
        }
    }



    // Uploading the image to Firebase Storage
    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val fileRef = storageRef.child("images/my_image.jpg") // Change the path and filename as needed



        val uploadTask = fileRef.putFile(imageUri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // File uploaded successfully
            // You can get the download URL to access the file using `getDownloadUrl()`
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                // Do something with the download URL
                // For example, save it to a database or display it to the user
                // You can also call a function to perform further actions with the download URL
                processDownloadUrl(downloadUrl)
            }
        }.addOnFailureListener { exception ->
            // Handle any errors that occurred during the upload
            Log.e(ContentValues.TAG, "Upload failed: ${exception.message}")
        }
    }



    // Function to process the download URL of the uploaded image
    private fun processDownloadUrl(downloadUrl: String) {
        // Perform any desired actions with the download URL
        // For example, save it to a database or display it to the user
        // You can also update UI elements with the image or perform additional tasks
        Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
    }
}




