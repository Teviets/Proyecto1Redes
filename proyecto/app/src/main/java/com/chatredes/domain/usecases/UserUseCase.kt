package com.chatredes.domain.usecases

import android.util.Log
import com.chatredes.data.XMPP.XMPPClient
import org.jivesoftware.smack.packet.Presence
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

    suspend fun registerAccount(username: String, password: String) {
        try {
            repo.registerAccount(username, password)
        } catch (e: Exception) {
            Log.e("UserUseCase", "Error registering account: ${e.message}")
            throw e
        }
    }

    fun deleteAccount() {
        repo.deleteAccount()
    }

    fun logout() {
        repo.disconnect()
    }

    fun changeDisponibility(status: Presence.Mode) {
        try {
            repo.changeDisponibility(status)
        }catch (e: Exception){
            throw e
        }
    }
}