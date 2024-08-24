package com.chatredes.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatredes.domain.usecases.MessageUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val messageUseCase: MessageUseCase
) :ViewModel(){

    fun sendMessage(user: String, sender: String, message: String, to: String) {
        viewModelScope.launch {
            messageUseCase.sendMessage(user, sender, message, to)
        }

    }

    fun getMessages() {
        messageUseCase.getMessages()
    }
}