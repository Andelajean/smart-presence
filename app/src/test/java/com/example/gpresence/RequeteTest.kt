package com.example.gpresence
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import com.google.firebase.firestore.CollectionReference
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseUser
import org.mockito.stubbing.OngoingStubbing

class RequeteTest {
    @get:Rule
    private lateinit var fragment: RequestsFragment
    private val firestore = mock(FirebaseFirestore::class.java)
    private val auth = mock(FirebaseAuth::class.java)
    private val user = mock(FirebaseUser::class.java)
    private val documentSnapshot = mock(DocumentSnapshot::class.java)
    private val task = mock(Task::class.java) as Task<DocumentSnapshot>

    @Before
    fun setUp() {
        fragment = RequestsFragment()
        fragment.firestore = firestore
        fragment.auth = auth

        `when`(auth.currentUser).thenReturn(user)
        `when`(user.email).thenReturn("test@example.com")
        `when`(user.uid).thenReturn("user_id")
    }

    @Test
    fun testSubmitRequestSuccess() {
        // Arrange
        `when`(documentSnapshot.exists()).thenReturn(true)
        `when`(documentSnapshot.getString("username")).thenReturn("testuser")
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.result).thenReturn(documentSnapshot)
        `when`(firestore.collection("users").document("user_id").get()).thenReturn(task)

        val requestMotif = "Test Motif"
        val requestDetail = "Test Detail"
        `when`(fragment.requestMotif.text.toString()).thenReturn(requestMotif)
        `when`(fragment.requestDetail.text.toString()).thenReturn(requestDetail)

        val requestCollection = mock(CollectionReference::class.java)
        val requestTask = mock(Task::class.java) as Task<Void>
        `when`(requestCollection.add(any())).thenReturn(requestTask)
        `when`(requestTask.isSuccessful).thenReturn(true)
        `when`(firestore.collection("requests")).thenReturn(requestCollection)

        // Act
        fragment.submitRequest()


    }

    @Test
    fun testSubmitRequestFailure() {
        // Arrange
        `when`(documentSnapshot.exists()).thenReturn(true)
        `when`(documentSnapshot.getString("username")).thenReturn("testuser")
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.result).thenReturn(documentSnapshot)
        `when`(firestore.collection("users").document("user_id").get()).thenReturn(task)

        val requestMotif = "Test Motif"
        val requestDetail = "Test Detail"
        `when`(fragment.requestMotif.text.toString()).thenReturn(requestMotif)
        `when`(fragment.requestDetail.text.toString()).thenReturn(requestDetail)

        val requestCollection = mock(CollectionReference::class.java)
        val requestTask = mock(Task::class.java) as Task<Void>
        `when`(requestCollection.add(any())).thenReturn(requestTask)
        `when`(requestTask.isSuccessful).thenReturn(false)
        `when`(firestore.collection("requests")).thenReturn(requestCollection)

        // Act
        fragment.submitRequest()

        // Assert
        // Verify Toast message and Firestore interactions
    }
}

 fun <T> OngoingStubbing<T>.thenReturn(requestTask: Task<Void>) {

}
