package com.domaners.everyminute.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.domaners.everyminute.ui.AuthState
import com.domaners.everyminute.ui.MainViewModel
import com.domaners.everyminute.ui.theme.EveryMinuteTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val authState by viewModel.authState.collectAsState()

    RegisterContent(
        authState = authState,
        onSignUp = viewModel::signUpWithEmail,
        onLoginClick = onLoginClick,
        onSignInWithCredential = viewModel::signInWithCredential
    )

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
        }
    }
}

@Composable
fun RegisterContent(
    authState: AuthState,
    onSignUp: (String, String) -> Unit,
    onLoginClick: () -> Unit,
    onSignInWithCredential: (AuthCredential) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    val webClientId = "1038368773962-h394f0educ5o4h83oqt72rih3p2q1bkq.apps.googleusercontent.com"

    fun handleGoogleSignIn() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                val credential = result.credential
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    onSignInWithCredential(firebaseCredential)
                }
            } catch (e: Exception) {
                Log.e("RegisterScreen", "Google Sign In failed", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Join EveryMinute Today",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (authState is AuthState.Error) {
            Text(
                text = authState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                onSignUp(email, password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading && email.isNotBlank() && password.length >= 6
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Register")
            }
        }

        TextButton(onClick = onLoginClick) {
            Text("Already have an account? Login")
        }

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { handleGoogleSignIn() },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        ) {
            Text("Sign up with Google")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    EveryMinuteTheme {
        RegisterContent(
            authState = AuthState.Idle,
            onSignUp = { _, _ -> },
            onLoginClick = { },
            onSignInWithCredential = { }
        )
    }
}
