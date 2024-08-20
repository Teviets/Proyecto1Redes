package com.chatredes.domain.usecases

import com.chatredes.data.XMPP.XMPPClient
import com.chatredes.domain.models.Contact
import javax.inject.Inject

class ContactsUseCase @Inject constructor(
    val repo: XMPPClient
) {


    fun getContacts(): List<Contact> {
        return repo.getContacts()
    }

    fun addContact(contact: String) {
        repo.addContact(contact)
    }
}