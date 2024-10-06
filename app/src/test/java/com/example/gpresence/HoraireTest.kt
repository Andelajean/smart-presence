package com.example.gpresence
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class HoraireTest {

    private lateinit var horaire: Horaire
    private lateinit var mockContext: Context
    private lateinit var mockWifiManager: WifiManager
    private lateinit var mockWifiInfo: WifiInfo
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockFirestore: FirebaseFirestore
    @Before
    fun setUp() {
        horaire = Horaire()
        mockContext = mock(Context::class.java)
        mockWifiManager = mock(WifiManager::class.java)
        mockWifiInfo = mock(WifiInfo::class.java)
        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        mockFirestore = mock(FirebaseFirestore::class.java)
        `when`(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth)
        `when`(FirebaseFirestore.getInstance()).thenReturn(mockFirestore)
        `when`(mockContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(mockWifiManager)
        `when`(mockWifiManager.connectionInfo).thenReturn(mockWifiInfo)
    }
    @Test
    fun `test connecter() when connected to correct WiFi`() {
        `when`(mockWifiInfo.networkId).thenReturn(1)
        `when`(mockWifiInfo.bssid).thenReturn("00:11:22:33:44:55")

        // Mock Firestore query success
        val mockQuerySnapshot = mock(QuerySnapshot::class.java)
        `when`(mockQuerySnapshot.isEmpty).thenReturn(false)

        doAnswer {
            val onSuccessListener = it.getArgument<(QuerySnapshot) -> Unit>(0)
            onSuccessListener.invoke(mockQuerySnapshot)
            null
        }.`when`(mockFirestore.collection("equipments").whereEqualTo("mac", "00:11:22:33:44:55").get())

        // Call the method
        horaire.connecter(mockContext)

        // Verify that the correct methods were called
        verify(mockFirestore.collection("equipments").whereEqualTo("mac", "00:11:22:33:44:55"), times(1)).get()
        verify(mockContext, times(1)).getSystemService(Context.WIFI_SERVICE)
    }

    @Test
    fun `test marquerArrive() with user logged in`() {
        // Mock user authentication
        val mockUser = mock(FirebaseUser::class.java) // Utilisez FirebaseUser ici au lieu de FirebaseAuth
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.email).thenReturn("test@example.com") // Accédez à l'email via FirebaseUser

        // Appeler la méthode
        horaire.marquerArrive(mockContext)

        // Vérifiez que les bonnes méthodes ont été appelées
        verify(mockFirestore.collection("horaire").document("test@example.com-2024-09-08"), times(1)).set(any())
        verify(mockFirebaseAuth, times(1)).currentUser
    }
    @Test
    fun `test verifierEtMarquerArrive() when no previous record`() {
        val mockUser = mock(FirebaseUser::class.java)
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.email).thenReturn("test@example.com")
        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot.exists()).thenReturn(false)
        doAnswer {
            val onSuccessListener = it.getArgument<(DocumentSnapshot) -> Unit>(0)
            onSuccessListener.invoke(mockDocumentSnapshot)
            null
        }.`when`(mockFirestore.collection("horaire").document("test@example.com-2024-09-08").get())
        horaire.verifierEtMarquerArrive(mockContext)
        verify(mockFirestore.collection("horaire").document("test@example.com-2024-09-08"), times(1)).get()
        verify(mockFirestore.collection("horaire").document("test@example.com-2024-09-08"), times(1)).set(any())
    }
    @Test
    fun `test marquerDepart() when user logged in`() {
        // Mock user authentication
        val mockUser = mock(FirebaseUser::class.java)
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.email).thenReturn("test@example.com")
        horaire.marquerDepart(mockContext)
        verify(mockFirestore.collection("horaire").document("test@example.com-2024-09-08"), times(1)).update("depart", any())
    }
}
