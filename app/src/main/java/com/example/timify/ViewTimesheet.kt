package com.example.timify


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ViewTimesheet : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var userArrayList: ArrayList<Task>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_timesheet)



        userRecyclerview = findViewById(R.id.userRecyclerView)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)



        userArrayList = arrayListOf<Task>()
        getUserData()


    }

    private fun getUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid


        dbref = FirebaseDatabase.getInstance().getReference("users/$userId")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {


                        val user = userSnapshot.getValue(Task::class.java)
                        userArrayList.add(user!!)

                    }

                    userRecyclerview.adapter = TimifyAdapter(userArrayList)
                }


            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}





