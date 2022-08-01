package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.ContactModel
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.util.UiState

interface FirebaseContactsRepository {
    suspend fun getUsersData(result: (UiState<ArrayList<ContactModel>>) -> Unit)
    suspend fun getFriendsData(result: (UiState<ArrayList<UserModel>>) -> Unit)
    suspend fun addFriend(friend: String)
    suspend fun searchUsers(queryTerm: String, result: (UiState<ArrayList<UserModel>>) -> Unit)
}