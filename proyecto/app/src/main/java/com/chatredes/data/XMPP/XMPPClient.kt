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
                if (connection == null) {
                    connection = XMPPTCPConnection(config)
                }

                connection!!.connect()

                if (connection!!.isConnected) {
                    println("Connection to server successful")
                    connection!!.login(username, password)
                    println("Logged in as: $username")
                    setupMessageListener()
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

    fun changeDisponibility(status: String) {
        val presence = Presence(Presence.Type.available)
        presence.status = status
        connection?.sendStanza(presence)
    }

    fun disconnect() {
        println("Disconnecting from XMPP server")
        connection?.disconnect()
        connection = null
        println("Disconnected from XMPP server")
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

    private fun setupMessageListener() {
        val chatManager = ChatManager.getInstanceFor(connection)
        chatManager.addIncomingListener { from, message, chat ->
            println("Received message from: $from - Message: ${message.body}")
            receivedMessages.add(message.toMessage())
        }
    }

    fun getMessages(): List<Message> {
        return receivedMessages.toList()
    }
}
