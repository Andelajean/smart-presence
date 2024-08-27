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
import com.google.android.material.textfield.TextInputEditText
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
                showResponseForm(equi.mac, equi.name, equi.id)
            }

            itemView.setOnClickListener {
                onItemClicked(equi)
            }
        }

        private fun deleteUser(equiId: String) {
            firestore.collection("equipments").document(equiId).delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Compte supprimé avec succès", Toast.LENGTH_SHORT)
                        .show()
                    equi = equi.filter { it.id != equiId } // Update the list
                    notifyDataSetChanged() // Notify adapter
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Erreur lors de la suppression : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        private fun showResponseForm(mac: String, name: String, equiId: String) {
            // Inflate le layout personnalisé pour le formulaire de modification
            val dialogView =
                LayoutInflater.from(itemView.context).inflate(R.layout.modifier_equipement, null)

            // Référence aux composants du layout
            val wifiNameEditText: TextInputEditText = dialogView.findViewById(R.id.wifi_name)
            val macAddressEditText: TextInputEditText = dialogView.findViewById(R.id.mac_address)
            val updateButton: Button = dialogView.findViewById(R.id.update_buttn)

            // Préremplir les champs avec les valeurs actuelles
            wifiNameEditText.setText(name)
            macAddressEditText.setText(mac)

            // Créer et afficher la boîte de dialogue
            val alertDialog = AlertDialog.Builder(itemView.context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // Définir le comportement du bouton Modifier
            updateButton.setOnClickListener {
                val wifiNameInput = wifiNameEditText.text.toString().trim()
                val macAddressInput = macAddressEditText.text.toString().trim()

                if (wifiNameInput.isNotEmpty() && macAddressInput.isNotEmpty()) {
                    val equipment = hashMapOf(
                        "name" to wifiNameInput,
                        "mac" to macAddressInput
                    )

                    // Mettre à jour l'équipement dans Firestore
                    firestore.collection(collectionName)
                        .document(equiId)
                        .set(equipment)
                        .addOnSuccessListener {
                            Toast.makeText(
                                itemView.context,
                                "Équipement mis à jour avec succès",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                itemView.context,
                                "Erreur lors de la mise à jour de l'équipement",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.w(
                                "EquipementFragment",
                                "Erreur lors de la mise à jour de l'équipement",
                                e
                            )
                        }
                } else {
                    Toast.makeText(
                        itemView.context,
                        "Veuillez remplir tous les champs",
                        Toast.LENGTH_SHORT
                    ).show()
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
