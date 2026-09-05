package com.animekings.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animekings.app.R
import com.animekings.app.data.repository.AuthRepository
import com.animekings.app.ui.theme.CardDark
import com.animekings.app.ui.theme.NeonPurple
import com.animekings.app.ui.theme.TextPrimary
import com.animekings.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * Shown only when AuthViewModel.sessionStatus is Authenticated. The role
 * badge/admin section read `viewModel.profile.role`, which is fetched from
 * the `profiles` table under RLS — a normal user's row always has role
 * 'user' server-side, so there is nothing for them to spoof client-side.
 */
@Composable
fun ProfileScreen(viewModel: AuthViewModel, authRepo: AuthRepository = remember { AuthRepository() }) {
    val scope = rememberCoroutineScope()
    var editing by remember { mutableStateOf(false) }
    var nameField by remember { mutableStateOf(viewModel.profile?.displayName ?: "") }
    var saveError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel.profile) {
        nameField = viewModel.profile?.displayName ?: ""
    }

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Text(
            text = viewModel.profile?.displayName?.takeIf { it.isNotBlank() } ?: "مستخدم Anime KINGS",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = authRepo.currentUserEmail ?: "", color = TextSecondary, fontSize = 13.sp)

        Spacer(Modifier.height(8.dp))
        RoleBadge(isAdmin = viewModel.isAdmin)

        Spacer(Modifier.height(20.dp))
        if (editing) {
            OutlinedTextField(
                value = nameField,
                onValueChange = { nameField = it },
                label = { Text(stringResource(R.string.display_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    scope.launch {
                        saveError = runCatching { authRepo.updateDisplayName(nameField.trim()) }
                            .exceptionOrNull()?.message
                        if (saveError == null) editing = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.save)) }
        } else {
            OutlinedButton(onClick = { editing = true }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.edit_profile))
            }
        }

        saveError?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = NeonPurple)
        }

        if (viewModel.isAdmin) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = "لوحة تحكم المسؤول (ستُبنى في مرحلة قادمة)",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.logout() },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.logout)) }
    }
}

@Composable
private fun RoleBadge(isAdmin: Boolean) {
    val label = if (isAdmin) stringResource(R.string.role_admin) else stringResource(R.string.role_user)
    Row {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 12.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isAdmin) NeonPurple else CardDark)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
