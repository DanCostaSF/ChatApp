package br.com.android.chatapp.data.repository

import android.util.Log
import br.com.android.chatapp.data.models.ChatModel
import br.com.android.chatapp.data.util.UiIntent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FirebaseChatRepositoryImp : FirebaseChatRepository {


    private val fire by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override suspend fun findFriends(
        result: (UiIntent<ArrayList<ChatModel>>) -> Unit,
    ) {
        val chatsInfo = arrayListOf<ChatModel>()

        chatsInfo.clear()
        withContext(Dispatchers.IO) {
            fire.collection("users")
                .document(auth.uid.toString())
                .collection("friends")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        val listContact = it.documents
                        for (doc in listContact) {
                            if (doc.id != auth.currentUser?.uid) {
                                val friendsID = doc.id
                                findChat(friendsID) { room ->
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


    private fun findChat(
        friend: String,
        result: (String) -> Unit,
    ) {
        val room = auth.uid.toString() + friend
        fire.collection("chats")
            .whereArrayContains("room", room)
            .addSnapshotListener { value, _ ->
                if (!value!!.isEmpty) {
                    val roomID = value.documents[0].id
                    result.invoke(roomID)
                }
            }
    }

}