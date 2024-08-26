package com.chatredes.data.XMPP

import android.content.Context
import android.util.Log
import com.chatredes.domain.models.toMessage
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smackx.delay.packet.DelayInformation
import org.pgpainless.key.selection.keyring.impl.XMPP

class MessageHandler : StanzaListener {

    override fun processStanza(stanza: Stanza) {
        Log.d("MessageHandler", "Processing stanza: ${stanza.toXML()}")
        try {
            if (stanza is org.jivesoftware.smack.packet.Message) {
                val message = stanza.toMessage()

                if (message.message.isEmpty()) {
                    Log.d("MessageHandler", "Empty message received, ignoring.")
                    return
                }

                val messageItem = com.chatredes.domain.models.Message(
                    sender = message.sender,
                    receiver = message.receiver,
                    message = message.message,
                    timestamp = message.timestamp
                )

                val isNewMessage = stanza.extensions.none { it is org.jivesoftware.smackx.delay.packet.DelayInformation }

                if (isNewMessage) {
                    Log.d("MessageHandler", "New message received: $messageItem")
                } else {
                    Log.d("MessageHandler", "Delayed message received, processing as necessary.")
                }
            } else {
                Log.d("MessageHandler", "Stanza received is not a message: ${stanza.toXML()}")
            }
        } catch (e: Exception) {
            Log.e("MessageHandler", "Error processing stanza", e)
        } finally {
            Log.d("MessageHandler END", "Continuing message listening...")
        }
    }
}