package com.animekings.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Account tab entry point. Renders one of Login / Register / ForgotPassword
 * / Profile based on AuthViewModel's reactive session state — there is no
 * client-side "logged in" flag being faked here; it all follows
 * client.auth.sessionStatus under the hood.
 */
@Composable
fun AccountScreen(viewModel: AuthViewModel = viewModel()) {
    when {
        viewModel.isAuthenticated -> ProfileScreen(viewModel)
        viewModel.mode == AccountScreenMode.REGISTER -> RegisterScreen(viewModel)
        viewModel.mode == AccountScreenMode.FORGOT_PASSWORD -> ForgotPasswordScreen(viewModel)
        else -> LoginScreen(viewModel)
    }
}
