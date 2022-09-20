package com.whitstan.jwtlogin.ui.screen

sealed class Screen(val route: String) {
    object Splash: Screen("splash")
    object Login: Screen("login")
    object Home: Screen("home")
}