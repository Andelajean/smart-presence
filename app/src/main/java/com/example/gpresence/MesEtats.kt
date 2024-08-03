package com.example.gpresence

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MesEtats : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var horaireAdapter: HoraireAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mes_etats, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchHoraire()

        return view
    }

    private fun fetchHoraire() {
        val currentUser = auth.currentUser
        currentUser?.let {
            val email = it.email
            if (email != null) {
                Log.d("MesEtats", "Fetching data for user email: $email")
                firestore.collection("horaire").whereEqualTo("email", email).get()
                    .addOnSuccessListener { result ->
                        val horaireList = mutableListOf<HoraireRecord>()
                        for (document in result) {
                            Log.d("MesEtats", "Document: ${document.id} => ${document.data}")
                            val horaireRecord = document.toObject(HoraireRecord::class.java)
                            horaireList.add(horaireRecord)
                        }
                        horaireAdapter = HoraireAdapter(horaireList)
                        recyclerView.adapter = horaireAdapter
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MesEtats", "Error fetching data", exception)
                    }
            } else {
                Log.e("MesEtats", "User email is null")
            }
        } ?: Log.e("MesEtats", "Current user is null")
    }
}
