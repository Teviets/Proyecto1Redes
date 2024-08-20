package com.chatredes.domain.usecases

import com.chatredes.data.XMPP.XMPPClient
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val repo: XMPPClient
)  {

    fun login(username: String, password: String) {
        repo.login(username, password)
    }

    fun registerAccount(username: String, password: String) {
        repo.registerAccount(username, password)
    }

    fun deleteAccount() {
        repo.deleteAccount()
    }

    fun logout() {
        repo.disconnect()
    }

    fun changeDisponibility(status: String) {
        repo.changeDisponibility(status)
    }
}