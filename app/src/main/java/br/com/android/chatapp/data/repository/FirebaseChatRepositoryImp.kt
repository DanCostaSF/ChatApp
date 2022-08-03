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
                                    getLastMessage(room) { lastmessage ->
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
                                                    val obj = ChatModel(
                                                        name,
                                                        lastmessage,
                                                        photo,
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

    private fun getLastMessage(
        room: String,
        message: (String) -> Unit,
    ) {

        fire.collection("chats")
            .document(room)
            .collection("messages")
            .orderBy("messageTime", Query.Direction.DESCENDING)
            .addSnapshotListener { msgsnapshot, exception ->
                if (exception != null) {
                    Log.d("teste", "deu erro setchat")
                } else {
                    if (!msgsnapshot!!.isEmpty) {
                        val id = msgsnapshot.documents[0].id

                        fire.collection("chats")
                            .document(room)
                            .collection("messages")
                            .document(id)
                            .get()
                            .addOnSuccessListener {
                                val lastmessage = it.getString("message").toString()
                                message.invoke(lastmessage)

                            }
                    }
                }
            }
    }
}