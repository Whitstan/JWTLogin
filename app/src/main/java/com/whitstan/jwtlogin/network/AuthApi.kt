package com.whitstan.jwtlogin.network

import com.whitstan.jwtlogin.domain.AuthResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("/idp/api/v1/token")
    suspend fun login(
        @Header("grant_type") grantType : String,
        @Header("client_id") clientId : String,
        @Field("username") username: String,
        @Field("password") password: String
    ) : AuthResponse

    @FormUrlEncoded
    @POST("/idp/api/v1/token")
    suspend fun refreshCredentials(
        @Header("grant_type") grantType : String,
        @Header("client_id") clientId : String,
        @Field("refresh_token") refreshToken: String
    ) : AuthResponse
}