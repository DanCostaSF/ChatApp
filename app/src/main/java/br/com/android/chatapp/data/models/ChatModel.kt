package br.com.android.chatapp.data.models

data class ChatModel(
    val receiver: String,
    val receiverImage: String,
    val email: String,
    val docId: String,
    val action: (() -> Unit)?
)
