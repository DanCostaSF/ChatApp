package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.ChatModel
import br.com.android.chatapp.data.util.UiState

interface FirebaseChatRepository {
    suspend fun findFriends(result: (UiState<ArrayList<ChatModel>>) -> Unit)
}