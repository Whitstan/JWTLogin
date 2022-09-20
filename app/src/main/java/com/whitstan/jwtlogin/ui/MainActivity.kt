package com.whitstan.jwtlogin.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.whitstan.jwtlogin.constants.PrefConsts.REFRESH_TOKEN
import com.whitstan.jwtlogin.ui.screen.HomeScreen
import com.whitstan.jwtlogin.ui.screen.LoginScreen
import com.whitstan.jwtlogin.ui.screen.Screen
import com.whitstan.jwtlogin.ui.screen.SplashScreen
import com.whitstan.jwtlogin.ui.theme.JWTLoginTheme
import com.whitstan.jwtlogin.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JWTLoginTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    val viewModel : AuthViewModel = hiltViewModel()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route,
                    ) {
                        composable(route = Screen.Splash.route){
                            SplashScreen(
                                navController = navController,
                                preferences.getString(REFRESH_TOKEN, null),
                                viewModel
                            )
                        }
                        composable(route = Screen.Login.route){
                            LoginScreen(navController, viewModel)
                        }
                        composable(route = Screen.Home.route) {
                            HomeScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}