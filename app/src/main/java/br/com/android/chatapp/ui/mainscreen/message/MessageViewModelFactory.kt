package br.com.android.chatapp.ui.mainscreen.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.data.repository.FirebaseMessageRepositoryImp
import java.lang.IllegalArgumentException

class MessageViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(FirebaseMessageRepositoryImp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
