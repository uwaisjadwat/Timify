package com.example.timify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView



class HomePage : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {



    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var timesheetEntry : Button
    lateinit var viewCategory : Button
    lateinit var testButton : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)


        timesheetEntry = findViewById(R.id.createEntry)
        viewCategory = findViewById(R.id.viewEntry)
        testButton = findViewById(R.id.TestButton)


        timesheetEntry.setOnClickListener {
            val intent1 = Intent(this, Create_Entry::class.java)
            startActivity(intent1)
        }
        viewCategory.setOnClickListener{
            val intent2 = Intent(this, ViewTimesheet::class.java)
            startActivity(intent2)
        }
        testButton.setOnClickListener{



        }



        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)



        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()



        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val navigationView: NavigationView = findViewById(R.id.TestNav)
        navigationView.setNavigationItemSelectedListener(this)
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