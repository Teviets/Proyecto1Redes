package com.chatredes.ui.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _isLogged = MutableLiveData<Boolean>()
    val isLogged get() = _isLogged

    private val _message = MutableLiveData<String>()
    val message get() = _message


    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _isLogged.value = userUseCase.login(username, password)
                if (_isLogged.value == true){
                    _message.value = "Inicio de sesión exitoso"
                }else{
                    _message.value = "Inicio de sesión fallido"
                }
            }catch (e: Exception){
                _isLogged.value = false
                _message.value = e.message
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