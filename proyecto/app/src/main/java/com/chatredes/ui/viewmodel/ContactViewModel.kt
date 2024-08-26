package com.chatredes.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatredes.data.constantes.SessionManager
import com.chatredes.domain.models.Contact
import com.chatredes.domain.usecases.ContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val contactsUseCase: ContactsUseCase
) : ViewModel(){

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts get() = _contacts

    private val _status = MutableLiveData<StatusApp>()
    val status get() = _status

    private val manager: SessionManager = SessionManager(context)

    fun getContacts() {
        _status.value = StatusApp.Loading
        viewModelScope.launch {
            try {
                val contacts = contactsUseCase.getContacts()
                if (contacts.isNotEmpty()){
                    _contacts.value = contacts
                    _status.value = StatusApp.Default
                }else{
                    _status.value = StatusApp.Error("No hay contactos")
                }
            }catch (e: Exception){
                e.printStackTrace()
                Log.e("Error de get contacts", e.toString())
                _status.value = StatusApp.Error("Error al obtener los contactos")
            }
        }
    }


    fun addContact(contact: String, name: String) {
        _status.value = StatusApp.Loading
        viewModelScope.launch {
            try {
                contactsUseCase.addContact(contact, name)
                getContacts()
                _status.value = StatusApp.Success
            }catch (e: Exception){
                Log.e("Error de add contact", e.toString())
                _status.value = StatusApp.Error("Error al agregar contacto")
            }
        }

    }

}