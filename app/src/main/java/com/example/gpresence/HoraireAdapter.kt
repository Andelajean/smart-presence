package com.example.gpresence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HoraireAdapter(private val horaireList: List<HoraireRecord>) : RecyclerView.Adapter<HoraireAdapter.HoraireViewHolder>() {

    class HoraireViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val arriveTextView: TextView = itemView.findViewById(R.id.arrive_text)
        val departTextView: TextView = itemView.findViewById(R.id.depart_text)
        val dateTextView: TextView = itemView.findViewById(R.id.date_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoraireViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horaire, parent, false)
        return HoraireViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoraireViewHolder, position: Int) {
        val record = horaireList[position]
        holder.arriveTextView.text = record.arrive ?: "No Arrival Time"
        holder.departTextView.text = record.depart ?: "No Departure Time"
        holder.dateTextView.text = record.date ?: "No Date"
    }

    override fun getItemCount(): Int {
        return horaireList.size
    }
}
