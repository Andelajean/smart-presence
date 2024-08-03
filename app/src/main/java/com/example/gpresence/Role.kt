package com.example.gpresence

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class Role(private val context: Context) {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Méthode pour obtenir l'UID de l'utilisateur basé sur l'email
    private fun getUserIdByEmail(email: String, callback: (String?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(null) // Aucune correspondance trouvée
                } else {
                    val document = documents.first()
                    val userId = document.id // L'ID du document est l'UID
                    callback(userId)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Role", "Error fetching user ID: ${e.message}", e)
                callback(null)
            }
    }

    // Méthode pour mettre à jour le rôle de l'utilisateur basé sur l'email
    fun updateUserRoleByEmail(email: String, newRole: String) {
        if (email.isEmpty()) {
            Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            return
        } else if ("Choisir".equals(newRole, ignoreCase = true)) {
            Toast.makeText(context, "Please choose a valid role", Toast.LENGTH_SHORT).show()
            return
        }

        getUserIdByEmail(email) { userId ->
            if (userId != null) {
                val userRef = firestore.collection("users").document(userId)
                userRef.update("role", newRole)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Role updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error updating role: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("Role", "Error updating role: ${e.message}", e)
                    }
            } else {
                Toast.makeText(context, "User with email $email not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
