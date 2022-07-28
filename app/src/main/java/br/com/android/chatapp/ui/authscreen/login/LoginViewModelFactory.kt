package br.com.android.chatapp.ui.authscreen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.data.repository.FirebaseAuthRepositoryImp
import java.lang.IllegalArgumentException

class LoginViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(FirebaseAuthRepositoryImp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}