package com.chatredes.domain.models

import org.jivesoftware.smack.roster.RosterEntry

data class Contact(
    val username: String,
    val name: String,
    val status: String
)

fun RosterEntry.toContact(status: String): Contact {
    return Contact(
        username = this.jid.toString(),
        name = this.name ?: this.jid.toString(),
        status = status
    )
}
