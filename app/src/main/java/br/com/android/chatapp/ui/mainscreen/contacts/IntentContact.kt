package br.com.android.chatapp.ui.mainscreen.contacts

sealed class IntentContact {
    data class goToMessage(
        val profileUid: String,
        val profileName: String,
        val profilePhoto: String
    ) : IntentContact()
    data class goToAddFriend(
        val profileUid: String
    ) : IntentContact()
}