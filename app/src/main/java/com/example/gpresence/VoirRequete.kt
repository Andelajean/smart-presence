package com.example.gpresence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class VoirRequete : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_voir_requete, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchRequests()

        return view
    }

    private fun fetchRequests() {
        firestore.collection("requests").get()
            .addOnSuccessListener { result ->
                val requests = mutableListOf<Request>()
                for (document in result) {
                    val request = document.toObject(Request::class.java).copy(id = document.id)
                    requests.add(request)
                }
                requestAdapter = RequestAdapter(requests) { selectedRequest ->
                    // Handle item click if needed
                }
                recyclerView.adapter = requestAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Erreur lors de la récupération des requêtes : ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
