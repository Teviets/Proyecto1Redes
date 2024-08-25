package com.chatredes.domain.models

import org.jxmpp.jid.impl.JidCreate

data class Message(
    val sender: String,
    val receiver: String,
    val message: String,
    val timestamp: Long
){
    override fun toString(): String {
        return "$message"
    }
}


fun Message.toSmackMessage(): org.jivesoftware.smack.packet.Message {
    val smackMessage = org.jivesoftware.smack.packet.Message()
    smackMessage.body = this.message
    smackMessage.to = JidCreate.bareFrom(this.receiver)
    smackMessage.from = JidCreate.bareFrom(this.sender)
    smackMessage.stanzaId = this.timestamp.toString()
    return smackMessage
}

fun org.jivesoftware.smack.packet.Message.toMessage(): Message {
    return Message(
        sender = this.from.asBareJid().toString(),
        receiver = this.to.asBareJid().toString(),
        message = this.body ?: "",
        timestamp = System.currentTimeMillis()
    )
}
