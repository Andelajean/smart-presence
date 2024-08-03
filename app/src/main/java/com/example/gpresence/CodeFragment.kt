package com.example.gpresence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class CodeFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var editarrive: EditText
    private lateinit var generateButton: Button

    private lateinit var arriver: Horaire

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialiser les vues
        emailEditText = view.findViewById(R.id.emailEditText)
        editarrive = view.findViewById(R.id.editarrive)
        generateButton = view.findViewById(R.id.generateButton)

        arriver = Horaire()

        generateButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val arrive = editarrive.text.toString().trim()

            if (email.isNotEmpty() && arrive.isNotEmpty()) {
               arriver.modifierHeureArrive(requireContext() , email, arrive)
            } else {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
