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
        viewModelScope.launch {
            userUseCase.registerAccount(username, password)

        }

    }

    fun deleteAccount() {
        userUseCase.deleteAccount()
    }

    fun logout() {
        userUseCase.logout()
    }

    fun changeDisponibility(status: String) {
        userUseCase.changeDisponibility(status)
    }
}