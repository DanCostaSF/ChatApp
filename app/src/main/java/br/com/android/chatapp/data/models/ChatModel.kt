package br.com.android.chatapp.data.models

data class ChatModel(
    val receiver      : String,
    val message       : String,
    val receiverImage : String,
    val docId         : String
)
