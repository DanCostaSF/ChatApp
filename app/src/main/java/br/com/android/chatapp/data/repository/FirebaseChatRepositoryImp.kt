package br.com.android.chatapp.data.repository

import android.util.Log
import br.com.android.chatapp.data.models.ChatModel
import br.com.android.chatapp.data.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object FirebaseChatRepositoryImp : FirebaseChatRepository {

    private val chatsInfo = arrayListOf<ChatModel>()

    private val fire by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance()}

    override suspend fun findFriends(
        result: (UiState<ArrayList<ChatModel>>) -> Unit,
    ) {
        withContext(Dispatchers.Main) {
            fire.collection("users")
                .document(FirebaseAuth.getInstance().uid.toString())
                .collection("friends")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        val listContact = it.documents
                        chatsInfo.clear()
                        for (doc in listContact) {
                            if (doc.id != auth.currentUser?.uid) {

                                val friendsID = doc.id
                                findChat(friendsID)

                            }
                        }
                    }

                }
            delay(2000)
            result.invoke(
                UiState.Success(chatsInfo)
            )
        }
    }

    private fun findChat(
        friend: String,
    ) {
        val room = auth.uid.toString() + friend

        fire.collection("chats")
            .whereArrayContains("room", room)
            .addSnapshotListener { value, _ ->
                if (!value!!.isEmpty) {
                    val roomID = value.documents[0].id
                    getLastMessage(roomID, friend)

                }
            }
    }

    private fun getLastMessage(
        room: String,
        friend: String,
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
                                val message = it.getString("message").toString()
                                setChatData(friend, message)


                            }

                    }
                }
            }
    }

    private fun setChatData(
        friend: String,
        lastMessage: String,
    ) {

        fire.collection("users")
            .document(friend)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("teste", "error findfriend")
                } else {

                    val name = value!!.getString("userName").toString()
                    val photo = value.getString("userProfilePhoto").toString()

                    val obj =
                        ChatModel(
                            name,
                            lastMessage,
                            photo,
                            friend
                        )
                    chatsInfo.add(obj)
                }

            }
    }

}