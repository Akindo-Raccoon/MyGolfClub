package com.ud.mygolfclub.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ud.mygolfclub.ui.screen.VM.LoginVM

@Composable
fun LoginScreen(viewModel: LoginVM = viewModel()){
    var idUser by remember { mutableStateOf("") }
    var pwdUser by remember { mutableStateOf("") }

    Column {

        TextField(
            value = idUser,
            onValueChange = {idUser = it},
            label = { Text("ID User") }
        )

        TextField(
            value = pwdUser,
            onValueChange = {pwdUser = it},
            label = {Text("Password User")}
        )

        Button(
            onClick = { viewModel.Login(idUser,pwdUser) }
        ) {
            Text("Login")
        }

    }
}