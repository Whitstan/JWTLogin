package com.whitstan.jwtlogin.ui

sealed class AuthUiEvent {
    data class LoginUsernameChanged(val value: String): AuthUiEvent()
    data class LoginPasswordChanged(val value: String): AuthUiEvent()
    object Login: AuthUiEvent()
}