package com.example.gpresence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class RequestsFragment : Fragment() {

    private lateinit var requestMotif: EditText
    private lateinit var requestDetail: EditText
    private lateinit var submitButton: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_requete, container, false)

        // Initialize views
        requestMotif = view.findViewById(R.id.request_motif)
        requestDetail = view.findViewById(R.id.request_detail)
        submitButton = view.findViewById(R.id.submit_request_button)

        // Initialize Firebase Auth and Firestore
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        submitButton.setOnClickListener {
            submitRequest()
        }

        return view
    }

    private fun submitRequest() {
        val motif = requestMotif.text.toString().trim()
        val detail = requestDetail.text.toString().trim()
        val user = auth.currentUser

        if (user != null && motif.isNotEmpty() && detail.isNotEmpty()) {
            val email = user.email
            val userId = user.uid
            val dateTime = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date())

            // Fetch the username from Firestore
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("username") // Removed default value

                        // Create a request object
                        val request = hashMapOf(
                            "motif" to motif,
                            "detail" to detail,
                            "email" to email,
                            "name" to name,
                            "dateTime" to dateTime
                        )

                        // Add a new document with a generated ID
                        firestore.collection("requests")
                            .add(request)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(requireContext(), "Request submitted successfully!", Toast.LENGTH_SHORT).show()
                                clearForm()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Error submitting request: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearForm() {
        requestMotif.text.clear()
        requestDetail.text.clear()
    }
}
