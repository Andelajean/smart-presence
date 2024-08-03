package com.example.gpresence

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CompteFragment : Fragment() {

    private lateinit var layoutCreerCompte: LinearLayout
    private lateinit var layoutSupprimerCompte: LinearLayout
    private lateinit var layoutAttribuerRole: LinearLayout
    private lateinit var etUserName: EditText
    private lateinit var etEmailCreer: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etEmailSupprimer: EditText
    private lateinit var etEmailRole: EditText
    private lateinit var roleSpinner: Spinner
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var roleManager: Role
    private lateinit var  aut : Authentification

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etUserName = view.findViewById(R.id.et_username)
        etEmailCreer = view.findViewById(R.id.et_email_creer)
        etPassword = view.findViewById(R.id.et_password)
        etConfirmPassword = view.findViewById(R.id.et_confirm_password)
        etEmailSupprimer = view.findViewById(R.id.et_email_supprimer)
        etEmailRole = view.findViewById(R.id.et_email_role)
        roleSpinner = view.findViewById(R.id.roleSpinner)
        val aut = Authentification(requireContext())
        val btnCreerCompte: Button = view.findViewById(R.id.btn_creer_compte)
        val btnSupprimerCompte: Button = view.findViewById(R.id.btn_supprimer_compte)
        val btnAttribuerRole: Button = view.findViewById(R.id.btn_attribuer_role)
        val roles = listOf("Choisir", "Admin", "RH", "CP")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        roleManager = Role(requireContext())

        layoutCreerCompte = view.findViewById(R.id.layout_creer_compte)
        layoutSupprimerCompte = view.findViewById(R.id.layout_supprimer_compte)
        layoutAttribuerRole = view.findViewById(R.id.layout_attribuer_role)

        btnCreerCompte.setOnClickListener {
            toggleVisibility(layoutCreerCompte)
        }

        btnSupprimerCompte.setOnClickListener {
            toggleVisibility(layoutSupprimerCompte)
        }

        btnAttribuerRole.setOnClickListener {
            toggleVisibility(layoutAttribuerRole)
        }

        view.findViewById<Button>(R.id.btn_creer).setOnClickListener {
            val userName = etUserName.text.toString()
            val email = etEmailCreer.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
           aut.registerUser(userName,email,password,confirmPassword)
        }

        view.findViewById<Button>(R.id.btn_supprimer).setOnClickListener {
            confirmDelete(requireContext())
        }

        view.findViewById<Button>(R.id.btn_sauvegarder).setOnClickListener {
            val email = etEmailRole.text.toString()
            val selectedRole = roleSpinner.selectedItem.toString()
            roleManager.updateUserRoleByEmail(email,selectedRole)

        }
    }

    private fun toggleVisibility(layout: LinearLayout) {
        layoutCreerCompte.visibility = View.GONE
        layoutSupprimerCompte.visibility = View.GONE
        layoutAttribuerRole.visibility = View.GONE

        layout.visibility = View.VISIBLE
    }

    private fun confirmDelete(context: Context) {
        val email = etEmailSupprimer.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(context, "Veuillez renseigner l'email", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(context)
        builder.setMessage("Voulez-vous vraiment supprimer ce compte ?")
            .setPositiveButton("Oui") { dialog, id ->
                // Vérifiez si l'utilisateur existe dans Firestore
                firestore.collection("users").whereEqualTo("email", email).get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            Toast.makeText(context, "Compte inexistant", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        // Utilisateur trouvé, suppression du compte
                        val userDocument = documents.documents[0]
                        val userId = userDocument.id

                        // Supprimer les données utilisateur de Firestore
                        firestore.collection("users").document(userId).delete()
                            .addOnSuccessListener {
                                // Supprimer l'utilisateur de l'authentification Firebase
                                auth.signInWithEmailAndPassword(email, "<andela>")
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val userToDelete = auth.currentUser
                                            userToDelete?.delete()
                                                ?.addOnCompleteListener { deleteTask ->
                                                    if (deleteTask.isSuccessful) {
                                                        Toast.makeText(context, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "Échec de la suppression du compte : ${deleteTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                        } else {
                                            Toast.makeText(context, "Échec de la connexion pour suppression : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Erreur de base de données : ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Échec de la requête de base de données : ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Non") { dialog, id ->
                dialog.dismiss()
            }
        builder.create().show()
    }


}

