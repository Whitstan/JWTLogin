package com.whitstan.jwtlogin.domain

data class AuthResponse(
    val access_token : String,
    val token_type : String,
    val expires_in : Long,
    val refresh_token: String
)