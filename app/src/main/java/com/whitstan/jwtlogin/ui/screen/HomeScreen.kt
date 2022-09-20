package com.whitstan.jwtlogin.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.whitstan.jwtlogin.R
import com.whitstan.jwtlogin.ui.viewmodel.AuthViewModel

@Composable
fun HomeScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val fullName = viewModel.state.fullName
    val role = viewModel.state.role

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(
                id = R.string.format_fullName, fullName ?:
                stringResource(id = R.string.error_notAvailable)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(
                id = R.string.format_role, role ?:
                stringResource(id = R.string.error_notAvailable)
            )
        )
    }
}