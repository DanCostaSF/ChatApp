package br.com.android.chatapp.data.models

data class ContactModel(
    val profileUid: String,
    val profileName: String,
    val profileEmail: String,
    val profileStatus: String,
    val profilePhoto: String,
    val isFriend: Boolean
)
