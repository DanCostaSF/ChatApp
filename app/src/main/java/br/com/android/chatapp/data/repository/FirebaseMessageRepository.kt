package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.MessageModel
import br.com.android.chatapp.data.util.UiState

interface FirebaseMessageRepository  {
     suspend fun sendMessagesData(message : String, friendId : String)
     suspend fun fetchingMessages(friendId: String, result: (UiState<ArrayList<MessageModel>>) -> Unit)
     fun createRoomIfDontExistis(friendId: String)
}