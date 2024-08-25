package com.chatredes.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatredes.data.XMPP.MessageListener
import com.chatredes.data.constantes.SessionManager
import com.chatredes.domain.models.Message
import com.chatredes.domain.usecases.MessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val messageUseCase: MessageUseCase
) :ViewModel(), MessageListener{

    private val _status = MutableLiveData<StatusApp>()
    val status get() = _status

    private val manager: SessionManager = SessionManager(context)

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            messageUseCase.getMessagesFlow().collect { newMessages ->
                _messages.value = newMessages
            }
        }
    }

    fun sendMessage(message: String, to: String) {
        _status.value = StatusApp.Loading
        viewModelScope.launch {
            try {
                val fromJID = manager.getUserDetails()["JID"]
                if (fromJID != null) {
                    messageUseCase.sendMessage(fromJID, message, to)
                    _status.value = StatusApp.Success
                } else {
                    _status.value = StatusApp.Error("JID del usuario no encontrado")
                }
            } catch (e: Exception) {
                Log.e("Error de message", e.toString())
                _status.value = StatusApp.Error("Error al enviar mensaje: ${e.message}")
            }
        }
    }

    fun getMessages() {
        _status.value = StatusApp.Loading
        viewModelScope.launch {
            try {
                val messages = messageUseCase.getMessages()
                if (messages.isNotEmpty()){
                    _status.value = StatusApp.Default
                }else{
                    _status.value = StatusApp.Error("No hay mensajes")
                }
            }catch (e: Exception){
                Log.e("Error de message", e.toString())
                _status.value = StatusApp.Error("Error al obtener los mensajes")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageUseCase.removeListener(this)
    }


    override fun onNewMessage(message: Message) {
        _messages.value = _messages.value?.plus(message) ?: listOf(message)
    }

    override fun onMessagesUpdated(messages: List<Message>) {
        _messages.value = messages
    }

    fun loadMessages(){
        viewModelScope.launch {
            try {
                val messages = messageUseCase.getMessages()
                if (messages.isNotEmpty()){
                    _messages.value = messages
                }else{
                    _messages.value = emptyList()
                }
            }catch (e: Exception){
                _status.value = StatusApp.Error("Error al obtener los mensajes")
            }
        }
    }
}