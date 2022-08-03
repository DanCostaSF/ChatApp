package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.MessageModel
import br.com.android.chatapp.data.util.UiIntent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object FirebaseMessageRepositoryImp : FirebaseMessageRepository {

    private var receiverRoom: String? = null
    private var senderRoom: String? = null
    private val messageInfo = arrayListOf<MessageModel>()
    private val fire by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }


    override suspend fun sendMessagesData(
        message: String,
        friendId: String,
    ) {
        val senderUid = auth.currentUser?.uid.toString()
        receiverRoom = senderUid + friendId
        senderRoom = friendId + senderUid

        fire.collection("chats")
            .whereArrayContains("room", receiverRoom.toString())
            .addSnapshotListener { value, _ ->
                if (value!!.isEmpty) {
                    createRoomIfDontExistis(friendId)
                } else {

                    val roomId = value.documents[0].id
                    val currentTimestamp = System.currentTimeMillis()
                    val simpleDateFormat = SimpleDateFormat("hh:mm")
                    val date = Date(currentTimestamp)
                    val timeNow = simpleDateFormat.format(date)

                    val messageObject = mutableMapOf<String, Any>().also {
                        it["message"] = message
                        it["messageSender"] = auth.currentUser?.uid.toString()
                        it["messageReceiver"] = friendId
                        it["messageTime"] = currentTimestamp.toString()
                        it["timeNow"] = timeNow.toString()
                    }

                    fire.collection("chats")
                        .document(roomId)
                        .collection("messages")
                        .document()
                        .set(messageObject)
                }
            }
    }


    override suspend fun fetchingMessages(
        friendId: String,
        result: (UiIntent<ArrayList<MessageModel>>) -> Unit,
    ) {

        val senderUid = auth.currentUser?.uid.toString()
        receiverRoom = senderUid + friendId

        fire.collection("chats")
            .whereArrayContains("room", receiverRoom.toString())
            .addSnapshotListener { value, _ ->
                if (value!!.isEmpty) {
                    result.invoke(
                        UiIntent.Loading
                    )
                } else {

                    val roomId = value.documents[0].id

                    fire.collection("chats")
                        .document(roomId)
                        .collection("messages")
                        .orderBy("messageTime", Query.Direction.ASCENDING)
                        .addSnapshotListener { chatSnapshot, exception ->

                            if (exception != null) {
                                result.invoke(
                                    UiIntent.Loading
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
                                            UiIntent.Success(messageInfo)
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
            it["room"] = arrayListOf(
                senderRoom.toString(),
                receiverRoom.toString()
            )
            it["uids"] = arrayListOf(
                auth.currentUser?.uid.toString(),
                friendId
            )
        }

        fire.collection("chats")
            .document()
            .set(obj1)

    }


}