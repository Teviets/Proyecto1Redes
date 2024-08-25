package com.chatredes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.chatredes.R
import com.chatredes.domain.models.Message

class MessageAdapter(
    private val dataset: List<Message>
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val layoutMessage: RelativeLayout = view.findViewById(R.id.chat_item)
        private val tvSender: TextView = view.findViewById(R.id.username)
        private val tvMessage: TextView = view.findViewById(R.id.textchat)

        fun setData(message: Message) {
            tvSender.text = message.sender
            tvMessage.text = message.message

            // Para depuración
            println("DEBUG: Setting data for message: Sender=${message.sender}, Message=${message.message}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = dataset[position]
        holder.setData(message)

        // Para depuración
        println("DEBUG: Binding view holder for position $position: Sender=${message.sender}, Message=${message.message}")
    }

    override fun getItemCount(): Int = dataset.size
}