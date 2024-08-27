package com.example.gpresence

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var telephoneEditText: TextInputEditText
    private lateinit var roleTextView: TextView
    private lateinit var updateButton: Button
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profil, container, false)

        // Initialisation des vues
        profileImageView = rootView.findViewById(R.id.profile_image)
        usernameEditText = rootView.findViewById(R.id.username)
        emailEditText = rootView.findViewById(R.id.email)
        telephoneEditText = rootView.findViewById(R.id.telephone)
        roleTextView = rootView.findViewById(R.id.role)
        updateButton = rootView.findViewById(R.id.update_button)

        // Initialisation des instances Firebase
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Gestion des clics
        profileImageView.setOnClickListener {
            chooseImage()
        }

        updateButton.setOnClickListener {
            if (filePath != null) {
                uploadImage()
            } else {
                updateUserProfile() // Appel si aucune image n'a été sélectionnée
            }
        }

        // Charger les informations de profil
        loadUserProfile()

        return rootView
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Sélectionnez une image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = BitmapFactory.decodeStream(requireActivity().contentResolver.openInputStream(filePath!!))
                profileImageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref = storageReference.child("images/" + auth.currentUser!!.uid)
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    Toast.makeText(context, "Image téléversée", Toast.LENGTH_SHORT).show()
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        saveImageUrlToFirestore(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileFragment", "Échec du téléversement de l'image", e)
                    Toast.makeText(context, "Échec du téléversement de l'image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        updateUserProfile(imageUrl)
    }

    private fun updateUserProfile(imageUrl: String? = null) {
        val username = usernameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val telephone = telephoneEditText.text.toString().trim()

        val userDoc = firestore.collection("users").document(auth.currentUser!!.uid)
        val updates = mutableMapOf<String, Any>(
            "username" to username,
            "email" to email,
            "telephone" to telephone
        )

        imageUrl?.let {
            updates["imageUrl"] = it
        }

        userDoc.update(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Profil mis à jour", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("ProfileFragment", "Erreur lors de la mise à jour du profil", e)
                Toast.makeText(context, "Échec de la mise à jour du profil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserProfile() {
        val userDoc = firestore.collection("users").document(auth.currentUser!!.uid)
        userDoc.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    usernameEditText.setText(document.getString("username"))
                    emailEditText.setText(document.getString("email"))
                    telephoneEditText.setText(document.getString("telephone"))
                    roleTextView.text = document.getString("role")
                    val imageUrl = document.getString("imageUrl")
                    if (imageUrl != null && imageUrl.isNotEmpty()) {
                        loadImageFromUrl(imageUrl) // Charger l'image si l'URL existe
                    }
                } else {
                    Log.d("ProfileFragment", "Aucun document trouvé")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ProfileFragment", "Échec de la récupération des données", exception)
            }
    }

    private fun loadImageFromUrl(imageUrl: String) {
        Picasso.get()
            .load(imageUrl)
            .into(profileImageView)
    }



}
