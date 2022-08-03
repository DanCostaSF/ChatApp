package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.ContactModel
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.util.UiIntent

interface FirebaseContactsRepository {
    suspend fun getUsersData(result: (UiIntent<ArrayList<ContactModel>>) -> Unit)
    suspend fun getFriendsData(result: (UiIntent<ArrayList<UserModel>>) -> Unit)
    suspend fun addFriend(friend: String)
    suspend fun searchUsers(queryTerm: String, result: (UiIntent<ArrayList<ContactModel>>) -> Unit)
}