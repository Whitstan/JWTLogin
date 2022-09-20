package com.whitstan.jwtlogin.util

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT

object JWTTools {

    fun getFullNameFromJwt(jwtString: String): String?{
        return try { JWT(jwtString).claims["idp:fullname"]?.asString() }
        catch (exception: DecodeException){ return null }
    }

    fun getRoleFromJwt(jwtString: String): String?{
        return try { JWT(jwtString).claims["role"]?.asString() }
        catch (exception: DecodeException){ return null }
    }

}