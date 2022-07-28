package br.com.android.chatapp.ui.authscreen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.data.repository.FirebaseAuthRepositoryImp
import java.lang.IllegalArgumentException

class RegisterViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(FirebaseAuthRepositoryImp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}