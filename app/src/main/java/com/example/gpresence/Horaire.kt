package com.example.gpresence

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Horaire {
    private val db = FirebaseFirestore.getInstance()

    public  fun connecter(context: Context) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo

        if (wifiInfo != null && wifiInfo.networkId != -1) {
            val currentWifiMacAddress: String
            val wifiSSID = wifiInfo.ssid

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Requires ACCESS_FINE_LOCATION permission
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission if not already granted
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                    return
                }
                currentWifiMacAddress = wifiInfo.bssid
            } else {
                currentWifiMacAddress = wifiInfo.bssid
            }

            // Display WiFi SSID and MAC address
           // Toast.makeText(context, "Nom du WiFi : $wifiSSID\nAdresse MAC : $currentWifiMacAddress", Toast.LENGTH_LONG).show()

            // Vérifiez si l'adresse MAC est enregistrée dans la collection 'equipement'
            val db = FirebaseFirestore.getInstance()
            db.collection("equipments")
                .whereEqualTo("mac", currentWifiMacAddress)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        //Toast.makeText(context, "Vous êtes connecté au WiFi de l'entreprise", Toast.LENGTH_LONG).show()
                        verifierEtMarquerArrive(context)
                    } else {
                        Toast.makeText(context, "Vous n'êtes pas connecté au WiFi de l'entreprise", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erreur lors de la vérification du WiFi", Toast.LENGTH_SHORT).show()
                    Log.w("WiFiVerification", "Erreur lors de la vérification du WiFi", e)
                }
        } else {
            Toast.makeText(context, "Aucun WiFi actif détecté", Toast.LENGTH_LONG).show()
        }
    }

   public  fun depart(context: Context) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo

        if (wifiInfo != null && wifiInfo.networkId != -1) {
            val currentWifiMacAddress: String
            val wifiSSID = wifiInfo.ssid

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Requires ACCESS_FINE_LOCATION permission
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission if not already granted
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                    return
                }
                currentWifiMacAddress = wifiInfo.bssid
            } else {
                currentWifiMacAddress = wifiInfo.bssid
            }

            // Display WiFi SSID and MAC address
           // Toast.makeText(context, "Nom du WiFi : $wifiSSID\nAdresse MAC : $currentWifiMacAddress", Toast.LENGTH_LONG).show()

            // Vérifiez si l'adresse MAC est enregistrée dans la collection 'equipement'
            val db = FirebaseFirestore.getInstance()
            db.collection("equipments")
                .whereEqualTo("mac", currentWifiMacAddress)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                       // Toast.makeText(context, "Vous êtes connecté au WiFi de l'entreprise", Toast.LENGTH_LONG).show()
                        marquerDepart(context)
                    } else {
                        Toast.makeText(context, "Vous n'êtes pas connecté au WiFi de l'entreprise", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erreur lors de la vérification du WiFi", Toast.LENGTH_SHORT).show()
                    Log.w("WiFiVerification", "Erreur lors de la vérification du WiFi", e)
                }
        } else {
            Toast.makeText(context, "Aucun WiFi actif détecté", Toast.LENGTH_LONG).show()
        }
    }
// méthode pour marquer l'arrivée
    fun marquerArrive(context: Context) {
        val email = FirebaseAuth.getInstance().currentUser?.email
        val heure = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (email == null) {
            Toast.makeText(context, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            return
        }

        val presenceData = mapOf(
            "arrive" to heure,
            "depart" to null,
            "date" to date,
            "email" to email
        )

        db.collection("horaire").document("$email-$date")
            .set(presenceData)
            .addOnSuccessListener {
                Toast.makeText(context, "Heure d'arrivée enregistrée avec succès", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erreur lors de l'enregistrement de l'heure d'arrivée", Toast.LENGTH_SHORT).show()
            }
    }
// méthode pour marquer le départ
    fun marquerDepart(context: Context) {
        val email = FirebaseAuth.getInstance().currentUser?.email
        val heure = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (email == null) {
            Toast.makeText(context, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("horaire").document("$email-$date")
            .update("depart", heure)
            .addOnSuccessListener {
                Toast.makeText(context, "Heure de départ enregistrée avec succès", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erreur lors de l'enregistrement de l'heure de départ", Toast.LENGTH_SHORT).show()
            }
    }
    // méthode pour vérifier si l'utilisateur a déjà marqué son heure d'arrivée
    fun verifierEtMarquerArrive(context: Context) {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser?.email
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (email == null) {
            Toast.makeText(context, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            return
        }

        val documentId = "$email-$date"
        val documentRef = db.collection("horaire").document(documentId)

        documentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.get("arrive") != null) {
                    Toast.makeText(context, "Vous avez déjà marqué votre arrivée aujourd'hui", Toast.LENGTH_SHORT).show()
                } else {
                    marquerArrive(context)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erreur lors de la vérification de l'heure d'arrivée", Toast.LENGTH_SHORT).show()
            }
    }
    // methode qui permet de repondre à la requete , en modifiant l'heure d'arrivée sans toute fois attendre que l'utilisateur marque sa presence
    fun modifierHeureArrive(context: Context, email: String, nouvelleHeure: String) {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (email.isEmpty()) {
            Toast.makeText(context, "Email de l'utilisateur non fourni", Toast.LENGTH_SHORT).show()
            return
        }

        val documentRef = db.collection("horaire").document("$email-$date")

        documentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // L'utilisateur a déjà marqué son arrivée, on met à jour l'heure d'arrivée
                    documentRef.update("arrive", nouvelleHeure)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Heure d'arrivée mise à jour avec succès", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Erreur lors de la mise à jour de l'heure d'arrivée", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // L'utilisateur n'a pas encore marqué son arrivée, on ajoute un listener
                    Toast.makeText(context, "L'utilisateur n'a pas encore marqué son arrivée. Attente de l'arrivée...", Toast.LENGTH_SHORT).show()

                    // Ajouter un listener pour surveiller les modifications du document
                    documentRef.addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Toast.makeText(context, "Erreur lors de l'écoute des changements", Toast.LENGTH_SHORT).show()
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            // Une fois que l'utilisateur marque son arrivée, mettre à jour l'heure
                            documentRef.update("arrive", nouvelleHeure)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Heure d'arrivée mise à jour avec succès", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(context, "Erreur lors de la mise à jour de l'heure d'arrivée", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erreur lors de la vérification de l'heure d'arrivée", Toast.LENGTH_SHORT).show()
            }
    }


}
