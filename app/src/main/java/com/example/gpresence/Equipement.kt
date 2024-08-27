package com.example.gpresence

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Equipement : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EquipementAdapter
    private lateinit var detect: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_equipement, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_equipem)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val ajouter = view.findViewById<ImageButton>(R.id.ad_button)
        detect = view.findViewById(R.id.detect_button)

        ajouter.setOnClickListener {
            showAddEquipement()
        }

        detect.setOnClickListener {
            detectWifiNetworks()
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

    private fun showAddEquipement() {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.ajouter_equipement, null)

        // Find the input fields and button from the inflated layout
        val wifiNameEditText = dialogView.findViewById<EditText>(R.id.wifi_name)
        val macAddressEditText = dialogView.findViewById<EditText>(R.id.mac_address)
        val addButton = dialogView.findViewById<Button>(R.id.ajout)

        // Create and configure the AlertDialog
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Set an OnClickListener for the 'Ajouter' button
        addButton.setOnClickListener {
            val wifiName = wifiNameEditText.text.toString().trim()
            val macAddress = macAddressEditText.text.toString().trim()

            if (wifiName.isEmpty() || macAddress.isEmpty()) {
                // Show a toast message if any field is empty
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                // Call the function to add equipment with the provided wifi name and MAC address
                addEquipment(wifiName, macAddress)
                alertDialog.dismiss() // Dismiss the dialog after adding the equipment
            }
        }

        // Show the AlertDialog
        alertDialog.show()
    }

    private fun detectWifiNetworks() {
        // Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }

        // Get the WifiManager
        val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiScanResults = wifiManager.scanResults
        val wifiList = wifiScanResults.map { scanResult ->
            // Use SSID and BSSID (if accessible)
            val wifiName = scanResult.SSID
            val macAddress = scanResult.BSSID ?: "N/A" // BSSID is the MAC address of the access point, if available
            "$wifiName (MAC: $macAddress)"
        }

        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.ajouter_equipement, null)

        // Find the input fields and button from the inflated layout
        val wifiNameEditText = dialogView.findViewById<EditText>(R.id.wifi_name)
        val macAddressEditText = dialogView.findViewById<EditText>(R.id.mac_address)
        val addButton = dialogView.findViewById<Button>(R.id.ajout)

        // Create and configure the AlertDialog
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Update the WiFi name EditText with the detected networks
        if (wifiList.isNotEmpty()) {
            // Assuming you want to display the first detected network as an example
            val firstWifi = wifiList.first()
            wifiNameEditText.setText(firstWifi.substringBefore(" (MAC:"))
            macAddressEditText.setText(firstWifi.substringAfter("MAC: ").replace(")", ""))
        }

        // Set an OnClickListener for the 'Ajouter' button
        addButton.setOnClickListener {
            val wifiName = wifiNameEditText.text.toString().trim()
            val macAddress = macAddressEditText.text.toString().trim()

            if (wifiName.isEmpty() || macAddress.isEmpty()) {
                // Show a toast message if any field is empty
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                // Call the function to add equipment with the provided wifi name and MAC address
                addEquipment(wifiName, macAddress)
                alertDialog.dismiss() // Dismiss the dialog after adding the equipment
            }
        }

        // Show the AlertDialog
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
