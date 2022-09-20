package com.whitstan.jwtlogin.ui.screen

import android.content.SharedPreferences
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.whitstan.jwtlogin.constants.PrefConsts
import com.whitstan.jwtlogin.constants.TestTags
import com.whitstan.jwtlogin.di.AppModule
import com.whitstan.jwtlogin.ui.MainActivity
import com.whitstan.jwtlogin.ui.theme.JWTLoginTheme
import com.whitstan.jwtlogin.ui.viewmodel.AuthViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import javax.inject.Inject
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class LoginScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var preferences: SharedPreferences

    @ExperimentalAnimationApi
    @Before
    fun setUp() {
        hiltRule.inject()

        composeRule.activity.setContent {
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
                                preferences.getString(PrefConsts.REFRESH_TOKEN, null),
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

    @Test
    fun loginButton_isDisabled() {
        composeRule.onNodeWithTag(TestTags.BUTTON_LOGIN).assertIsNotEnabled()

        composeRule.onNodeWithTag(TestTags.INPUT_USERNAME).performTextInput("testUser")
        composeRule.onNodeWithTag(TestTags.BUTTON_LOGIN).assertIsNotEnabled()

        composeRule.onNodeWithTag(TestTags.INPUT_USERNAME).performTextClearance()
        composeRule.onNodeWithTag(TestTags.BUTTON_LOGIN).assertIsNotEnabled()

        composeRule.onNodeWithTag(TestTags.INPUT_PASSWORD).performTextInput("testPassword")
        composeRule.onNodeWithTag(TestTags.BUTTON_LOGIN).assertIsNotEnabled()
    }

    @Test
    fun loginButton_isEnabled() {
        composeRule.onNodeWithTag(TestTags.INPUT_USERNAME).performTextClearance()
        composeRule.onNodeWithTag(TestTags.INPUT_USERNAME).performTextInput("testUser")
        composeRule.onNodeWithTag(TestTags.INPUT_PASSWORD).performTextInput("testPassword")
        composeRule.onNodeWithTag(TestTags.BUTTON_LOGIN).assertIsEnabled()
    }
}