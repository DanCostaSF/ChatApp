package br.com.android.chatapp.ui.mainscreen.chats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.android.chatapp.data.models.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatViewModel : ViewModel() {
    private lateinit var fstore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var room: String? = null
    private var roomID: String? = null
    private var chatsInfo = arrayListOf<ChatModel>()

    private val _chatsList= MutableLiveData<List<ChatModel>?>()
    val chatsList: LiveData<List<ChatModel>?>
        get() = _chatsList

    fun startFragment() {
        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
    }

    fun findChat() {

        fstore.collection("users").document(auth.uid.toString())
            .collection("friends").get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    chatsInfo.clear()
                    val listContact = it.documents
                    for (doc in listContact) {
                        if (doc.id != auth.currentUser?.uid) {

                            val friendsID = doc.id

                            room = auth.uid.toString() + friendsID

                            fstore.collection("chats")
                                .whereArrayContains("room", room.toString())
                                .addSnapshotListener { value, _ ->
                                    if (value!!.isEmpty) {
                                        Log.i("teste", "vazio findchat")
                                    } else {
                                        roomID = value.documents[0].id

                                        getLastMessage(roomID.toString(), friendsID)

                                    }
                                }
                        }
                    }
                }
            }
    }

    private fun getLastMessage(room: String, friend: String) {
        fstore.collection("chats").document(room)
            .collection("messages")
            .orderBy("messageTime", Query.Direction.DESCENDING)
            .addSnapshotListener { msgsnapshot, exception ->
                if (exception != null) {
                    Log.i("teste", "deu erro setchat")
                } else {
                    if (!msgsnapshot!!.isEmpty) {
                        val id = msgsnapshot.documents[0].id

                        fstore.collection("chats").document(room)
                            .collection("messages")
                            .document(id).get().addOnSuccessListener {
                                val message = it.getString("message").toString()

                                findFriend( friend, message)

                            }

                    }
                }
            }
    }

    private fun findFriend(friend: String, lastMessage: String) {

        fstore.collection("users").document(friend)
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
                    chatsInfo.clear()
                    chatsInfo.add(obj)
                    _chatsList.postValue(chatsInfo)

                }
            }
    }
}