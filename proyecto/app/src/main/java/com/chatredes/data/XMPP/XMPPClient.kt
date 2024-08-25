package com.chatredes.data.XMPP

import com.chatredes.domain.models.Contact
import com.chatredes.domain.models.Message
import com.chatredes.domain.models.toContact
import com.chatredes.domain.models.toMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jivesoftware.smack.roster.Roster
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Localpart

class XMPPClient private constructor(private val server: String) {

    private var connection: XMPPTCPConnection? = null
    private var config: XMPPTCPConnectionConfiguration? = null
    private val receivedMessages = mutableListOf<Message>()
    private val listeners = mutableListOf<MessageListener>()

    companion object {
        @Volatile
        private var instance: XMPPClient? = null

        fun getInstance(server: String): XMPPClient {
            return instance ?: synchronized(this) {
                instance ?: XMPPClient(server).also { instance = it }
            }
        }
    }

    fun connect() {
        if (config == null) {
            config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(server)
                .setHost(server)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setCompressionEnabled(false)
                .setSendPresence(true)
                .build()
            println("DEBUG: XMPP configuration created for server: $server")
        }

        println("DEBUG: Configured to connect to XMPP server")
    }

    suspend fun login(username: String, password: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                if (connection == null) {
                    connection = XMPPTCPConnection(config)
                    println("DEBUG: New XMPP connection created")
                }

                connection!!.connect()
                println("DEBUG: Attempting to connect to server")

                if (connection!!.isConnected) {
                    println("DEBUG: Connection to server successful")
                    connection!!.login(username, password)
                    println("DEBUG: Logged in as: $username")
                    setupMessageListener()
                    true
                } else {
                    println("DEBUG: Failed to connect to server")
                    false
                }
            }
        } catch (e: Exception) {
            println("DEBUG: Exception during login: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    fun changeDisponibility(status: String) {
        val presence = Presence(Presence.Type.available)
        presence.status = status
        connection?.sendStanza(presence)
        println("DEBUG: Changed availability status to: $status")
    }

    fun disconnect() {
        println("DEBUG: Disconnecting from XMPP server")
        connection?.disconnect()
        connection = null
        println("DEBUG: Disconnected from XMPP server")
    }

    fun sendMessage(message: Message) {
        val stanza = connection?.stanzaFactory
            ?.buildMessageStanza()
            ?.to(message.receiver)
            ?.from(message.sender)
            ?.setBody(message.message)
            ?.build()

        stanza?.let {
            connection?.sendStanza(it)
            // Add the sent message to the list
            receivedMessages.add(message)
            println("DEBUG: Message sent: $message")
            notifyMessageSent(message)
        } ?: println("DEBUG: Failed to create message stanza")
    }

    fun deleteAccount() {
        try {
            val accountManager = AccountManager.getInstance(connection)
            accountManager.deleteAccount()
            println("DEBUG: Account deleted")
        } catch (e: SmackException) {
            println("DEBUG: Failed to delete account: ${e.message}")
            e.printStackTrace()
        }
    }

    fun registerAccount(username: String, password: String) {
        try {
            val accountManager = AccountManager.getInstance(connection)
            accountManager.sensitiveOperationOverInsecureConnection(true)

            val attributes = HashMap<String, String>().apply {
                put("username", username)
                put("password", password)
            }

            val localpart = Localpart.from(username)
            accountManager.createAccount(localpart, password, attributes)
            println("DEBUG: Account registered successfully: $username")

        } catch (e: Exception) {
            println("DEBUG: Failed to register account: ${e.message}")
            e.printStackTrace()
        }
    }

    fun addContact(jid: String, name: String) {
        try {
            val roster = Roster.getInstanceFor(connection)
            roster.subscriptionMode = Roster.SubscriptionMode.accept_all

            val entry = roster.getEntry(JidCreate.bareFrom(jid))
            if (entry == null) {
                roster.createEntry(JidCreate.bareFrom(jid), name, null)
                println("DEBUG: Contact added: $jid")
            } else {
                println("DEBUG: Contact already exists: $jid")
            }
        } catch (e: SmackException) {
            println("DEBUG: Failed to add contact: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun getContacts(): List<Contact> {
        val roster = Roster.getInstanceFor(connection)
        val contacts = mutableListOf<Contact>()

        roster.entries.forEach { entry ->
            val presence: Presence = roster.getPresence(entry.jid)
            val status = presence.status ?: "Unavailable"
            contacts.add(entry.toContact(status))
        }

        println("DEBUG: Retrieved ${contacts.size} contacts")
        return contacts
    }

    private fun setupMessageListener() {
        val chatManager = ChatManager.getInstanceFor(connection)
        chatManager.addIncomingListener { from, message, chat ->
            val msg = message.toMessage()
            receivedMessages.add(msg)
            println("DEBUG: Incoming message received: $msg")
            notifyNewMessage(msg)
        }
        println("DEBUG: Message listener set up")
    }

    fun getMessages(): List<Message> {
        println("DEBUG: Returning ${receivedMessages.size} messages")
        return receivedMessages.toList()
    }

    fun addListener(listener: MessageListener) {
        listeners.add(listener)
        println("DEBUG: Listener added. Total listeners: ${listeners.size}")
    }

    fun removeListener(listener: MessageListener) {
        listeners.remove(listener)
        println("DEBUG: Listener removed. Total listeners: ${listeners.size}")
    }

    private fun notifyNewMessage(message: Message) {
        println("DEBUG: Notifying ${listeners.size} listeners about new message")
        listeners.forEach { it.onNewMessage(message) }
    }

    private fun notifyMessageSent(message: Message) {
        println("DEBUG: Notifying ${listeners.size} listeners about message sent")
        listeners.forEach { it.onMessagesUpdated(receivedMessages) }
    }
}