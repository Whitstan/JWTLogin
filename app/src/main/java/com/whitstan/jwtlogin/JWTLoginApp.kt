package com.whitstan.jwtlogin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

@HiltAndroidApp
class JWTLoginApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startMockServer() // TODO: remove when mock server is not needed anymore
    }

    private fun startMockServer(){
        val mockWebServer = MockWebServer()

        val mockJson = """{"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZHA6dXNlcl9pZCI6IjUwYTdkYTFkLWZlMDctNGMxNC04YjFiLTAwNzczN2Y0Nzc2MyIsImlkcDp1c2VyX25hbWUiOiJqZG9lIiwiaWRwOmZ1bGxuYW1lIjoiSm9obiBEb2UiLCJyb2xlIjoiZWRpdG9yIiwiZXhwIjoxNTU2NDc2MjU1fQ.iqFmotBtfAYLplfpLVh_kPgvOIPyV7UMm-NZA06XA5I","token_type": "bearer","expires_in": 119,"refresh_token":"NTBhN2RhMWQtZmUwNy00YzE0LThiMWItMDA3NzM3ZjQ3NzYzIyNkNmQ5OTViZS1jY2IxLTQ0MGUtODM4NS1lOTkwMTEwMzBhYzA="}"""

        CoroutineScope(Dispatchers.IO).launch {

            mockWebServer.dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBodyDelay(3000, TimeUnit.MILLISECONDS)
                        .setBody(mockJson)
                }
            }

            mockWebServer.start(8080)
        }
    }
}