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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val messageUseCase: MessageUseCase
) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages get() = _messages

    private val _status = MutableLiveData<StatusApp>()
    val status get() = _status

    private val manager: SessionManager = SessionManager(context)

    init {
        viewModelScope.launch {
            _messages.value = messageUseCase.getMessages()
        }

        messageUseCase.addListener(object : MessageListener {
            override fun onNewMessage(message: Message) {
                Log.d("MessageViewModel", "New message received: $message")
                val updatedMessages = _messages.value.orEmpty() + message
                _messages.postValue(updatedMessages)
            }

            override fun onMessagesUpdated(messages: List<Message>) {
                Log.d("MessageViewModel", "Messages updated: $messages")
                _messages.postValue(messages)
            }
        })
    }

    fun sendMessage(message: String, to: String) {
        _status.value = StatusApp.Loading
        viewModelScope.launch {
            try {
                messageUseCase.sendMessage(manager.getUserDetails()["username"]!!, message, to)
                _status.value = StatusApp.Success
            } catch (e: Exception) {
                Log.e("MessageViewModel", "Error sending message", e)
                _status.value = StatusApp.Error("Error al enviar mensaje")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageUseCase.clearListeners() // Limpia los listeners al destruir el ViewModel
    }
}