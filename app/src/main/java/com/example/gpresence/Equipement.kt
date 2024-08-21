package com.example.gpresence

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Equipement : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EquipementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_equipement, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_equipem)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val ajouter = view.findViewById<ImageButton>(R.id.ad_button)
      ajouter.setOnClickListener {
          showResetPasswordDialog()
      }
        fetchUsers()

        return view
    }

    private fun fetchUsers() {
        firestore.collection("equipments").get()
            .addOnSuccessListener { result ->
                val equi = result.map { document ->
                    document.toObject(Equipe::class.java).copy(id = document.id)
                }
                // Assurez-vous que l'adaptateur est de type EquipementAdapter
                adapter = EquipementAdapter(equi, requireContext()) { equi ->
                    // Gérer le clic sur un élément équipement
                    Toast.makeText(requireContext(), "Équipement cliqué: ${equi.name}", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erreur : ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun showResetPasswordDialog() {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.ajouter_equipement, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.wifi_name)
        val mac = dialogView.findViewById<EditText>(R.id.mac_address)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<Button>(R.id.ajout).setOnClickListener {
            val nom = emailEditText.text.toString().trim()
            val adresse = mac.text.toString().trim()
            if (nom.isEmpty() || adresse.isEmpty()) {
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                addEquipment(nom, adresse)
            }
        }

        alertDialog.show()
    }
    private fun addEquipment(name: String, mac: String) {
        val equipment = hashMapOf(
            "name" to name,
            "mac" to mac
        )

        firestore.collection("equipments")
            .add(equipment)
            .addOnSuccessListener {
                Toast.makeText(context, "Équipement ajouté avec succès", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erreur lors de l'ajout de l'équipement", Toast.LENGTH_SHORT).show()
                Log.w("EquipementFragment", "Erreur lors de l'ajout de l'équipement", e)
            }
    }
}
