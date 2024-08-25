package com.chatredes.domain.usecases

import com.chatredes.data.XMPP.MessageListener
import com.chatredes.data.XMPP.XMPPClient
import com.chatredes.domain.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MessageUseCase @Inject constructor(
    private val repo: XMPPClient
) {
    private val _messagesFlow = MutableStateFlow<List<Message>>(emptyList())

    init {
        repo.addListener(object : MessageListener {
            override fun onNewMessage(message: Message) {
                _messagesFlow.value = _messagesFlow.value + message
            }

            override fun onMessagesUpdated(messages: List<Message>) {
                _messagesFlow.value = messages
            }
        })
    }

    fun getMessagesFlow(): Flow<List<Message>> = _messagesFlow.asStateFlow()

    suspend fun sendMessage(sender: String, message: String, to: String) {
        val newMessage = Message(
            sender = sender,
            receiver = to,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        repo.sendMessage(newMessage)
        notifyNewMessage(newMessage)
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
}