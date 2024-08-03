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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException

class ProfileFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var telephoneEditText: EditText
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

        profileImageView = rootView.findViewById(R.id.profile_image)
        usernameEditText = rootView.findViewById(R.id.username)
        emailEditText = rootView.findViewById(R.id.email)
        telephoneEditText = rootView.findViewById(R.id.telephone)
        roleTextView = rootView.findViewById(R.id.role)
        updateButton = rootView.findViewById(R.id.update_button)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        profileImageView.setOnClickListener {
            chooseImage()
        }

        updateButton.setOnClickListener {
            uploadImage()
        }

        loadUserProfile()

        return rootView
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
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
                    Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show()
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        saveImageUrlToFirestore(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileFragment", "Failed to upload image", e)
                    Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val userDoc = firestore.collection("users").document(auth.currentUser!!.uid)
        userDoc.update("imageUrl", imageUrl)
            .addOnSuccessListener {
                Log.d("ProfileFragment", "Image URL saved to Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("ProfileFragment", "Failed to save image URL to Firestore", e)
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
                        loadImageFromUrl(imageUrl)
                    }
                } else {
                    Log.d("ProfileFragment", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ProfileFragment", "get failed with ", exception)
            }
    }

    private fun loadImageFromUrl(imageUrl: String) {
        val storageRef = storage.reference
        val islandRef = storageRef.child(imageUrl)

        val localFile = File.createTempFile("images", "jpg")

        islandRef.getFile(localFile).addOnSuccessListener {
            Log.d("ProfileFragment", "Image loaded successfully")
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            profileImageView.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("ProfileFragment", "Failed to load image", it)
            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

}
