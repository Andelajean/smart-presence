package com.example.gpresence

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class RHActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences(SettingsFragment.PREFS_NAME, Context.MODE_PRIVATE)
        val theme = sharedPreferences.getString(SettingsFragment.KEY_THEME, SettingsFragment.THEME_LIGHT)
        AppCompatDelegate.setDefaultNightMode(
            if (theme == SettingsFragment.THEME_DARK) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_rh)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.imagination)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tableau -> {
                    loadFragment(StatFragment())
                    true
                }
                R.id.nav_r -> {
                    loadFragment(VoirRequete())
                    true
                }
                R.id.nav_E -> {
                    loadFragment(StatFragment())
                    true
                }
                R.id.nav_s -> {
                    loadFragment(Stattisque_Rh())
                    true
                }
                R.id.nav_profile->{
                    loadFragment(SettingsFragment())
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
            .replace(R.id.fragment_container2, fragment)
            .commit()
    }
    /*
    override fun onBackPressed() {
        // Afficher un dialogue de confirmation
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Voulez-vous vraiment quitter l'application?")
            .setCancelable(false)
            .setPositiveButton("Oui") { dialog, id ->
                super.onBackPressed() // Appeler la méthode par défaut
            }
            .setNegativeButton("Non") { dialog, id ->
                dialog.dismiss() // Ferme le dialogue et retourne à l'application
            }
        val alert = builder.create()
        alert.show()
    }*/

}
