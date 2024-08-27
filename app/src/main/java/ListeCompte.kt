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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListeCompte(
    private var users: List<User>,
    private val context: Context,
    private val onItemClicked: (User) -> Unit
) : RecyclerView.Adapter<ListeCompte.UserViewHolder>() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
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
            val password = user.pwd
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
            // Afficher une boîte de dialogue de confirmation
            AlertDialog.Builder(context)
                .setTitle("Confirmation")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce compte ?")
                .setPositiveButton("Oui") { dialog, _ ->
                    // Supprimer l'utilisateur de Firebase Authentication
                    val currentUser = auth.currentUser
                    val credential = EmailAuthProvider.getCredential("ajeangael@gmail.co" , "andela") // Assurez-vous de fournir le bon mot de passe ici

                    currentUser?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            // Supprimer l'utilisateur de Firebase Authentication
                            currentUser.delete()
                                .addOnSuccessListener {
                                    // Supprimer l'utilisateur de Firestore
                                    firestore.collection("users").document(userId).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show()
                                            users = users.filter { it.id != userId } // Mettre à jour la liste
                                            notifyDataSetChanged() // Notifier l'adaptateur
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Erreur lors de la suppression dans Firestore : ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Erreur lors de la suppression dans Authentification : ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(context, "Échec de la réauthentification : ${reauthTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Non") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        private fun showResponseForm(email: String) {
            val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.role_compte, null)

            // Trouver les vues correspondantes
            val emailEditText: TextInputEditText = dialogView.findViewById(R.id.email)
            val roleSpinner: Spinner = dialogView.findViewById(R.id.roleSpinner)
            val saveButton: Button = dialogView.findViewById(R.id.saveButton)

            // Définir les rôles dans le Spinner
            val roles = arrayOf("Choisir","Admin", "User", "Manager")
            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, roles)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            roleSpinner.adapter = adapter

            // Pré-remplir l'email
            emailEditText.setText(email)

            // Créer et configurer l'AlertDialog
            val alertDialog = AlertDialog.Builder(itemView.context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // Gérer le clic sur le bouton Sauvegarder
            saveButton.setOnClickListener {
                val emailInput = emailEditText.text.toString().trim()
                val selectedRole = roleSpinner.selectedItem.toString()

                if (emailInput.isNotEmpty() && selectedRole.isNotEmpty()) {
                    // Logique de mise à jour du rôle de l'utilisateur
                    val role = Role(itemView.context)
                    role.updateUserRoleByEmail(emailInput, selectedRole)
                } else {
                    Toast.makeText(itemView.context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
                alertDialog.dismiss()
            }

            // Afficher le formulaire de réponse
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
