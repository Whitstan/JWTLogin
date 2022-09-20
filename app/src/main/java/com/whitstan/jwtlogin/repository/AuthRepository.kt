package com.whitstan.jwtlogin.repository

import com.whitstan.jwtlogin.domain.AuthResult

interface AuthRepository {
    suspend fun login(username: String, password: String): AuthResult
    suspend fun refreshCredentials(refreshToken: String): AuthResult
}