package br.com.android.chatapp.ui.mainscreen.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.data.repository.FirebaseContactsRepositoryImp
import br.com.android.chatapp.ui.mainscreen.message.MessageViewModel
import java.lang.IllegalArgumentException

class FriendsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
            return FriendsViewModel(FirebaseContactsRepositoryImp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}