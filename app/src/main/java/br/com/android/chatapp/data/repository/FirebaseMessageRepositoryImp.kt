package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.MessageModel
import br.com.android.chatapp.data.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.ArrayList

object FirebaseMessageRepositoryImp : FirebaseMessageRepository {

    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private val messageInfo = arrayListOf<MessageModel>()


    override suspend fun sendMessagesData(
        message: String,
        friendId: String,
    ) {
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        receiverRoom = senderUid + friendId
        senderRoom = friendId + senderUid

        FirebaseFirestore.getInstance()
            .collection("chats")
            .whereArrayContains("room", receiverRoom.toString())
            .addSnapshotListener { value, _ ->
                if (value!!.isEmpty) {
                    createRoomIfDontExistis(friendId)
                } else {

                    val roomId = value.documents[0].id
                    val currentTimestamp = System.currentTimeMillis().toString()
                    val c = Calendar.getInstance(Locale.getDefault())
                    val hour = c.get(Calendar.HOUR_OF_DAY)
                    val minute = c.get(Calendar.MINUTE)
                    val timeNow = "$hour:$minute"
                    val messageObject = mutableMapOf<String, Any>().also {
                        it["message"] = message
                        it["messageSender"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        it["messageReceiver"] = friendId
                        it["messageTime"] = currentTimestamp
                        it["timeNow"] = timeNow
                    }

                    FirebaseFirestore.getInstance()
                        .collection("chats")
                        .document(roomId)
                        .collection("messages")
                        .document()
                        .set(messageObject)
                }
            }
    }


    override suspend fun fetchingMessages(
        friendId: String,
        result: (UiState<ArrayList<MessageModel>>) -> Unit,
    ) {

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        receiverRoom = senderUid + friendId

        FirebaseFirestore.getInstance()
            .collection("chats")
            .whereArrayContains("room", receiverRoom.toString())
            .addSnapshotListener { value, _ ->
                if (value!!.isEmpty) {
                    result.invoke(
                        UiState.Loading
                    )
                } else {

                    val roomId = value.documents[0].id

                    FirebaseFirestore.getInstance()
                        .collection("chats")
                        .document(roomId)
                        .collection("messages")
                        .orderBy("messageTime", Query.Direction.ASCENDING)
                        .addSnapshotListener { chatSnapshot, exception ->

                            if (exception != null) {
                                result.invoke(
                                    UiState.Loading
                                )
                            } else {

                                messageInfo.clear()
                                if (!chatSnapshot?.isEmpty!!) {
                                    val listChat = chatSnapshot.documents

                                    for (chat in listChat) {

                                        val chatobj = MessageModel(
                                            chat.getString("messageSender").toString(),
                                            chat.getString("message").toString(),
                                            chat.getString("timeNow").toString(),
                                            chat.getString("messageTime").toString()
                                        )
                                        messageInfo.add(chatobj)
                                        result.invoke(
                                            UiState.Success(messageInfo)
                                        )
                                    }
                                }
                            }
                        }
                }
            }
    }

    override fun createRoomIfDontExistis(
        friendId: String,
    ) {
        val obj1 = mutableMapOf<String, ArrayList<String>>().also {
            it["room"] = arrayListOf(senderRoom.toString(), receiverRoom.toString())
            it["uids"] =
                arrayListOf(
                    FirebaseAuth.getInstance().currentUser?.uid.toString(),
                    friendId
                )
        }

        FirebaseFirestore.getInstance()
            .collection("chats")
            .document()
            .set(obj1)

    }


}