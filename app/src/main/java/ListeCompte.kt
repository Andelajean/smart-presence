package com.example.gpresence

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ListeCompte(
    private var users: List<User>,
    private val context: Context,
    private val onItemClicked: (User) -> Unit
) : RecyclerView.Adapter<ListeCompte.UserViewHolder>() {
    private val firestore = FirebaseFirestore.getInstance()

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.compte_name)
        val emailTextView: TextView = itemView.findViewById(R.id.compte_email)
        val telephoneTextView: TextView = itemView.findViewById(R.id.telephone)
        val roleTextView: TextView = itemView.findViewById(R.id.role)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delte_butto)
        val replyButton: ImageButton = itemView.findViewById(R.id.repl_butto)

        fun bind(user: User) {
            nameTextView.text = user.username
            emailTextView.text = user.email
            telephoneTextView.text = user.telephone
            roleTextView.text = user.role

            deleteButton.setOnClickListener {
                deleteUser(user.id)
            }

            replyButton.setOnClickListener {
                showResponseForm(user.email)
            }

            itemView.setOnClickListener {
                onItemClicked(user)
            }
        }

        private fun deleteUser(userId: String) {
            firestore.collection("users").document(userId).delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show()
                    users = users.filter { it.id != userId } // Update the list
                    notifyDataSetChanged() // Notify adapter
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erreur lors de la suppression : ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        private fun showResponseForm(email: String) {
            val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.role_compte, null)
            val emailTextView: TextView = dialogView.findViewById(R.id.emailEditText)
            val roleSpinner: Spinner = dialogView.findViewById(R.id.roleSpinner)
            val saveButton: Button = dialogView.findViewById(R.id.saveButton)

            val roles = arrayOf("Admin", "User", "Manager")
            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, roles)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            roleSpinner.adapter = adapter
            emailTextView.text = email

            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            saveButton.setOnClickListener {
                val emailInput = emailTextView.text.toString().trim()
                val selectedRole = roleSpinner.selectedItem.toString()

                if (emailInput.isNotEmpty() && selectedRole.isNotEmpty()) {
                    val role = Role(context)
                    role.updateUserRoleByEmail(emailInput, selectedRole)
                } else {
                    Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.compte_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size
}
