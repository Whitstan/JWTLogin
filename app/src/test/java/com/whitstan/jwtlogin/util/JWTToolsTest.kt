package com.whitstan.jwtlogin.util

import org.junit.Test

class JWTToolsTest {

    @Test
    fun `correct JWT token decoding`(){
        val jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZHA6dXNlcl9pZCI6IjUwYTdkYTFkLWZlMDctNGMxNC04YjFiLTAwNzczN2Y0Nzc2MyIsImlkcDp1c2VyX25hbWUiOiJqZG9lIiwiaWRwOmZ1bGxuYW1lIjoiSm9obiBEb2UiLCJyb2xlIjoiZWRpdG9yIiwiZXhwIjoxNTU2NDc2MjU1fQ.iqFmotBtfAYLplfpLVh_kPgvOIPyV7UMm-NZA06XA5I"
        val fullName = JWTTools.getFullNameFromJwt(jwt)
        val role = JWTTools.getRoleFromJwt(jwt)
        assert(fullName == "John Doe")
        assert(role == "editor")
    }

    @Test
    fun `incorrect JWT token decoding`(){
        val jwt = "incorrect_jwt"
        val fullName = JWTTools.getFullNameFromJwt(jwt)
        val role = JWTTools.getRoleFromJwt(jwt)
        assert(fullName != "John Doe")
        assert(role != "editor")
    }
}