package com.chatredes.domain.usecases

import com.chatredes.data.XMPP.XMPPClient
import com.chatredes.domain.models.Message
import javax.inject.Inject

class MessageUseCase @Inject constructor(
    private val repo: XMPPClient
) {

        fun sendMessage(user: String, sender: String, message: String, to: String) {
            val message = Message(
                sender = sender,
                receiver = to,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            repo.sendMessage(message)
        }

        fun getMessages(): List<Message> {
            return repo.getMessages()
        }
}