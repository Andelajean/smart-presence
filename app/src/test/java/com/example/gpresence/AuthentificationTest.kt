package com.example.gpresence
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.gpresence.Authentification
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*

class AuthentificationTest {

    private lateinit var auth: Authentification
    private lateinit var mockContext: Context
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockUser: FirebaseUser

    @Before
    fun setUp() {
        // Initialisation des mocks
        mockContext = mock(Context::class.java)
        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        mockFirestore = mock(FirebaseFirestore::class.java)
        mockUser = mock(FirebaseUser::class.java)
        auth = Authentification(mockContext)
        val authField = Authentification::class.java.getDeclaredField("auth")
        authField.isAccessible = true
        authField.set(auth, mockFirebaseAuth)

        val firestoreField = Authentification::class.java.getDeclaredField("firestore")
        firestoreField.isAccessible = true
        firestoreField.set(auth, mockFirestore)

        `when`(mockFirebaseAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn("testUID")
    }
    @Test
    fun `registerUser should show toast when fields are empty`() {
        auth.registerUser("", "", "", "")
        verify(mockContext, times(1)).let {
            Toast.makeText(it, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }
    @Test
    fun `registerUser should show toast when passwords do not match`() {
        auth.registerUser("test@example.com", "password1", "username", "password2")
        verify(mockContext, times(1)).let {
            Toast.makeText(it, "Passwords do not match", Toast.LENGTH_SHORT).show()
        }
    }
    @Test
    fun `registerUser should call FirebaseAuth createUserWithEmailAndPassword when data is valid`() {
        auth.registerUser("test@example.com", "password", "username", "password")
        verify(mockFirebaseAuth, times(1)).createUserWithEmailAndPassword("test@example.com", "password")
    }
    @Test
    fun `registerUser should store user data in Firestore on successful registration`() {
        val mockTask = mock<Task<AuthResult>>()
        `when`(mockTask.isSuccessful).thenReturn(true)
        val taskCaptor = ArgumentCaptor.forClass(OnCompleteListener::class.java)
        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString()))
            .thenReturn(mockTask)
        auth.registerUser("test@example.com", "password", "username", "password")
        verify(mockFirestore.collection("users").document("testUID"), times(1)).set(any(Authentification.RegisterClass::class.java), any(SetOptions::class.java))
    }
    @Test
    fun `loginUser should call FirebaseAuth signInWithEmailAndPassword when data is valid`() {
        // Exécution
        auth.loginUser("test@example.com", "password")

        // Vérification que la méthode signInWithEmailAndPassword est appelée
        verify(mockFirebaseAuth, times(1)).signInWithEmailAndPassword("test@example.com", "password")
    }

    @Test
    fun `loginUser should show toast when email or password is empty`() {
        // Exécution avec email vide
        auth.loginUser("", "password")

        // Vérification que Toast.makeText() est appelé
        verify(mockContext, times(1)).let {
            Toast.makeText(it, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    @Test
    fun `loginUser should show toast on FirebaseAuthException with invalid email`() {
        // Mock l'exception FirebaseAuthException pour un email invalide
        val mockTask = mock<Task<AuthResult>>()
        val mockException = mock(FirebaseAuthException::class.java)
        `when`(mockException.errorCode).thenReturn("ERROR_INVALID_EMAIL")
        `when`(mockTask.isSuccessful).thenReturn(false)
        `when`(mockTask.exception).thenReturn(mockException)
        `when`(mockFirebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask)

        // Exécution
        auth.loginUser("invalidemail", "password")

        // Vérification que Toast.makeText() est appelé pour l'email invalide
        verify(mockContext, times(1)).let {
            Toast.makeText(it, "The email address is badly formatted.", Toast.LENGTH_SHORT).show()
        }
    }
}
