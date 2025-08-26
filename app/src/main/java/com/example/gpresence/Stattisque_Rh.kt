package com.example.gpresence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment

class Stattisque_Rh : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gonfler le layout pour ce fragment
        return inflater.inflate(R.layout.fragment_stattisque__rh, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lier le Spinner
        val poureSpinner: Spinner = view.findViewById(R.id.poure)

        // Créer une liste d'options
        val options = arrayOf("pour la semaine", "pour le mois", "pour le semestre")

        // Créer un ArrayAdapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        poureSpinner.adapter = adapter
    }
}