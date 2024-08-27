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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private val dataset: MutableList<Message>
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val layoutMessage: RelativeLayout = view.findViewById(R.id.chat_item)
        private val tvSender: TextView = view.findViewById(R.id.username)
        private val tvMessage: TextView = view.findViewById(R.id.textchat)
        private val tvTime: TextView = view.findViewById(R.id.message_time)

        fun setData(message: Message) {
            tvSender.text = message.sender.toString()
            tvMessage.text = message.message.toString()
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val currentTime = dateFormat.format(Date())
            tvTime.text = currentTime

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

    fun updateMessages(newMessages: List<Message>) {
        dataset.clear()
        dataset.addAll(newMessages)
        notifyDataSetChanged()
    }
}