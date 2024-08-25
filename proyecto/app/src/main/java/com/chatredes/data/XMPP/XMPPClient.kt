package com.chatredes.data.XMPP

import android.util.Log
import com.chatredes.domain.models.Contact
import com.chatredes.domain.models.Message
import com.chatredes.domain.models.toContact
import com.chatredes.domain.models.toMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.filter.StanzaTypeFilter
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smackx.ping.PingManager
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Localpart

class XMPPClient private constructor(private val server: String) {

    private var connection: XMPPTCPConnection? = null
    private var config: XMPPTCPConnectionConfiguration? = null
    private val receivedMessages = mutableListOf<Message>()
    private val listeners = mutableListOf<MessageListener>()
    private var stanzaListener: StanzaListener? = null

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
        }

        println("Configured to connect to XMPP server")
    }

    suspend fun login(username: String, password: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {

                connection?.disconnect()

                connection = XMPPTCPConnection(config)

                connection!!.connect()

                if (connection!!.isConnected) {
                    println("Connection to server successful")
                    connection!!.login(username, password)
                    println("Logged in as: $username")
                    true
                } else {
                    println("Failed to connect to server")
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun setupReconnectionManager() {
        val reconnectionManager = ReconnectionManager.getInstanceFor(connection)
        reconnectionManager.enableAutomaticReconnection()
        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY)
    }


    fun changeDisponibility(status: String) {
        val presence = Presence(Presence.Type.available)
        presence.status = status
        connection?.sendStanza(presence)
    }

    fun disconnect() {
        println("Disconnecting from XMPP server")
        connection?.removeAsyncStanzaListener(stanzaListener)
        connection?.disconnect()
        connection = null
        println("Disconnected from XMPP server")
    }

    fun reconnect() {
        try {
            connection?.disconnect()
            connection?.connect()
            Log.d("XMPPClient", "Reconnected to XMPP server.")
            if (connection?.isAuthenticated == true) {
                setupStanzaListener()
                Log.d("XMPPClient", "Stanza listener reconfigured after reconnection.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("XMPPClient", "Failed to reconnect: ${e.message}")
        }
    }

    fun sendMessage(message: Message) {

        if (connection?.isConnected != true || connection?.isAuthenticated != true) {
            Log.d("XMPPClient", "Connection is not active, not sending message.")
            reconnect()
            return
        }

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
            notifyMessageSent(message)
            println("Message sent: $message")
        }
    }


    fun deleteAccount() {
        try {
            val accountManager = AccountManager.getInstance(connection)
            accountManager.deleteAccount()
            println("Account deleted")
        } catch (e: SmackException) {
            e.printStackTrace()
            println("Failed to delete account: ${e.message}")
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
            println("Account registered successfully: $username")

        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to register account: ${e.message}")
        }
    }

    fun addContact(jid: String, name: String) {
        try {
            val roster = Roster.getInstanceFor(connection)
            roster.subscriptionMode = Roster.SubscriptionMode.accept_all

            val entry = roster.getEntry(JidCreate.bareFrom(jid))
            if (entry == null) {
                roster.createEntry(JidCreate.bareFrom(jid), name, null)
                println("Contact added: $jid")
            } else {
                println("Contact already exists: $jid")
            }
        } catch (e: SmackException) {
            e.printStackTrace()
            println("Failed to add contact: ${e.message}")
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

        return contacts
    }

    fun setPresenceActive(){
        val presence = Presence(Presence.Type.available)
        connection?.sendStanza(presence)
    }

    fun setupListenerOnStart() {
        // Asegúrate de que la conexión esté establecida y autenticada
        if (connection?.isConnected == true && connection?.isAuthenticated == true) {
            setupStanzaListener() // Configura el listener si la conexión es válida
        } else {
            Log.d("XMPPClient", "Connection is not ready. Cannot set up listener.")
        }
    }


    fun setupStanzaListener() {
        connection?.removeAsyncStanzaListener(stanzaListener)
        Log.d("XMPPClient", "Setting up stanza listener without filter")
        stanzaListener = StanzaListener { packet ->
            if (packet is org.jivesoftware.smack.packet.Message) {
                Log.d("XMPPClient", "Received message: ${packet.body}")
                handleIncomingMessage(packet)
            } else {
                Log.d("XMPPClient", "Received non-message stanza, ignoring.")
            }
        }
        connection?.addAsyncStanzaListener(stanzaListener, StanzaTypeFilter(org.jivesoftware.smack.packet.Message::class.java))
        Log.d("XMPPClient", "Stanza listener set up")
    }

    private fun setUpPingManager() {
        val pingManager = PingManager.getInstanceFor(connection)
        pingManager.pingInterval = 300 // 5 minutos
    }

    private fun handleIncomingMessage(message: org.jivesoftware.smack.packet.Message) {
        val body = message.body
        if (body != null && body.isNotBlank()) {
            val from = message.from.asBareJid().toString()
            Log.d("XMPPClient", "Received message from $from: $body")

            val newMessage = message.toMessage()
            receivedMessages.add(newMessage)
            notifyNewMessage(newMessage)
        } else {
            Log.d("XMPPClient", "Received empty or null message, ignoring.")
        }
    }

    fun getMessages(): List<Message> {
        return receivedMessages.toList()
    }

    fun addListener(listener: MessageListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: MessageListener) {
        listeners.remove(listener)
    }

    private fun notifyNewMessage(message: Message) {
        Log.d("XMPPClient", "Notifying listeners of new message: $message")
        listeners.forEach { it.onNewMessage(message) }
    }


    private fun notifyMessageSent(message: Message) {
        listeners.forEach { it.onMessagesUpdated(receivedMessages) }
    }
}