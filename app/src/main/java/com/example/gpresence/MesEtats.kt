package com.example.gpresence

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MesEtats : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var horaireAdapter: HoraireAdapter
    private lateinit var image: ImageView
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mes_etats, container, false)
        image = view.findViewById(R.id.header_image)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Show the popup menu when the image is clicked
        image.setOnClickListener {
            showMonthPopupMenu(it)
        }

        fetchHoraire()

        return view
    }

    private fun showMonthPopupMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_mois, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val selectedMonth = when (menuItem.itemId) {
                R.id.january -> Calendar.JANUARY
                R.id.february -> Calendar.FEBRUARY
                R.id.march -> Calendar.MARCH
                R.id.april -> Calendar.APRIL
                R.id.may -> Calendar.MAY
                R.id.june -> Calendar.JUNE
                R.id.july -> Calendar.JULY
                R.id.august -> Calendar.AUGUST
                R.id.september -> Calendar.SEPTEMBER
                R.id.october -> Calendar.OCTOBER
                R.id.november -> Calendar.NOVEMBER
                R.id.december -> Calendar.DECEMBER
                else -> -1
            }
            if (selectedMonth != -1) {
                fetchHoraire(selectedMonth)
            }
            true
        }
        popupMenu.show()
    }

    private fun fetchHoraire(selectedMonth: Int? = null) {
        val currentUser = auth.currentUser
        currentUser?.let {
            val email = it.email
            if (email != null) {
                Log.d("MesEtats", "Fetching data for user email: $email")
                firestore.collection("horaire").whereEqualTo("email", email).get()
                    .addOnSuccessListener { result ->
                        val horaireList = mutableListOf<HoraireRecord>()
                        for (document in result) {
                            Log.d("MesEtats", "Document: ${document.id} => ${document.data}")
                            val horaireRecord = document.toObject(HoraireRecord::class.java)
                            selectedMonth?.let { month ->
                                // Filtrage basé sur le mois sélectionné
                                val dateStr = horaireRecord.date // Utilisation du champ `date`
                                val date = dateFormat.parse(dateStr)
                                val calendar = Calendar.getInstance()

                                date?.let {
                                    calendar.time = it
                                    val recordMonth = calendar.get(Calendar.MONTH)
                                    if (recordMonth == month) {
                                        horaireList.add(horaireRecord)
                                    }
                                }
                            } ?: run {
                                // Si aucun mois n'est sélectionné, ajouter toutes les données
                                horaireList.add(horaireRecord)
                            }
                        }
                        horaireAdapter = HoraireAdapter(horaireList)
                        recyclerView.adapter = horaireAdapter
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MesEtats", "Error fetching data", exception)
                    }
            } else {
                Log.e("MesEtats", "User email is null")
            }
        } ?: Log.e("MesEtats", "Current user is null")
    }
}
