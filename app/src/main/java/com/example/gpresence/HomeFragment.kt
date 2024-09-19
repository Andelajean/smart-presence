package com.example.gpresence

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnMarquerArrive: Button
    private lateinit var btnMarquerDepart: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var profileImageView: ImageView
    private lateinit var notificationDot: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        tvWelcome = view.findViewById(R.id.tv_welcome)
        btnMarquerArrive = view.findViewById(R.id.btn_marquer_arrive)
        btnMarquerDepart = view.findViewById(R.id.btn_marquer_depart)
        profileImageView = view.findViewById(R.id.center_image)
         notificationDot = view.findViewById(R.id.notification_dot)

        notificationDot.setOnClickListener {
            showNotificationDialog()
        }

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            // Permission already granted, call the connect method
            val horaire = Horaire()
            horaire.connecter(requireContext())
        }

        checkUserRoleAndUpdateUI()
        fetchUserData()

        btnMarquerArrive.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            } else {
                val horaire = Horaire()
                horaire.connecter(requireContext())
            }
        }

        btnMarquerDepart.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            } else {
                val horaire = Horaire()
                horaire.depart(requireContext())
            }
        }

        return view
    }

    private fun fetchUserData() {
        val user = auth.currentUser
        user?.let {
            val uid = it.uid
            if (uid != null) {
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val username = document.getString("username")
                            val profileImageUrl = document.getString("imageUrl")

                            tvWelcome.text = username

                            if (!profileImageUrl.isNullOrEmpty()) {
                                Picasso.get().load(profileImageUrl).into(profileImageView)
                            } else {
                                Toast.makeText(context, "Photo de profil non disponible", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            tvWelcome.text = "Welcome back"
                            Toast.makeText(context, "Document does not exist", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Erreur lors de la récupération des informations: $exception", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun checkUserRoleAndUpdateUI() {
        val currentUser = auth.currentUser
        currentUser?.let {
            firestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val role = document.getString("role")
                        if (role == "Admin") {
                            checkForUnreadNotifications()
                        } else {
                            notificationDot.visibility = View.GONE
                        }
                    } else {
                        notificationDot.visibility = View.GONE
                    }
                }
                .addOnFailureListener { exception ->
                    notificationDot.visibility = View.GONE
                    Toast.makeText(context, "Erreur lors de la récupération du rôle utilisateur: $exception", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            notificationDot.visibility = View.GONE
        }
    }

    private fun checkForUnreadNotifications() {
        val user = auth.currentUser
        user?.let {
            firestore.collection("requests")
                .whereEqualTo("status", "unread")
                .get()
                .addOnSuccessListener { result ->
                    val unreadCount = result.size()
                    if (unreadCount > 0) {
                        notificationDot.visibility = View.VISIBLE
                        notificationDot.text = unreadCount.toString()
                    } else {
                        notificationDot.visibility = View.GONE
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error fetching notifications: $exception", Toast.LENGTH_SHORT).show()
                    notificationDot.visibility = View.GONE
                }
        }
    }


    private fun showNotificationDialog() {
        val user = auth.currentUser
        user?.let {
            firestore.collection("requests")
                .whereEqualTo("status", "unread")
                .get()
                .addOnSuccessListener { result ->
                    val unreadCount = result.size()
                    val dialogBuilder = AlertDialog.Builder(requireContext())
                    dialogBuilder.setTitle("Notifications")
                    dialogBuilder.setMessage("Vous avez  $unreadCount notifications non lues.")
                    dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                        // Mark all notifications as read
                        firestore.collection("requests")
                            .whereEqualTo("status", "unread")
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    document.reference.update("status", "read")
                                }
                                notificationDot.visibility = View.GONE
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(context, "Error updating notifications: $exception", Toast.LENGTH_SHORT).show()
                            }
                        dialog.dismiss()
                    }
                    dialogBuilder.create().show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error fetching notifications: $exception", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission accordée, appelez la méthode connecter
                    val horaire = Horaire()
                    horaire.connecter(requireContext())
                    horaire.depart(requireContext())
                } else {
                    // Permission refusée, affichez un message ou gérez le cas
                    Toast.makeText(
                        context,
                        "Permission refusée, impossible de vérifier le WiFi",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
