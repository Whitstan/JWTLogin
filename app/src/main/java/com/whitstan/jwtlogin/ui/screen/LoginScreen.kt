package com.whitstan.jwtlogin.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.whitstan.jwtlogin.R
import com.whitstan.jwtlogin.domain.AuthResult
import com.whitstan.jwtlogin.ui.AuthState
import com.whitstan.jwtlogin.ui.AuthUiEvent
import com.whitstan.jwtlogin.ui.viewmodel.AuthViewModel
import com.whitstan.jwtlogin.constants.TestTags

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    val state = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            when (result) {
                is AuthResult.Authorized -> onAuthorized(navController)
                is AuthResult.Unauthorized -> onAuthorizationFailed(context)
                is AuthResult.RefreshFailed -> onRefreshFailed(context)
                is AuthResult.NoInternet -> onNoInternet(context)
                is AuthResult.UnknownError -> onUnknownError(context)
            }
        }
    }

    if (!state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.INPUT_USERNAME),
                value = state.loginUsername,
                onValueChange = {
                    viewModel.onEvent(AuthUiEvent.LoginUsernameChanged(it))
                },
                singleLine = true,
                placeholder = {
                    Text(text = stringResource(id = R.string.placeholder_username))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.INPUT_PASSWORD),
                value = state.loginPassword,
                onValueChange = {
                    viewModel.onEvent(AuthUiEvent.LoginPasswordChanged(it))
                },
                singleLine = true,
                placeholder = {
                    Text(text = stringResource(id = R.string.placeholder_password))
                },
                visualTransformation = (
                        if (state.isPasswordVisible) VisualTransformation.None
                        else PasswordVisualTransformation()
                        ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    val image = if (state.isPasswordVisible) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description =
                        if (state.isPasswordVisible) stringResource(id = R.string.description_hidePassword)
                        else stringResource(id = R.string.description_showPassword)

                    IconButton(onClick = {
                        viewModel.state =
                            viewModel.state.copy(isPasswordVisible = !viewModel.state.isPasswordVisible)
                    }) {
                        Icon(imageVector = image, description)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                enabled = isInputValid(state),
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onEvent(AuthUiEvent.Login)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag(TestTags.BUTTON_LOGIN),
            ) {
                Text(text = stringResource(id = R.string.button_login))
            }
        }
    }
    else{
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(Modifier.testTag(TestTags.LOADER))
        }
    }
}

private fun isInputValid(state: AuthState): Boolean{
    return state.loginUsername.isNotBlank() && state.loginPassword.isNotBlank()
}

private fun onAuthorized(navController: NavController){
    navController.navigate(route = Screen.Home.route){ popUpTo(0) }
}

private fun onAuthorizationFailed(context: Context){
    Toast.makeText(context, context.getString(R.string.error_unauthorized), Toast.LENGTH_SHORT).show()
}

private fun onRefreshFailed(context: Context){
    Toast.makeText(context, context.getString(R.string.error_failedToExtendLogin), Toast.LENGTH_SHORT).show()
}

private fun onNoInternet(context: Context){
    Toast.makeText(context, context.getString(R.string.error_noInternet), Toast.LENGTH_SHORT).show()
}

private fun onUnknownError(context: Context){
    Toast.makeText(context, context.getString(R.string.error_unknown), Toast.LENGTH_SHORT).show()
}