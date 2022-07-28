package br.com.android.chatapp.ui.mainscreen.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.data.repository.FirebaseChatRepositoryImp
import java.lang.IllegalArgumentException

class ChatViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(FirebaseChatRepositoryImp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}