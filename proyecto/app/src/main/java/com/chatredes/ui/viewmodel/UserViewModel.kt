package com.chatredes.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatredes.data.constantes.SessionManager
import com.chatredes.domain.usecases.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.jivesoftware.smack.packet.Presence
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val userUseCase: UserUseCase
): ViewModel() {

    private val _status = MutableLiveData<StatusApp>()
    val status get() = _status

    private val manager: SessionManager = SessionManager(context)

    fun connect() {
        viewModelScope.launch {
            userUseCase.connect()
        }
    }

    fun login(username: String, password: String) {
        _status.value = StatusApp.Loading
        viewModelScope.launch {
            try {

                val isLogged = userUseCase.login(username, password)
                if (isLogged){
                    manager.createLoginSession(username, password, username, true)
                    _status.value = StatusApp.Success
                }else{
                    _status.value = StatusApp.Error("Error al iniciar sesión")
                }
            }catch (e: Exception){
                _status.value = StatusApp.Error("Error al iniciar sesión")
                Log.e("El error de login es: ",e.message.toString())
            }

        }

    }

    fun registerAccount(username: String, password: String) {
        _status.value = StatusApp.Loading
        viewModelScope.launch {
            try {
                userUseCase.registerAccount(username, password)
                Log.d("UserViewModel", "Registration successful for $username")
                _status.value = StatusApp.Success
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error registering account: ${e.message}")
                _status.value = StatusApp.Error("Error al registrar cuenta: ${e.message}")
            }
        }
    }

    fun deleteAccount() {
        _status.value = StatusApp.Loading
        viewModelScope.launch {
            try {
                userUseCase.deleteAccount()
                manager.LogoutUser()
                _status.value = StatusApp.Success
            }catch (e: Exception) {
                _status.value = StatusApp.Error("Error al eliminar cuenta")
                Log.e("El error de delete es: ", e.message.toString())
            }
        }

    }

    fun logout() {
        _status.value = StatusApp.Loading
        viewModelScope.launch {
            try {
                userUseCase.logout()
                manager.LogoutUser()
                _status.value = StatusApp.Success
            }catch (e: Exception) {
                _status.value = StatusApp.Error("Error al cerrar sesión")
                Log.e("El error de logout es: ", e.message.toString())
            }
        }

    }

    fun changeDisponibility(status: Presence.Mode) {
        viewModelScope.launch {
            try {
                userUseCase.changeDisponibility(status)
            }catch (e: Exception){
                Log.e("El error de cambio de disponibilidad es: ", e.message.toString())
            }
        }
    }
}