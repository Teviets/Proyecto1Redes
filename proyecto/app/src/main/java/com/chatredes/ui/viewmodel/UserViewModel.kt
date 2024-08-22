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


    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLogged.value = userUseCase.login(username, password)
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