package com.whitstan.jwtlogin.ui

import android.annotation.SuppressLint
import com.whitstan.jwtlogin.network.AuthApi
import com.whitstan.jwtlogin.domain.AuthResult
import com.whitstan.jwtlogin.repository.FakeAuthRepository
import com.whitstan.jwtlogin.ui.viewmodel.AuthViewModel
import com.whitstan.jwtlogin.constants.HttpCodes.OK
import com.whitstan.jwtlogin.constants.HttpCodes.UNAUTHORIZED
import com.whitstan.jwtlogin.constants.HttpCodes.UNKNOWN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class AuthViewModelTest {
    private lateinit var viewModel : AuthViewModel
    private val mockWebServer = MockWebServer()

    @OptIn(ExperimentalCoroutinesApi::class)
    val dispatcher = StandardTestDispatcher()

    @SuppressLint("CheckResult")
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp(){
        Dispatchers.setMain(dispatcher)

        CoroutineScope(Dispatchers.IO).launch {
            mockWebServer.start(8080)
        }

        val api : AuthApi = Retrofit.Builder()
            .baseUrl("http://localhost:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()

        viewModel = AuthViewModel(FakeAuthRepository(api)).apply{
            scope = CoroutineScope(Dispatchers.Default)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mockWebServer.shutdown()
    }

    @Test
    fun `correct login`() {
        val mockJson = """{"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZHA6dXNlcl9pZCI6IjUwYTdkYTFkLWZlMDctNGMxNC04YjFiLTAwNzczN2Y0Nzc2MyIsImlkcDp1c2VyX25hbWUiOiJqZG9lIiwiaWRwOmZ1bGxuYW1lIjoiSm9obiBEb2UiLCJyb2xlIjoiZWRpdG9yIiwiZXhwIjoxNTU2NDc2MjU1fQ.iqFmotBtfAYLplfpLVh_kPgvOIPyV7UMm-NZA06XA5I","token_type": "bearer","expires_in": 119,"refresh_token":"NTBhN2RhMWQtZmUwNy00YzE0LThiMWItMDA3NzM3ZjQ3NzYzIyNkNmQ5OTViZS1jY2IxLTQ0MGUtODM4NS1lOTkwMTEwMzBhYzA="}"""

        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setBody(mockJson)
                    .setResponseCode(OK)
            }
        }

        viewModel.onEvent(AuthUiEvent.Login)

        runBlocking {
            val result = viewModel.authResults.first()
            assert(result is AuthResult.Authorized && result.data == "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZHA6dXNlcl9pZCI6IjUwYTdkYTFkLWZlMDctNGMxNC04YjFiLTAwNzczN2Y0Nzc2MyIsImlkcDp1c2VyX25hbWUiOiJqZG9lIiwiaWRwOmZ1bGxuYW1lIjoiSm9obiBEb2UiLCJyb2xlIjoiZWRpdG9yIiwiZXhwIjoxNTU2NDc2MjU1fQ.iqFmotBtfAYLplfpLVh_kPgvOIPyV7UMm-NZA06XA5I")
        }
    }

    @Test
    fun `unauthorized login`() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(UNAUTHORIZED)
            }
        }

        viewModel.onEvent(AuthUiEvent.Login)

        runBlocking {
            assert(viewModel.authResults.first() is AuthResult.Unauthorized)
        }
    }

    @Test
    fun `unknown error during login`() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(UNKNOWN)
            }
        }

        viewModel.onEvent(AuthUiEvent.Login)

        runBlocking {
            assert(viewModel.authResults.first() is AuthResult.UnknownError)
        }
    }

    @Test
    fun `correct refreshing of credentials`() {
        val mockJson = """{"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZHA6dXNlcl9pZCI6IjUwYTdkYTFkLWZlMDctNGMxNC04YjFiLTAwNzczN2Y0Nzc2MyIsImlkcDp1c2VyX25hbWUiOiJqZG9lIiwiaWRwOmZ1bGxuYW1lIjoiSm9obiBEb2UiLCJyb2xlIjoiZWRpdG9yIiwiZXhwIjoxNTU2NDc2MjU1fQ.iqFmotBtfAYLplfpLVh_kPgvOIPyV7UMm-NZA06XA5I","token_type": "bearer","expires_in": 119,"refresh_token":"NTBhN2RhMWQtZmUwNy00YzE0LThiMWItMDA3NzM3ZjQ3NzYzIyNkNmQ5OTViZS1jY2IxLTQ0MGUtODM4NS1lOTkwMTEwMzBhYzA="}"""
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setBody(mockJson)
                    .setResponseCode(OK)
            }
        }

        runBlocking {
            viewModel.callRefreshCredentials("12345678")
            val result = viewModel.authResults.first()
            assert(result is AuthResult.Authorized && result.data == "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZHA6dXNlcl9pZCI6IjUwYTdkYTFkLWZlMDctNGMxNC04YjFiLTAwNzczN2Y0Nzc2MyIsImlkcDp1c2VyX25hbWUiOiJqZG9lIiwiaWRwOmZ1bGxuYW1lIjoiSm9obiBEb2UiLCJyb2xlIjoiZWRpdG9yIiwiZXhwIjoxNTU2NDc2MjU1fQ.iqFmotBtfAYLplfpLVh_kPgvOIPyV7UMm-NZA06XA5I")
        }
    }

    @Test
    fun `failed refreshing of credentials`() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(UNAUTHORIZED)
            }
        }

        runBlocking {
            viewModel.callRefreshCredentials("12345678")
            assert(viewModel.authResults.first() is AuthResult.RefreshFailed)
        }
    }

    @Test
    fun `unknown error at refreshing of credentials`() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(UNKNOWN)
            }
        }

        runBlocking {
            viewModel.callRefreshCredentials("12345678")
            assert(viewModel.authResults.first() is AuthResult.UnknownError)
        }
    }
}