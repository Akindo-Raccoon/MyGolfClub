package com.ud.mygolfclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.ud.mygolfclub.ui.screen.HomeScreen
import com.ud.mygolfclub.ui.screen.LoginScreen
import com.ud.mygolfclub.ui.session.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var isLoggedIn by remember {
                mutableStateOf(SessionManager.isLoggedIn(context))
            }
            if (isLoggedIn) {
                HomeScreen(onLogout = { isLoggedIn = false })
            } else {
                LoginScreen(onLoginSuccess = { isLoggedIn = true })
            }
        }
    }
}