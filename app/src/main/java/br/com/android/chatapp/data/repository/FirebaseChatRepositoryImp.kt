package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.ChatModel
import br.com.android.chatapp.data.util.UiIntent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FirebaseChatRepositoryImp : FirebaseChatRepository {

    private val fire by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override suspend fun findChats(
        result: (UiIntent<ArrayList<ChatModel>>) -> Unit,
    ) {
        val chatsInfo = arrayListOf<ChatModel>()
        chatsInfo.clear()
        withContext(Dispatchers.IO) {
            fire.collection("users")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        val listContact = it.documents
                        for (doc in listContact) {
                            if (doc.id != auth.currentUser?.uid) {
                                val friendsID = doc.id
                                fire.collection("users")
                                    .document(friendsID)
                                    .addSnapshotListener { value, error ->
                                        if (error != null) {
                                            result.invoke(
                                                UiIntent.Loading
                                            )
                                        } else {
                                            val name =
                                                value!!.getString("userName")
                                                    .toString()
                                            val photo =
                                                value.getString("userProfilePhoto")
                                                    .toString()
                                            val email =
                                                value.getString("userEmail")
                                                    .toString()
                                            val obj = ChatModel(
                                                name,
                                                photo,
                                                email,
                                                friendsID,
                                                null
                                            )
                                            chatsInfo.add(obj)
                                            result.invoke(
                                                UiIntent.Success(chatsInfo)
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
        }
    }
}