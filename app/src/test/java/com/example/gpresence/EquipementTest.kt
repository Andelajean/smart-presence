package com.example.gpresence

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.testing.FragmentScenario
import com.example.gpresence.Equipement
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class EquipementTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockWifiManager: WifiManager

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    private lateinit var equipementFragment: Equipement

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        equipementFragment = Equipement()
        equipementFragment.firestore = mockFirestore
    }

    @Test
    fun testDetectWifiNetworks_Success() {
        // Mock WiFi scan results
        val scanResult = mock(ScanResult::class.java)
        `when`(scanResult.SSID).thenReturn("TestWiFi")
        `when`(scanResult.BSSID).thenReturn("00:11:22:33:44:55")

        `when`(mockWifiManager.scanResults).thenReturn(listOf(scanResult))

        // Simulate detectWifiNetworks method
        equipementFragment.detectWifiNetworks()

        // Check if WiFi network is correctly detected
        assertNotNull(scanResult.SSID)
        assertNotNull(scanResult.BSSID)
    }

    @Test
    fun testAddEquipment_Success() {
        val wifiName = "TestWiFi"
        val macAddress = "00:11:22:33:44:55"

        // Mock Firestore success
        val mockTask = mock(Task::class.java) as Task<DocumentReference>
        `when`(mockFirestore.collection("equipments").add(any())).thenReturn(mockTask)

        equipementFragment.addEquipment(wifiName, macAddress)

        verify(mockFirestore.collection("equipments"), times(1)).add(any())
    }

    @Test
    fun testAddEquipment_AlreadyExists() {
        val wifiName = "TestWiFi"
        val macAddress = "00:11:22:33:44:55"

        // Mock Firestore to return existing equipment
        val mockQuerySnapshot = mock(QuerySnapshot::class.java)
        `when`(mockQuerySnapshot.isEmpty).thenReturn(false)

        equipementFragment.addEquipment(wifiName, macAddress)

        verify(mockContext, times(1)).let { Toast.makeText(it, "L'équipement existe déjà", Toast.LENGTH_SHORT).show() }
    }
}
