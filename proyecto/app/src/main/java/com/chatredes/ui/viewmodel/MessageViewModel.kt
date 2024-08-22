package com.chatredes.ui.viewmodel

import android.content.Context
import com.chatredes.domain.usecases.MessageUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MessageViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val messageUseCase: MessageUseCase
) {

    fun sendMessage(user: String, sender: String, message: String, to: String) {
        messageUseCase.sendMessage(user, sender, message, to)
    }

    fun getMessages() {
        messageUseCase.getMessages()
    }
}