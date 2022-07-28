package br.com.android.chatapp.ui.authscreen.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.android.chatapp.data.models.UserLoginModel
import br.com.android.chatapp.data.repository.FirebaseAuthRepository
import br.com.android.chatapp.data.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: FirebaseAuthRepository) : ViewModel() {


    private val _navigateToMainScreen = MutableLiveData<Boolean?>()
    val navigateToMainScreen: LiveData<Boolean?>
        get() = _navigateToMainScreen

    private val _savedExcepLogin = MutableLiveData<String?>()
    val savedExceptLogin: LiveData<String?>
        get() = _savedExcepLogin

    fun doneNavigatingMS() {
        _navigateToMainScreen.value = null
    }

    fun doneSaveExceptLogin() {
        _savedExcepLogin.value = null
    }

    private fun saveExceptLogin(exception: String) {
        _savedExcepLogin.value = exception
    }

    fun loginUser(userLogin: UserLoginModel) {
        viewModelScope.launch {
            repository.loginUser(userLogin) {
                when(it) {
                    is UiState.Success -> {
                        saveExceptLogin(it.data)
                        _navigateToMainScreen.value = true
                    }
                    is UiState.Failure -> {
                        saveExceptLogin(it.error.toString())
                    }
                    UiState.Loading -> UiState.Loading
                }
            }
        }
    }
}