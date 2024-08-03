package com.example.gpresence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class EtatsGlobaux : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_etats_globaux, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchHoraireData()

        return view
    }

    private fun fetchHoraireData() {
        firestore.collection("horaire")
            .get()
            .addOnSuccessListener { result ->
                val horaireList = mutableListOf<HoraireRecord>()
                for (document in result) {
                    val horaire = document.toObject(HoraireRecord::class.java)
                    horaireList.add(horaire)
                }
                val adapter = StatGlobale(horaireList)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }
}
