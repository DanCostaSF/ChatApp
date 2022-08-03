package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.ChatModel
import br.com.android.chatapp.data.util.UiIntent

interface FirebaseChatRepository {
    suspend fun findFriends(result: (UiIntent<ArrayList<ChatModel>>) -> Unit)
}