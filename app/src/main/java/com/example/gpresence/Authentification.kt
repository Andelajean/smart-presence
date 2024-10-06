package com.example.gpresence

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class Authentification(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val role: String = "user"

    data class RegisterClass(
        val username: String,
        val email: String,
        val password: String,
        val confirmPassword: String,
        val role: String

    )

    fun registerUser(email: String, password: String, username: String, confirmPassword: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
// Pour chaque Toast, fais pareil

            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, context.getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
// Pour chaque Toast, fais pareil

            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userData = RegisterClass(username, email, password, confirmPassword, role)
                        firestore.collection("users").document(user.uid).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(context, context.getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, context.getString(R.string.database_error, e.message), Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        when (exception.errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                Toast.makeText(context, context.getString(R.string.invalid_email_format), Toast.LENGTH_SHORT).show()
                            }
                            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                Toast.makeText(context, context.getString(R.string.email_already_in_use), Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(context, context.getString(R.string.authentication_failed, exception.message), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.authentication_failed, exception?.message), Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, context.getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                    // Add the code to navigate to the next activity or main screen

                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        when (exception.errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                Toast.makeText(context, context.getString(R.string. invalid_email_format), Toast.LENGTH_SHORT).show()
                            }
                            "ERROR_WRONG_PASSWORD" -> {
                                Toast.makeText(context, context.getString(R.string.wrong_password), Toast.LENGTH_SHORT).show()
                            }
                            "ERROR_USER_NOT_FOUND" -> {
                                Toast.makeText(context, context.getString(R.string.user_not_found), Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(context, context.getString(R.string.login_failed, exception.message), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Login failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
