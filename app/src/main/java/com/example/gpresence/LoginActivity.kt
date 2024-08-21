package com.example.gpresence

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView

    // SharedPreferences pour stocker l'état de connexion et le rôle de l'utilisateur
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Initialize UI components
        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        registerLink = findViewById(R.id.register_link)

        // Set up the click listener for the login button
        loginButton.setOnClickListener {
            loginUser()
        }

        // Set up the click listener for the "Register here" link
        registerLink.setOnClickListener {
            // Navigate to registration activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = emailField.text.toString().trim()
        val password = passwordField.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Récupérer l'UID de l'utilisateur connecté
                    val user = auth.currentUser
                    user?.let {
                        val uid = it.uid
                        // Récupérer le rôle de l'utilisateur à partir de Firestore
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val userRole = document.getString("role")
                                    if (userRole != null) {
                                        // Enregistrer l'état de connexion et le rôle de l'utilisateur dans SharedPreferences
                                        val editor = sharedPreferences.edit()
                                        editor.putString("role", userRole)
                                        editor.putBoolean("isLoggedIn", true)
                                        editor.apply()

                                        // Rediriger l'utilisateur vers l'activité appropriée en fonction de son rôle
                                        if (userRole == "Admin") {
                                            val intent = Intent(this, AdminActivity::class.java)
                                            startActivity(intent)
                                        } else {
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                        }

                                        // Fermer la LoginActivity pour empêcher l'utilisateur de revenir à la page de connexion
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Role not found", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Error getting role: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        when (exception.errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                Toast.makeText(this, "The email address is badly formatted.", Toast.LENGTH_SHORT).show()
                            }
                            "ERROR_WRONG_PASSWORD" -> {
                                Toast.makeText(this, "The password is incorrect.", Toast.LENGTH_SHORT).show()
                            }
                            "ERROR_USER_NOT_FOUND" -> {
                                Toast.makeText(this, "No user found with this email.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Login failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun clearForm(){
        emailField.text.clear()
        passwordField.text.clear()
    }
}
