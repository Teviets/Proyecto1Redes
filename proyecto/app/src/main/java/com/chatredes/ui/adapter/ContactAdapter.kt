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
    private val dataset : List<Contact>,
    private val listener: RecyclerViewContactEvents
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(){

    class ViewHolder(
        val view: View,
        val listener: RecyclerViewContactEvents
    ): RecyclerView.ViewHolder(view){

        private val layoutContact: ConstraintLayout = view.findViewById(R.id.contactscard)
        private val tvContact: TextView = view.findViewById(R.id.usernameContact)
        private val tvStatus: TextView = view.findViewById(R.id.disponibilityContact)

        fun setData(contact: Contact){
            tvContact.text = contact.username
            tvStatus.text = contact.status
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
}