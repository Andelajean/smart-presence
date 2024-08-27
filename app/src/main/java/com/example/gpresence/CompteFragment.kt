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

class CompteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListeCompte
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_compte, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_users)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchUsers()

        return view
    }

    private fun fetchUsers() {
        firestore.collection("users").get()
            .addOnSuccessListener { result ->
                val users = result.map { document ->
                    document.toObject(User::class.java).copy(id = document.id)
                }
                adapter = ListeCompte(users, requireContext()) { user ->
                    // Gérer le clic sur un élément utilisateur
                    Toast.makeText(requireContext(), "Utilisateur cliqué: ${user.username}", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Erreur : ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
