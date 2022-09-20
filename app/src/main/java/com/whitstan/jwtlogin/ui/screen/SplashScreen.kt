package com.whitstan.jwtlogin.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.whitstan.jwtlogin.R
import com.whitstan.jwtlogin.domain.AuthResult
import com.whitstan.jwtlogin.ui.viewmodel.AuthViewModel

@Composable
fun SplashScreen(navController: NavController,
                 refreshToken: String?,
                 viewModel: AuthViewModel = hiltViewModel()) {

    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        if (refreshToken != null) viewModel.callRefreshCredentials(refreshToken)
        else navigateToLoginScreen(navController)

        viewModel.authResults.collect { result ->
            when (result) {
                is AuthResult.Authorized -> onAuthorized(navController)
                is AuthResult.RefreshFailed -> onRefreshFailed(context, navController)
                is AuthResult.NoInternet -> onNoInternet(context, navController)
                is AuthResult.UnknownError -> onUnknownError(context, navController)
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

private fun onAuthorized(navController: NavController){
    navController.navigate(route = Screen.Home.route){ popUpTo(0) }
}

private fun onRefreshFailed(context: Context, navController: NavController){
    showToast(context, context.getString(R.string.error_failedToExtendLogin))
    navigateToLoginScreen(navController)
}

private fun onNoInternet(context: Context, navController: NavController){
    showToast(context, context.getString(R.string.error_noInternet))
    navigateToLoginScreen(navController)
}

private fun onUnknownError(context: Context, navController: NavController){
    showToast(context, context.getString(R.string.error_unknown))
    navigateToLoginScreen(navController)
}

private fun navigateToLoginScreen(navController: NavController){
    navController.navigate(route = Screen.Login.route){ popUpTo(0) }
}

private fun showToast(context: Context, text: String){
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}