package br.com.android.chatapp.ui.mainscreen.message

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.android.chatapp.data.models.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class MessageViewModel : ViewModel() {

        private var receiverRoom: String? = null
        private var senderRoom: String? = null
        private lateinit var senderUid: String
        private lateinit var fstore: FirebaseFirestore
        private lateinit var receiverUid : String
        private lateinit var roomId: String

        private val _messages = MutableLiveData<List<MessageModel>?>()
        val messagesList: MutableLiveData<List<MessageModel>?>
            get() = _messages

        private val _errorEdtSend = MutableLiveData<Boolean?>()
        val errorEdtSend: LiveData<Boolean?>
            get() = _errorEdtSend

        private var messageInfo = arrayListOf<MessageModel>()

        fun initialization(receiverid: String) {

            senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

            fstore = FirebaseFirestore.getInstance()

            receiverUid = receiverid

            senderRoom = receiverUid + senderUid

            receiverRoom = senderUid + receiverUid

            fstore.collection("chats").whereArrayContains("room", receiverRoom.toString())
                .addSnapshotListener { value, _ ->
                    if (value!!.isEmpty) {
                        Log.i("tomanocu", "vazio")
                    } else {
                        roomId = value.documents[0].id
                        fetchingMessages(roomId)
                    }
                }

        }

        fun fabClick(message: String, friendId: String) {
            fstore.collection("chats").whereArrayContains("room", receiverRoom.toString())
                .addSnapshotListener { value, _ ->
                    if (value!!.isEmpty) {
                        Log.i("tomanocu", "vazio")
                        createRoomIfDontExistis(friendId)
                    } else {
                        roomId = value.documents[0].id
                        sendMessage(roomId, message)
                    }
                }
        }

        private fun sendMessage(id: String, message: String) {
            Log.i("tomanocu", message)
            if (message.isEmpty()) {
                _errorEdtSend.value = true
            } else {
                val currentTimestamp = System.currentTimeMillis().toString()
                val c = Calendar.getInstance(Locale.getDefault())
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                val timeNow = "$hour:$minute"
                val messageObject = mutableMapOf<String, Any>().also {
                    it["message"] = message
                    it["messageSender"] = senderUid
                    it["messageReceiver"] = receiverUid
                    it["messageTime"] = currentTimestamp
                    it["timeNow"] = timeNow

                }
                fstore.collection("chats").document(id).collection("messages").document()
                    .set(messageObject).addOnSuccessListener {
                        Log.i("tomanocu", "mensagem foi")
                    }
            }
        }

        private fun fetchingMessages(room: String) {
            fstore.collection("chats").document(room)
                .collection("messages")
                .orderBy("messageTime", Query.Direction.ASCENDING)
                .addSnapshotListener { chatSnapshot, exception ->
                    if (exception != null) {
                        Log.d("", "")
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
                                messagesList.postValue(messageInfo)
                            }

                        }
                    }
                }
        }

        private fun createRoomIfDontExistis(friendId: String) {
            val obj1 = mutableMapOf<String, ArrayList<String>>().also {
                it["room"] = arrayListOf(senderRoom.toString(), receiverRoom.toString())
                it["uids"] = arrayListOf(senderUid, friendId)
            }

            fstore.collection("chats").document()
                .set(obj1)
                .addOnSuccessListener {

                    Log.d("onSucess", "Sucessfuly Created Chat ")
                }
        }

        fun doneErrorEdtSend() {
            _errorEdtSend.value = null
        }
    }