package com.chatredes.domain.usecases

import android.util.Log
import com.chatredes.data.XMPP.MessageListener
import com.chatredes.data.XMPP.XMPPClient
import com.chatredes.domain.models.Message
import javax.inject.Inject

class MessageUseCase @Inject constructor(
    private val repo: XMPPClient
) {

    private val listeners = mutableListOf<MessageListener>()

    init {
        repo.addListener(object: MessageListener {
            override fun onNewMessage(message: Message) {
                Log.d("MessageUseCase", "New message received: $message")
                listeners.forEach { it.onNewMessage(message) }
            }

            override fun onMessagesUpdated(messages: List<Message>) {
                Log.d("MessageUseCase", "Messages updated: $messages")
                listeners.forEach { it.onMessagesUpdated(messages) }
            }
        })
    }

    suspend fun sendMessage(sender: String, message: String, to: String) {
        val message = Message(
            sender = sender,
            receiver = to,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        repo.sendMessage(message)
    }

    suspend fun getMessages(): List<Message> {
        return repo.getMessages()
    }

    fun addListener(listener: MessageListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: MessageListener) {
        listeners.remove(listener)
    }

    private fun notifyNewMessage(message: Message) {
        listeners.forEach { it.onNewMessage(message) }
    }

    private fun notifyMessagesUpdated(messages: List<Message>) {
        listeners.forEach { it.onMessagesUpdated(messages) }
    }

    fun initListener(){
        repo.setupListenerOnStart()
    }
}