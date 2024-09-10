package com.example.gpresence

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import android.provider.Settings.Secure
import android.view.View
import android.widget.ProgressBar


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameField: EditText
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText

    //private lateinit var roleField: EditText
    //private lateinit var codeField: EditText
    private lateinit var registerButton: Button
    private lateinit var alreadyHaveAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences =
            getSharedPreferences(SettingsFragment.PREFS_NAME, Context.MODE_PRIVATE)
        val theme =
            sharedPreferences.getString(SettingsFragment.KEY_THEME, SettingsFragment.THEME_LIGHT)
        AppCompatDelegate.setDefaultNightMode(
            if (theme == SettingsFragment.THEME_DARK) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        // Initialize UI components
        usernameField = findViewById(R.id.username)
        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        confirmPasswordField = findViewById(R.id.confirm_password)
        //roleField = findViewById(R.id.role)
        //codeField = findViewById(R.id.code)
        registerButton = findViewById(R.id.register_button)
        alreadyHaveAccount = findViewById(R.id.already_have_account)

        // Set up the click listener for the register button
        registerButton.setOnClickListener {
            registerUse()
        }

        // Set up the click listener for the "Already have an account" link
        alreadyHaveAccount.setOnClickListener {
            // Navigate to login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUse() {
        val username = usernameField.text.toString().trim()
        val email = emailField.text.toString().trim()
        val password = passwordField.text.toString().trim()
        val confirmPassword = confirmPasswordField.text.toString().trim()
        val role = "user"

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "les mots de passe sont differents", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 8) {
            Toast.makeText(this, "le mot de passe doit contenir au moins 8 caracteres", Toast.LENGTH_SHORT).show()
            return
        }
        // Afficher le ProgressBar
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        // Simuler une tâche en arrière-plan
        Handler(Looper.getMainLooper()).postDelayed({
            // Masquer le ProgressBar après l'enregistrement
            progressBar.visibility = View.GONE

            // Logique d'enregistrement terminé
            //Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()

        }, 3000) // Simule une tâche qui prend 3 secondes


        // Récupérer l'Android ID
        val code = Secure.getString(contentResolver, Secure.ANDROID_ID)

        // Enregistrer l'utilisateur avec l'Android ID
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userData = RegisterClass(
                            username,
                            email,
                            password,
                            confirmPassword,
                            role,
                            code
                        )
                        firestore.collection("users").document(user.uid).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Compte créer avec succes", Toast.LENGTH_SHORT)
                                    .show()
                                clearForm()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Database error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        when (exception.errorCode) {
                            "ERROR_INVALID_EMAIL" -> {
                                Toast.makeText(
                                    this,
                                    "The email address is badly formatted.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                Toast.makeText(
                                    this,
                                    "Cette adresse email existe deja.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                Toast.makeText(
                                    this,
                                    "Authentication failed: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Authentication failed: ${exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun clearForm() {
        emailField.text.clear()
        usernameField.text.clear()
        passwordField.text.clear()
        confirmPasswordField.text.clear()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Voulez-vous vraiment quitter l'application?")
            .setCancelable(false)
            .setPositiveButton("Oui") { dialog, id ->
                super.onBackPressed()
            }
            .setNegativeButton("Non") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}