package com.ud.mygolfclub.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ud.mygolfclub.ui.session.SessionManager
import com.ud.mygolfclub.ui.viewmodel.AuthState
import com.ud.mygolfclub.ui.viewmodel.LoginVM
import com.ud.mygolfclub.ui.theme.GolfGreen
import com.ud.mygolfclub.ui.theme.LightGreen

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginVM = viewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    var isRegisterMode by remember { mutableStateOf(false) }

    // Input Login
    var loginId by remember { mutableStateOf("") }
    var loginPwd by remember { mutableStateOf("") }
    var loginPwdVisible by remember { mutableStateOf(false) }

    // Input Register
    var regId by remember { mutableStateOf("") }
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPwd by remember { mutableStateOf("") }
    var regPwdVisible by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                SessionManager.saveSession(context, state.clientId, state.name, state.phone)
                onLoginSuccess()
                viewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8E9)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "⛳ Golf Club",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = GolfGreen
                )
                Spacer(Modifier.height(4.dp))
                AnimatedContent(targetState = isRegisterMode) { isRegister ->
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (isRegister) "Crear cuenta" else "Iniciar sesión",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GolfGreen
                        )

                        if (isRegister) {
                            GolfTextField(value = regId, onValueChange = { regId = it },
                                label = "Cédula", keyboardType = KeyboardType.Number)
                            GolfTextField(value = regName, onValueChange = { regName = it },
                                label = "Nombre completo")
                            GolfTextField(value = regPhone, onValueChange = { regPhone = it },
                                label = "Teléfono", keyboardType = KeyboardType.Phone)
                            GolfPasswordField(value = regPwd, onValueChange = { regPwd = it },
                                label = "Contraseña", visible = regPwdVisible,
                                onToggle = { regPwdVisible = !regPwdVisible })
                        } else {
                            GolfTextField(value = loginId, onValueChange = { loginId = it },
                                label = "Cédula", keyboardType = KeyboardType.Number)
                            GolfPasswordField(value = loginPwd, onValueChange = { loginPwd = it },
                                label = "Contraseña", visible = loginPwdVisible,
                                onToggle = { loginPwdVisible = !loginPwdVisible })
                        }

                        val isLoading = authState is AuthState.Loading
                        Button(
                            onClick = {
                                if (isRegister)
                                    viewModel.register(regId, regName, regPhone, regPwd)
                                else
                                    viewModel.login(loginId, loginPwd)
                            },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = GolfGreen)
                        ) {
                            if (isLoading)
                                CircularProgressIndicator(color = Color.White,
                                    modifier = Modifier.size(20.dp))
                            else
                                Text(if (isRegister) "Registrarse" else "Ingresar",
                                    color = Color.White)
                        }

                        TextButton(
                            onClick = {
                                isRegisterMode = !isRegisterMode
                                viewModel.resetState()
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = if (isRegister) "¿Ya tienes cuenta? Inicia sesión"
                                else "¿No tienes cuenta? Regístrate",
                                color = LightGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GolfTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GolfGreen,
            focusedLabelColor = GolfGreen
        )
    )
}

@Composable
private fun GolfPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggle: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (visible) VisualTransformation.None
        else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (visible) Icons.Default.Clear
                    else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = GolfGreen
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GolfGreen,
            focusedLabelColor = GolfGreen
        )
    )
}