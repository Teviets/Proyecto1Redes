package com.chatredes.domain.usecases

import com.chatredes.data.XMPP.XMPPClient
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val repo: XMPPClient
)  {


    suspend fun connect() {
        repo.connect()
    }
    suspend fun login(username: String, password: String): Boolean {
        try {
            return repo.login(username, password)
        }catch (e: Exception){
            throw e
        }

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