package com.chatredes.data.XMPP

import com.chatredes.domain.models.Message

interface MessageListener {

    fun onNewMessage(message: Message)
    fun onMessagesUpdated(messages: List<Message>)
}