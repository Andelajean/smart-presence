package com.example.gpresence
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.example.gpresence.ProfileFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
class ProfilTest {
    private lateinit var fragmentScenario: FragmentScenario<ProfileFragment>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    @Before
    fun setup() {
        firestore = mock(FirebaseFirestore::class.java)
        auth = mock(FirebaseAuth::class.java)
        storage = mock(FirebaseStorage::class.java)

        //`when`(auth.currentUser).thenReturn(mock(FirebaseAuth::class.java))
        `when`(auth.currentUser!!.uid).thenReturn("user_id")

        fragmentScenario = launchFragmentInContainer<ProfileFragment>(Bundle(), R.style.AppTheme)
    }

    @Test
    fun testLoadUserProfile() {
        val documentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(documentSnapshot.getString("username")).thenReturn("testuser")
        `when`(documentSnapshot.getString("email")).thenReturn("test@example.com")
        `when`(documentSnapshot.getString("telephone")).thenReturn("123456789")
        `when`(documentSnapshot.getString("role")).thenReturn("Admin")
        `when`(documentSnapshot.getString("imageUrl")).thenReturn("http://example.com/image.jpg")
        val task = mock(Task::class.java) as Task<DocumentSnapshot>
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.result).thenReturn(documentSnapshot)
        `when`(firestore.collection("users").document("user_id").get()).thenReturn(task)
        fragmentScenario.onFragment { fragment ->
            fragment.loadUserProfile()
            val usernameEditText = fragment.view?.findViewById<TextView>(R.id.username)
            val emailEditText = fragment.view?.findViewById<TextView>(R.id.email)
            val telephoneEditText = fragment.view?.findViewById<TextView>(R.id.telephone)
            val roleTextView = fragment.view?.findViewById<TextView>(R.id.role)

            assert(usernameEditText?.text == "testuser")
            assert(emailEditText?.text == "test@example.com")
            assert(telephoneEditText?.text == "123456789")
            assert(roleTextView?.text == "Admin")
        }
    }
    }

