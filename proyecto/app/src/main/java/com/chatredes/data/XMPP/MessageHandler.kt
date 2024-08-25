package com.chatredes.data.XMPP

import android.content.Context
import android.util.Log
import com.chatredes.domain.models.toMessage
import org.jivesoftware.smack.StanzaListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smackx.delay.packet.DelayInformation
import org.pgpainless.key.selection.keyring.impl.XMPP

class MessageHandler () : StanzaListener {


    fun isAppRunning(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        for (processInfo in runningAppProcesses) {
            if (processInfo.processName == context.packageName) {
                if (processInfo.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (d in processInfo.pkgList) {
                        if (d == context.packageName) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    override fun processStanza(stanza: Stanza) {
        Log.d("MessageHandler", "Procesando stanza: ${stanza.toXML()}")
        try {
            if (stanza is Message) {
                val message = stanza.toMessage()

                if (message.message.isEmpty()) {
                    Log.d("MessageHandler", "Mensaje vacío recibido, ignorando")
                    return
                }

                // Procesar mensaje no vacío
                Log.d("MessageHandler", "Procesando mensaje: ${message.message}")

                val messageItem = com.chatredes.domain.models.Message(
                    sender = message.sender,
                    receiver = message.receiver,
                    message = message.message,
                    timestamp = message.timestamp
                )

                val isNewMessage = stanza.extensions.none { it is DelayInformation }

                if (isNewMessage) {
                    Log.d("MessageHandler", "Nuevo mensaje recibido: $messageItem")
                    // Procesar nuevo mensaje aquí
                } else {
                    Log.d("MessageHandler", "Mensaje retrasado recibido, considerando si procesar")
                    // Procesar mensajes retrasados si es necesario
                }
            } else {
                Log.d("MessageHandler", "Stanza recibida no es un mensaje: ${stanza.toXML()}")
            }
        } catch (e: NullPointerException) {
            Log.e("MessageHandler", "Null Pointer Exception al procesar el mensaje: ${e.message}")
        } catch (e: IllegalArgumentException) {
            Log.e("MessageHandler", "Illegal Argument Exception: ${e.message}")
        } catch (e: Exception) {
            Log.e("MessageHandler", "Error desconocido al procesar el stanza", e)
        } finally {
            Log.d("MessageHandler END", "Continuando la escucha de mensajes...")
        }
    }
}