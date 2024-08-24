package com.chatredes.ui.viewmodel

sealed class StatusApp {
    object Loading : StatusApp()
    object Success : StatusApp()
    object Default : StatusApp()
    data class Error(val message: String) : StatusApp()

}