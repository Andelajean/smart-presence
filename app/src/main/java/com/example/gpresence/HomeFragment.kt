package com.example.gpresence

import android.Manifest
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
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.android.synthetic.main.activity_main.*

class HomeFragment : Fragment() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnMarquerArrive: Button
    private lateinit var btnMarquerDepart: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val horaire = Horaire()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        tvWelcome = view.findViewById(R.id.tv_welcome)
        btnMarquerArrive = view.findViewById(R.id.btn_marquer_arrive)
        btnMarquerDepart = view.findViewById(R.id.btn_marquer_depart)
// Check for location permissions
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
               // Permission already granted, call the connect method
            horaire.connecter(requireContext())

        }
        // Fetch and display username
        val user = auth.currentUser
        user?.let {
            val uid = it.uid
            Log.d("HomeFragment", "User UID: $uid")
            if (uid != null) {
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val username = document.getString("username")
                            Log.d("HomeFragment", "Fetched username: $username")
                            tvWelcome.text = "Bienvenue, $username"
                        } else {
                            Log.d("HomeFragment", "Document does not exist")
                            tvWelcome.text = "Welcome back"
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("HomeFragment", "Error fetching document: $exception")
                        Toast.makeText(context, "Error fetching username", Toast.LENGTH_SHORT).show()
                    }
            }
        }


        btnMarquerArrive.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            } else {
                    horaire.connecter(requireContext())
            }
        }

        btnMarquerDepart.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            } else {
                    horaire.depart(requireContext())
                }
            }
        return view
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission accordée, appelez la méthode connecter
                    val or = Horaire()
                    or.connecter(requireContext())
                    or.depart(requireContext())
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
