package com.example.gpresence

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class RequestAdapter(
    private var requests: List<Request>,
    private val context: Context,
    private val onItemClicked: (Request) -> Unit
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var horaire : Horaire
    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val motifTextView: TextView = itemView.findViewById(R.id.motif_text_view)
        val detailTextView: TextView = itemView.findViewById(R.id.detail_text_view)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        val nameTextView: TextView = itemView.findViewById(R.id.request_name)
        val emailTextView: TextView = itemView.findViewById(R.id.request_email)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.request_dateTime)
        val respondImage: ImageButton = itemView.findViewById(R.id.reply_button)

        fun bind(request: Request) {
            nameTextView.text = request.name
            emailTextView.text = request.email
            motifTextView.text = request.motif
            detailTextView.text = request.detail
            dateTimeTextView.text = request.dateTime

            deleteButton.setOnClickListener {
                deleteRequest(request.id)
            }

            respondImage.setOnClickListener {
                // Afficher un formulaire de réponse avec le champ email pré-rempli
                showResponseForm(request.email)
            }

            itemView.setOnClickListener {
                onItemClicked(request)
            }
        }

        private fun deleteRequest(requestId: String) {
            firestore.collection("requests").document(requestId).delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Requête supprimée avec succès", Toast.LENGTH_SHORT).show()
                    requests = requests.filter { it.id == requestId }
                    notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erreur lors de la suppression : ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        private fun showResponseForm(email: String) {
            // Créez une vue personnalisée pour la boîte de dialogue
            val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.reponse_requete, null)
            val emailTextView: TextView = dialogView.findViewById(R.id.emailEditText)
            val arriveer: TextView = dialogView.findViewById(R.id.editarrive)
            val saveButton: Button = dialogView.findViewById(R.id.generateButton)

            // Pré-remplir le champ email
            emailTextView.text = email

            // Créez la boîte de dialogue
            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // Gérer le clic sur le bouton Sauvegarder
            saveButton.setOnClickListener {
                val email = emailTextView.text.toString().trim()
                val arrive = arriveer.text.toString().trim()
                if (email.isNotEmpty() && arrive.isNotEmpty()) {
                   val horaire = Horaire()
                    horaire.modifierHeureArrive(context, email, arrive)
                } else {
                    Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }

                // Fermer la boîte de dialogue
                alertDialog.dismiss()
            }

            // Afficher la boîte de dialogue
            alertDialog.show()
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
