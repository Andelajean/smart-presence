package com.example.gpresence

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashbord, container, false)

        /* Demander les permissions si nécessaire
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
          //  connecter(requireContext(), companyWifiMacAddress)
        }*/

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 ->HomeFragment()
                    1 -> MesEtats()

                    else -> throw IllegalStateException("Unexpected position $position")
                }
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Home"
                1 -> "Mes Etats"
                else -> null
            }
        }.attach()

        return view
    }

   /* override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission accordée, appelez la méthode connecter
                    connecter(requireContext(), companyWifiMacAddress)
                } else {
                    // Permission refusée, affichez un message ou gérez le cas
                    Toast.makeText(
                        requireContext(), "Permission refusée, impossible de vérifier le WiFi",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun connecter(context: Context, companyWifiMacAddress: String) {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo

        if (wifiInfo != null && wifiInfo.networkId != -1) {
            val currentWifiMacAddress: String
            val wifiSSID = wifiInfo.ssid

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Requires ACCESS_FINE_LOCATION permission
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission if not already granted
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                    return
                }
                currentWifiMacAddress = wifiInfo.bssid
            } else {
                currentWifiMacAddress = wifiInfo.bssid
            }

            // Display WiFi SSID and MAC address
            Toast.makeText(context, "Nom du WiFi : $wifiSSID\nAdresse MAC : $currentWifiMacAddress", Toast.LENGTH_LONG).show()

            if (currentWifiMacAddress.equals(companyWifiMacAddress, ignoreCase = true)) {
                Toast.makeText(context, "Vous êtes connecté au WiFi de l'entreprise", Toast.LENGTH_LONG).show()
                // MarquerPresence(context)
            } else {
                Toast.makeText(context, "Vous n'êtes pas connecté au WiFi de l'entreprise", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Aucun WiFi actif détecté", Toast.LENGTH_LONG).show()
        }
    }*/
}
