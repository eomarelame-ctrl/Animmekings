package com.animekings.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animekings.app.R
import com.animekings.app.ui.theme.NeonPurple
import com.animekings.app.ui.theme.TextPrimary
import com.animekings.app.ui.theme.TextSecondary

@Composable
fun RegisterScreen(viewModel: AuthViewModel) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Text(
            text = stringResource(R.string.signup),
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text(stringResource(R.string.display_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.confirm_password)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        val mismatchText = stringResource(R.string.passwords_dont_match)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (password != confirmPassword) {
                    localError = mismatchText
                } else {
                    localError = null
                    viewModel.register(email.trim(), password, displayName.trim())
                }
            },
            enabled = !viewModel.isLoading && email.isNotBlank() && password.isNotBlank() && displayName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp))
            } else {
                Text(stringResource(R.string.signup))
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(
            onClick = { viewModel.switchMode(AccountScreenMode.LOGIN) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.have_account), color = TextSecondary)
        }

        (localError ?: viewModel.errorMessage)?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = NeonPurple)
        }
        viewModel.infoMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = TextSecondary)
        }
    }
}
