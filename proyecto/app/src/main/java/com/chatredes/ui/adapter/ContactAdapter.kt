package com.chatredes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.chatredes.R
import com.chatredes.domain.models.Contact

class ContactAdapter(
    private val dataset : MutableList<Contact>,
    private val listener: RecyclerViewContactEvents
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(){

    class ViewHolder(
        val view: View,
        val listener: RecyclerViewContactEvents
    ): RecyclerView.ViewHolder(view){

        private val layoutContact: ConstraintLayout = view.findViewById(R.id.contactscard)
        private val tvContact: TextView = view.findViewById(R.id.usernameContact)
        private val tvStatus: TextView = view.findViewById(R.id.disponibilityContact)
        private val tvStatusMessage: TextView = view.findViewById(R.id.estadoContact)

        fun setData(contact: Contact){
            val username = contact.username.split("@")[0]
            tvContact.text = username
            tvStatus.text = contact.status
            tvStatusMessage.text = contact.statusMessage

            layoutContact.setOnClickListener {
                listener.onContactClick(contact)
            }
        }


    }

    interface RecyclerViewContactEvents{
        fun onContactClick(contact: Contact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chats, parent, false)

        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(dataset[position])
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    fun updateContacts(newContacts: List<Contact>) {
        dataset.clear()  // Limpiar el dataset
        dataset.addAll(newContacts)  // Añadir los nuevos contactos
        notifyDataSetChanged()  // Notificar los cambios al adapter
    }
}