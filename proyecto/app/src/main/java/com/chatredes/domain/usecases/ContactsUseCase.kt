package com.chatredes.domain.usecases

import com.chatredes.data.XMPP.XMPPClient
import com.chatredes.domain.models.Contact
import javax.inject.Inject

class ContactsUseCase @Inject constructor(
    val repo: XMPPClient
) {


    suspend fun getContacts(): List<Contact> {
        try{
            return repo.getContacts()
        }catch (e: Exception){
            throw e
        }

    }

    suspend fun addContact(contact: String, name: String) {
        try{
            repo.addContact(contact, name)
        }catch (e: Exception){
            throw e
        }
    }
}