package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.UserLoginModel
import br.com.android.chatapp.data.util.UiState

interface FirebaseAuthRepository {
    suspend fun loginUser(user: UserLoginModel, result: (UiState<String>) -> Unit)
    suspend fun createAccount(email: String, password: String, result: (UiState<String>) -> Unit)
}