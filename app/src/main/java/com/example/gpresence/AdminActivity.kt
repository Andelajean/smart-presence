package com.example.gpresence

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tableau -> {
                    loadFragment(StatFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(SettingsFragment())
                    true
                }
                R.id.nav_equipement -> {
                    loadFragment(Equipement())
                    true
                }
                R.id.nav_code -> {
                    loadFragment(CompteFragment())
                    true
                }
                R.id.nav_voir_requete->{
                    loadFragment(VoirRequete())
                    true
                }
                else -> false
            }
        }

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(StatFragment())
        }
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_containe, fragment)
            .commit()
    }
}
