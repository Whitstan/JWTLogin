package com.whitstan.jwtlogin.ui

data class AuthState(
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val loginUsername: String = "",
    val loginPassword: String = "",
    val fullName: String? = null,
    val role: String? = null
)