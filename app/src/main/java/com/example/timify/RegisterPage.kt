package com.example.timify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)


        //initialising the firebase
        auth = Firebase.auth

        //intialises the edit text from the view
        val username = findViewById<EditText>(R.id.reg_username)
        val password = findViewById<EditText>(R.id.reg_password)
        val btnReg = findViewById<Button>(R.id.btnRegister)



        //when the register button is clicked
        btnReg.setOnClickListener {
            //converts the text that was entered from the username and password into string
            val enteredUsername = username.text.toString()
            val enteredPassword = password.text.toString()


            // if either the username or password is not entered this message shows
            if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Username and Password cannot be empty", Toast.LENGTH_SHORT).show()
            } else {

                //calls the registerUserAndCreateDatabaseEntry() method and passes the entered values into that method
                registerUserAndCreateDatabaseEntry(enteredUsername, enteredPassword)


            }
        }
    }




    private fun registerUserAndCreateDatabaseEntry(username: String, password: String) {

        //code that adds the new user to the database and passes the username and password that was entered
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->

                //if the user is successfully added to the database
                if (task.isSuccessful) {
                    Toast.makeText(this@RegisterPage, "Logged In", Toast.LENGTH_LONG).show()

                    //calls the method to take to the next page
                    switchActivities()
                }
            }
            //displays the error if the user is not on the database
            .addOnFailureListener(this) { exception ->
                Toast.makeText(this@RegisterPage, exception.message, Toast.LENGTH_LONG).show()
            }
    }



    // Function to switch activities
    private fun switchActivities() {
        val switchActivityIntent = Intent(this, HomePage::class.java)
        startActivity(switchActivityIntent)
        finish()
    }
}