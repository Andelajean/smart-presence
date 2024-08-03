package com.example.gpresence

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class Equipement : Fragment() {

    private lateinit var buttonAdd: Button
    private lateinit var buttonDelete: Button
    private lateinit var buttonEdit: Button
    private lateinit var formContainer: LinearLayout
    private lateinit var buttonSubmit: Button
    private lateinit var wifiName: EditText
    private lateinit var macAddress: EditText

    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "equipments"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_equipement, container, false)

        buttonAdd = view.findViewById(R.id.button_add)
        buttonDelete = view.findViewById(R.id.button_delete)
        buttonEdit = view.findViewById(R.id.button_edit)
        formContainer = view.findViewById(R.id.form_container)
        buttonSubmit = view.findViewById(R.id.button_submit)
        wifiName = view.findViewById(R.id.wifi_name)
        macAddress = view.findViewById(R.id.mac_address)

        buttonAdd.setOnClickListener {
            showForm("Ajouter")
        }

        buttonDelete.setOnClickListener {
            showForm("Supprimer")
        }

        buttonEdit.setOnClickListener {
            showForm("Modifier")
        }

        buttonSubmit.setOnClickListener {
            submitForm()
        }

        return view
    }

    private fun showForm(action: String) {
        formContainer.visibility = View.VISIBLE
        buttonSubmit.text = action
    }

    private fun submitForm() {
        val name = wifiName.text.toString()
        val mac = macAddress.text.toString()
        val action = buttonSubmit.text.toString()

        if (name.isEmpty() || mac.isEmpty()) {
            Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        when (action) {
            "Ajouter" -> addEquipment(name, mac)
            "Supprimer" -> fetchEquipmentForAction("Supprimer", name, mac)
            "Modifier" -> fetchEquipmentForAction("Modifier", name, mac)
        }
    }

    private fun addEquipment(name: String, mac: String) {
        val equipment = hashMapOf(
            "name" to name,
            "mac" to mac
        )

        db.collection(collectionName)
            .add(equipment)
            .addOnSuccessListener {
                Toast.makeText(context, "Équipement ajouté avec succès", Toast.LENGTH_SHORT).show()
                formContainer.visibility = View.GONE
                clearForm()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erreur lors de l'ajout de l'équipement", Toast.LENGTH_SHORT).show()
                Log.w("EquipementFragment", "Erreur lors de l'ajout de l'équipement", e)
            }
    }

    private fun fetchEquipmentForAction(action: String, name: String, mac: String) {
        db.collection(collectionName)
            .whereEqualTo("name", name)
            .whereEqualTo("mac", mac)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        when (action) {
                            "Supprimer" -> deleteEquipment(document.id)
                            "Modifier" -> updateEquipment(document.id, name, mac)
                        }
                    }
                } else {
                    Toast.makeText(context, "Équipement non trouvé", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erreur lors de la récupération de l'équipement", Toast.LENGTH_SHORT).show()
                Log.w("EquipementFragment", "Erreur lors de la récupération de l'équipement", e)
            }
    }

    private fun deleteEquipment(documentId: String) {
        db.collection(collectionName)
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Équipement supprimé avec succès", Toast.LENGTH_SHORT).show()
                formContainer.visibility = View.GONE
                clearForm()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erreur lors de la suppression de l'équipement", Toast.LENGTH_SHORT).show()
                Log.w("EquipementFragment", "Erreur lors de la suppression de l'équipement", e)
            }
    }

    private fun updateEquipment(documentId: String, name: String, mac: String) {
        val equipment = hashMapOf(
            "name" to name,
            "mac" to mac
        )

        db.collection(collectionName)
            .document(documentId)
            .set(equipment)
            .addOnSuccessListener {
                Toast.makeText(context, "Équipement mis à jour avec succès", Toast.LENGTH_SHORT).show()
                formContainer.visibility = View.GONE
                clearForm()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erreur lors de la mise à jour de l'équipement", Toast.LENGTH_SHORT).show()
                Log.w("EquipementFragment", "Erreur lors de la mise à jour de l'équipement", e)
            }
    }

    private fun clearForm() {
        wifiName.text.clear()
        macAddress.text.clear()
    }
}
