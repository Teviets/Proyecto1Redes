package com.chatredes.data.XMPP

import com.chatredes.domain.models.Contact
import com.chatredes.domain.models.Message
import com.chatredes.domain.models.toContact
import com.chatredes.domain.models.toMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.SmackConfiguration
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jivesoftware.smackx.iqregister.packet.Registration
import org.jivesoftware.smackx.iqregister.packet.Registration.Feature
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterEntry
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Localpart


class XMPPClient (
    private val server: String
) {

    private var connection: XMPPTCPConnection? = null
    private var config: XMPPTCPConnectionConfiguration? = null
    private val receivedMessages = mutableListOf<Message>()

    fun connect() {

        config = XMPPTCPConnectionConfiguration.builder()
            .setXmppDomain(server)
            .setHost(server)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .setCompressionEnabled(false)
            .setSendPresence(true)
            .build()

        println("Connected to XMPP server")

    }

    suspend fun login(username: String, password: String) : Boolean{
        try {
            return withContext(Dispatchers.IO){
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
            println("El error aqui apareceeeee")
            e.printStackTrace()
            return false
        }

    }



    fun changeDisponibility(status: String) {
        val presence = Presence(Presence.Type.available)
        presence.status = status
        connection!!.sendStanza(presence)
    }

    fun disconnect() {
        println("Disconnecting from XMPP server")
        connection!!.disconnect()
        println("Disconnected from XMPP server")
    }

    fun sendMessage(message: Message) {
        println("Sending message: $message")

        val message = connection!!.stanzaFactory
            .buildMessageStanza()
            .to(message.receiver)
            .from(message.sender)
            .setBody(message.message)
            .build()

        connection!!.sendStanza(message)

        println("Message sent")
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
            // Configurar el AccountManager con la conexión
            val accountManager = AccountManager.getInstance(connection)
            accountManager.sensitiveOperationOverInsecureConnection(true)

            // Crear un mapa con los atributos de registro
            val attributes = HashMap<String, String>()
            attributes["username"] = username
            attributes["password"] = password

            // Crear el Localpart para el nombre de usuario
            val localpart = Localpart.from(username)

            // Registrar la cuenta en el servidor
            accountManager.createAccount(localpart, password, attributes)
            println("Account registered successfully: $username")

        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to register account: ${e.message}")
        }
    }

    fun addContact(jid: String) {
        try {
            val roster = Roster.getInstanceFor(connection)
            roster.subscriptionMode = Roster.SubscriptionMode.accept_all

            // Verificar si el contacto ya existe
            val entry = roster.getEntry(JidCreate.bareFrom(jid))
            if (entry == null) {
                // Agregar contacto al roster con el mismo JID como nombre
                roster.createEntry(JidCreate.bareFrom(jid), jid, null)
                println("Contact added: $jid")
            } else {
                println("Contact already exists: $jid")
            }
        } catch (e: SmackException) {
            e.printStackTrace()
            println("Failed to add contact: ${e.message}")
        }
    }

    fun getContacts(): List<Contact> {
        val roster = Roster.getInstanceFor(connection)
        val contacts = mutableListOf<Contact>()

        for (entry in roster.entries) {
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