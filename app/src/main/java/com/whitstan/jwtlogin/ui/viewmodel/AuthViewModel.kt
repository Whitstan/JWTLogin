package com.whitstan.jwtlogin.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitstan.jwtlogin.repository.AuthRepository
import com.whitstan.jwtlogin.domain.AuthResult
import com.whitstan.jwtlogin.ui.AuthState
import com.whitstan.jwtlogin.ui.AuthUiEvent
import com.whitstan.jwtlogin.util.JWTTools
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    var state by mutableStateOf(AuthState())

    var scope: CoroutineScope = viewModelScope

    private val resultChannel = Channel<AuthResult>()
    val authResults = resultChannel.receiveAsFlow()

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.LoginUsernameChanged -> state = state.copy(loginUsername = event.value)
            is AuthUiEvent.LoginPasswordChanged -> state = state.copy(loginPassword = event.value)
            is AuthUiEvent.Login -> callLogin()
        }
    }

    private fun callLogin() {
        scope.launch {
            showLoader()
            val result = repository.login(state.loginUsername,state.loginPassword)
            if (!result.data.isNullOrBlank()) updateUserDataByJwt(result.data)
            resultChannel.send(result)
            if (result !is AuthResult.Authorized) hideLoader()
        }
    }

    fun callRefreshCredentials(refreshToken: String) {
        scope.launch {
            showLoader()
            val result = repository.refreshCredentials(refreshToken)
            if (!result.data.isNullOrBlank()) updateUserDataByJwt(result.data)
            resultChannel.send(result)
            hideLoader()
        }
    }

    private fun showLoader(){
        state = state.copy(isLoading = true)
    }

    private fun hideLoader(){
        state = state.copy(isLoading = false)
    }

    private fun updateUserDataByJwt(jwt: String){
        state = state.copy(
            fullName = JWTTools.getFullNameFromJwt(jwt),
            role = JWTTools.getRoleFromJwt(jwt)
        )
    }
}