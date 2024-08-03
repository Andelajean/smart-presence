package com.example.gpresence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StatGlobale(private val horaireList: List<HoraireRecord>) : RecyclerView.Adapter<StatGlobale.HoraireViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoraireViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.satglabale, parent, false)
        return HoraireViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoraireViewHolder, position: Int) {
        val horaire = horaireList[position]
        holder.emailTextView.text = horaire.email
        holder.dateTextView.text = horaire.date
        holder.arriveTextView.text = horaire.arrive
        holder.departTextView.text = horaire.depart
    }

    override fun getItemCount(): Int {
        return horaireList.size
    }

    inner class HoraireViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val arriveTextView: TextView = itemView.findViewById(R.id.arriveTextView)
        val departTextView: TextView = itemView.findViewById(R.id.departTextView)
    }
}
