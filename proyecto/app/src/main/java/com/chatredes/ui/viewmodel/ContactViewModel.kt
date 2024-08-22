package com.chatredes.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.chatredes.domain.usecases.ContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val contactsUseCase: ContactsUseCase
) : ViewModel(){

    fun getContacts() {
        contactsUseCase.getContacts()
    }

    fun addContact(contact: String) {
        contactsUseCase.addContact(contact)
    }

}