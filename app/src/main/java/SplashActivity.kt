package com.example.gpresence

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {
    private lateinit var logoImageView: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        logoImageView = findViewById(R.id.logo)
        progressBar = findViewById(R.id.progressBar)
        val rotation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        logoImageView.startAnimation(rotation)
        val sharedPreferences = getSharedPreferences(SettingsFragment.PREFS_NAME, Context.MODE_PRIVATE)
        val theme = sharedPreferences.getString(SettingsFragment.KEY_THEME, SettingsFragment.THEME_LIGHT)
        AppCompatDelegate.setDefaultNightMode(
            if (theme == SettingsFragment.THEME_DARK) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO)
        if (isInternetAvailable()) {
            Handler(Looper.getMainLooper()).postDelayed({
                checkUserRoleAndRedirect()
            }, 2000) // 2 seconds delay
        } else {
            showNoInternetDialog()
        }
    }

    private fun checkUserRoleAndRedirect() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            // User is signed in, now check their role
            FirebaseFirestore.getInstance().collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    if (role == "Admin") {
                        startActivity(Intent(this, AdminActivity::class.java))
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    finish()
                }
                .addOnFailureListener {
                    // Failed to get user role, fallback to login
                    Toast.makeText(this, "Failed to retrieve user role", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
        } else {
            // No user is signed in, go to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("You have no internet connection. Please try again later.")
            .setPositiveButton("OK") { _, _ ->
                finish() // Close the application
            }
            .setCancelable(false)
            .show()
    }
}
