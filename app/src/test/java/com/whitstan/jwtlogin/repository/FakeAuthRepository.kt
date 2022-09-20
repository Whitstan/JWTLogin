package com.whitstan.jwtlogin.repository

import com.whitstan.jwtlogin.domain.AuthResult
import com.whitstan.jwtlogin.network.AuthApi
import retrofit2.HttpException
import java.net.UnknownHostException

class FakeAuthRepository(private val api: AuthApi): AuthRepository {

    private val clientId = "69bfdce9-2c9f-4a12-aa7b-4fe15e1228dc"
    private val passwordGrantType = "password"
    private val refreshTokenGrantType = "refresh_token"

    override suspend fun login(username: String, password: String): AuthResult {
        return try {
            val response = api.login(passwordGrantType, clientId, username, password)
            AuthResult.Authorized(response.access_token)
        }
        catch (exception: HttpException) {
            if (exception.code() == 401) AuthResult.Unauthorized()
            else {
                AuthResult.UnknownError()
            }
        }
        catch (exception: UnknownHostException) {
            AuthResult.NoInternet()
        }
        catch (exception : Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun refreshCredentials(refreshToken: String): AuthResult {
        return try {
            val response = api.refreshCredentials(refreshTokenGrantType, clientId, refreshToken)
            AuthResult.Authorized(response.access_token)
        }
        catch (e: HttpException) {
            if (e.code() == 401) AuthResult.RefreshFailed()
            else AuthResult.UnknownError()
        }
        catch (exception: UnknownHostException) {
            AuthResult.NoInternet()
        }
        catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

}