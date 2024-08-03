package com.example.gpresence

data class HoraireRecord(
    val arrive: String? = null,
    val depart: String? = null,
    val date: String? = null,
    val email: String? = null  // Ensure this field is included if it's in your Firestore documents
)

