package br.com.android.chatapp.ui.mainscreen.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.data.repository.FirebaseContactsRepositoryImp

import java.lang.IllegalArgumentException

class ContactsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            return ContactsViewModel(FirebaseContactsRepositoryImp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}