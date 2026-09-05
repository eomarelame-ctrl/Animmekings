package com.animekings.app.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animekings.app.data.models.Profile
import com.animekings.app.data.repository.AuthRepository
import io.github.jan.supabase.auth.SessionStatus
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

enum class AccountScreenMode { LOGIN, REGISTER, FORGOT_PASSWORD, PROFILE }

/**
 * Single source of truth for "am I logged in, and as whom". Screens read
 * `sessionStatus`/`profile`/`isAdmin` from here instead of each doing their
 * own currentUserOrNull() check, so the whole app reacts consistently the
 * moment a session appears, expires, or is signed out.
 */
class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    // Nullable/untyped-default on purpose: SessionStatus's concrete subtypes'
    // constructors vary slightly across supabase-kt versions, so we don't
    // guess one. null just means "haven't heard from the Auth plugin yet".
    var sessionStatus by mutableStateOf<SessionStatus?>(null)
        private set
    var profile by mutableStateOf<Profile?>(null)
        private set
    var mode by mutableStateOf(AccountScreenMode.LOGIN)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var infoMessage by mutableStateOf<String?>(null)
        private set

    val isAdmin: Boolean get() = profile?.role == "admin"
    val isAuthenticated: Boolean get() = sessionStatus is SessionStatus.Authenticated

    init {
        repo.sessionStatus
            .onEach { status ->
                sessionStatus = status
                when (status) {
                    is SessionStatus.Authenticated -> {
                        mode = AccountScreenMode.PROFILE
                        loadProfile()
                    }
                    else -> profile = null
                }
            }
            .launchIn(viewModelScope)
    }

    fun switchMode(newMode: AccountScreenMode) {
        errorMessage = null
        infoMessage = null
        mode = newMode
    }

    private fun loadProfile() {
        viewModelScope.launch {
            runCatching { repo.myProfile() }
                .onSuccess { profile = it }
        }
    }

    fun login(email: String, password: String) = runGuarded {
        repo.signIn(email, password)
        infoMessage = null
    }

    fun register(email: String, password: String, displayName: String) = runGuarded {
        repo.signUp(email, password, displayName)
        infoMessage = "تم إنشاء الحساب. تحقق من بريدك لتأكيده إذا كان مطلوبًا."
    }

    fun sendPasswordReset(email: String) = runGuarded {
        repo.sendPasswordReset(email)
        infoMessage = "تم إرسال رابط إعادة تعيين كلمة المرور إلى بريدك الإلكتروني."
    }

    fun logout() = runGuarded {
        repo.signOut()
        mode = AccountScreenMode.LOGIN
    }

    private fun runGuarded(block: suspend () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                block()
            } catch (e: Exception) {
                errorMessage = e.message ?: "حدث خطأ غير متوقع"
            } finally {
                isLoading = false
            }
        }
    }
}
