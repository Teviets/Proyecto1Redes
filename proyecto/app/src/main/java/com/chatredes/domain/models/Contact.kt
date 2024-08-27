package com.chatredes.domain.models

import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.RosterEntry

data class Contact(
    val username: String,
    val name: String,
    val status: String,
    val statusMessage: String = ""
)

fun RosterEntry.toContact(statusMsg: String = "", status: Presence.Mode): Contact {
    val st = when (status){
        Presence.Mode.chat -> "Disponible"
        Presence.Mode.available -> "Disponible"
        Presence.Mode.away -> "Ausente"
        Presence.Mode.xa -> "Ocupado"
        Presence.Mode.dnd -> "No molestar"
    }
    return Contact(
        username = this.jid.toString(),
        name = this.name ?: this.jid.toString(),
        status = st,
        statusMessage = statusMsg
    )
}
