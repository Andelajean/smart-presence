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
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EtatsGlobaux : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var image: ImageView
    private lateinit var horaireAdapter: StatGlobale
    private val firestore = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_etats_globaux, container, false)
        image = view.findViewById(R.id.header_image)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Show the popup menu when the image is clicked
        image.setOnClickListener {
            showMonthPopupMenu(it)
        }

        // Fetch all data initially
        fetchHoraireData(null)

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
                fetchHoraireData(selectedMonth)
            }
            true
        }
        popupMenu.show()
    }

    private fun fetchHoraireData(selectedMonth: Int?) {
        firestore.collection("horaire")
            .get()
            .addOnSuccessListener { result ->
                val horaireList = mutableListOf<HoraireRecord>()
                for (document in result) {
                    val horaireRecord = document.toObject(HoraireRecord::class.java)
                    // Filter based on the selected month, if provided
                    val dateStr = horaireRecord.date // Use the `date` field
                    val date = dateFormat.parse(dateStr)
                    val calendar = Calendar.getInstance()

                    date?.let {
                        calendar.time = it
                        val recordMonth = calendar.get(Calendar.MONTH)
                        if (selectedMonth == null || recordMonth == selectedMonth) {
                            horaireList.add(horaireRecord)
                        }
                    }
                }
                horaireAdapter = StatGlobale(horaireList)
                recyclerView.adapter = horaireAdapter
            }
            .addOnFailureListener { exception ->
                Log.e("EtatsGlobaux", "Error fetching data", exception)
            }
    }
}
