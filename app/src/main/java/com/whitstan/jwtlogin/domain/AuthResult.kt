package com.whitstan.jwtlogin.domain

sealed class AuthResult(val data: String? = null) {
    class Authorized(data: String? = null): AuthResult(data)
    class Unauthorized: AuthResult()
    class RefreshFailed: AuthResult()
    class NoInternet: AuthResult()
    class UnknownError: AuthResult()
}