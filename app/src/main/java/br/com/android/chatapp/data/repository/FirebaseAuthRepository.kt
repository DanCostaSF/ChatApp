package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.UserLoginModel
import br.com.android.chatapp.data.util.UiIntent

interface FirebaseAuthRepository {
    suspend fun loginUser(user: UserLoginModel, result: (UiIntent<String>) -> Unit)
    suspend fun createAccount(email: String, password: String, result: (UiIntent<String>) -> Unit)
}