package com.example.gpresence

//import AdminActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.nav_parametre -> {
                    loadFragment(SettingsFragment())
                    true
                }
                R.id.nav_profil -> {
                    loadFragment(ProfileFragment())
                    true
                }
                R.id.nav_requete -> {
                    loadFragment(RequestsFragment())
                    true
                }
               /* R.id.nav_admin -> {

                }*/
                R.id.nav_admin -> {
                   looadmain()
                    true
                }
                else -> false
            }
        }

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
   private fun looadmain(){
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
    }
}
