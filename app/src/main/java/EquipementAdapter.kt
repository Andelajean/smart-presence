package com.example.gpresence

import android.app.AlertDialog
import android.content.Context
import android.util.Log
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

class EquipementAdapter(
    private var equi: List<Equipe>,
    private val context: Context,
    private val onItemClicked: (Equipe) -> Unit
) : RecyclerView.Adapter<EquipementAdapter.UserViewHolder>() {
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionName = "equipments"

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.equipement_nom)
        val mactext: TextView = itemView.findViewById(R.id.equipement_mac)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete_equi)
        val replyButton: ImageButton = itemView.findViewById(R.id.btn_update_equi)

        fun bind(equi: Equipe) {
            nameTextView.text = equi.name
            mactext.text = equi.mac

            deleteButton.setOnClickListener {
                deleteUser(equi.id)
            }

            replyButton.setOnClickListener {
                showResponseForm(equi.mac,equi.name,equi.id)
            }

            itemView.setOnClickListener {
                onItemClicked(equi)
            }
        }

        private fun deleteUser(equiId: String) {
            firestore.collection("equipments").document(equiId).delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show()
                    equi = equi.filter { it.id != equiId } // Update the list
                    notifyDataSetChanged() // Notify adapter
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erreur lors de la suppression : ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        private fun showResponseForm(mac : String ,name: String,  equiId: String) {
            val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.modifier_equipement, null)
            val emailTextView: TextView = dialogView.findViewById(R.id.wifi_nam)
            val roleSpinner: TextView = dialogView.findViewById(R.id.mac_addres)
            val saveButton: Button = dialogView.findViewById(R.id.update_buttn)
            roleSpinner.text = mac
            emailTextView.text = name

            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            saveButton.setOnClickListener {
                val emailInput = emailTextView.text.toString().trim()
                val selectedRole = roleSpinner.text.toString().trim()

                if (emailInput.isNotEmpty() && selectedRole.isNotEmpty()) {
                    val equipment = hashMapOf(
                        "name" to emailInput,
                        "mac" to selectedRole
                    )

                    firestore.collection(collectionName)
                        .document(equiId)
                        .set(equipment)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Équipement mis à jour avec succès", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Erreur lors de la mise à jour de l'équipement", Toast.LENGTH_SHORT).show()
                            Log.w("EquipementFragment", "Erreur lors de la mise à jour de l'équipement", e)
                        }
                }
                 else {
                    Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.liste_equipement, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(equi[position])
    }

    override fun getItemCount(): Int = equi.size
}
