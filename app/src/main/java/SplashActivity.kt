package com.example.gpresence

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserRoleAndRedirect()
        }, 2000) // 2 seconds delay
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
}
