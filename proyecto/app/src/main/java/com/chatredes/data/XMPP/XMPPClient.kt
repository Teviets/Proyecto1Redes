package com.chatredes.data.XMPP

import android.util.Log
import com.chatredes.domain.models.Contact
import com.chatredes.domain.models.Message
import com.chatredes.domain.models.toContact
import com.chatredes.domain.models.toMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.ReconnectionListener
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
    private var messageHandler: MessageHandler? = null

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
        // Disconnect any previous connection before establishing a new one
        disconnect()

        if (config == null) {
            config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(server)
                .setHost(server)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setCompressionEnabled(false)
                .setSendPresence(true)
                .build()
        }

        connection = XMPPTCPConnection(config)
        try {
            connection?.connect()
            Log.d("XMPPClient", "Connected to XMPP server")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun login(username: String, password: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                // Ensure connection is fresh
                disconnect()
                connection = XMPPTCPConnection(config)

                connection!!.connect()

                if (connection!!.isConnected) {
                    connection!!.login(username, password)
                    Log.d("XMPPClient", "Logged in as: $username")
                    setUpPingManager()
                    true
                } else {
                    Log.d("XMPPClient", "Failed to connect to server")
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun setupMessageListener() {
        messageHandler = MessageHandler()
        connection?.addStanzaListener(messageHandler, StanzaTypeFilter(org.jivesoftware.smack.packet.Message::class.java))
    }

    private fun setupReconnectionManager() {
        val reconnectionManager = ReconnectionManager.getInstanceFor(connection)
        reconnectionManager.enableAutomaticReconnection()
        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY)
    }



    fun disconnect() {
        if (connection?.isConnected == true) {
            Log.d("XMPPClient", "Disconnecting from XMPP server")
            connection?.removeAsyncStanzaListener(messageHandler)
            connection?.disconnect()
            Log.d("XMPPClient", "Disconnected from XMPP server")
        }
        connection = null
    }


    fun changeDisponibility(mode: Presence.Mode) {
        val presence = Presence(Presence.Type.available)
        presence.mode = mode
        connection?.sendStanza(presence)
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
            receivedMessages.add(message)
            notifyMessageSent(message)
            Log.d("XMPPClient", "Message sent: $message")
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

    suspend fun registerAccount(username: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                // Asegúrate de que no estás autenticado
                if (connection?.isAuthenticated == true) {
                    connection?.disconnect()
                }

                // Reconecta si es necesario
                if (connection?.isConnected != true) {
                    connect()
                }

                val accountManager = AccountManager.getInstance(connection)
                accountManager.sensitiveOperationOverInsecureConnection(true)

                val localpart = Localpart.from(username)
                accountManager.createAccount(localpart, password)
                Log.d("XMPPClient", "Account registered successfully: $username")
            } catch (e: Exception) {
                Log.e("XMPPClient", "Failed to register account: ${e.message}")
                throw e
            }
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
        try {
            val roster = Roster.getInstanceFor(connection)
            val contacts = mutableListOf<Contact>()

            roster.entries.forEach { entry ->
                val presence: Presence = roster.getPresence(entry.jid)
                val status = presence.status ?: "Unavailable"
                contacts.add(entry.toContact(status))
                Log.d("XMPPClient", "Contact added: $entry")
            }

            return contacts
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("XMPPClient", "Failed to get contacts: ${e.message}")
            throw e
        }

    }

    fun setPresenceActive(){
        val presence = Presence(Presence.Type.available)
        connection?.sendStanza(presence)
    }

    private fun setUpPingManager() {
        val pingManager = PingManager.getInstanceFor(connection)
        pingManager.pingInterval = 300 // 5 minutos
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

    private fun notifyMessageSent(message: Message) {
        listeners.forEach { it.onMessagesUpdated(receivedMessages) }
    }

    fun getConnection(): XMPPTCPConnection? {
        return connection
    }
}