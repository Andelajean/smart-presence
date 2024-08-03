package com.example.gpresence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RequestAdapter(
    private val requests: List<Request>,
    private val onItemClicked: (Request) -> Unit
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val motifTextView: TextView = itemView.findViewById(R.id.motif_text_view)
        val detailTextView: TextView = itemView.findViewById(R.id.detail_text_view)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
        val nameTextView: TextView = itemView.findViewById(R.id.request_name)
        val emailTextView: TextView = itemView.findViewById(R.id.request_email)
        val date_time : TextView = itemView.findViewById(R.id.request_dateTime)
        fun bind(request: Request) {
            nameTextView.text = request.name
            emailTextView.text = request.email
            motifTextView.text = request.motif
            detailTextView.text = request.detail
            date_time.text = request.dateTime

            deleteButton.setOnClickListener {
                deleteRequest(request.id)
            }

            itemView.setOnClickListener {
                onItemClicked(request)
            }
        }

        private fun deleteRequest(requestId: String) {
            firestore.collection("requests").document(requestId).delete()
                .addOnSuccessListener {
                    Toast.makeText(itemView.context, "Requête supprimée avec succès", Toast.LENGTH_SHORT).show()
                    notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    // Handle error
                    // Example: show a Toast message
                    Toast.makeText(itemView.context, "Erreur lors de la suppression : ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_item, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size
}
