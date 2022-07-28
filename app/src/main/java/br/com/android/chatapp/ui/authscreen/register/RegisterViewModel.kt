package br.com.android.chatapp.ui.authscreen.register


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.android.chatapp.data.repository.FirebaseAuthRepository
import br.com.android.chatapp.data.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.lang.Exception

class RegisterViewModel(private val repository: FirebaseAuthRepository) : ViewModel() {


    private val _navigateToMainScreen = MutableLiveData<Boolean?>()
    val navigateToMainScreen: LiveData<Boolean?>
        get() = _navigateToMainScreen

    private val _progressBar = MutableLiveData<Boolean?>()
    val progressBar: LiveData<Boolean?>
        get() = _progressBar

    private val _savedExceptRegister = MutableLiveData<String?>()
    val savedExcepRegister: LiveData<String?>
        get() = _savedExceptRegister

    fun doneSaveExceptRegister() {
        _savedExceptRegister.value = null
    }

    private fun saveExceptRegister(exception: String) {
        _savedExceptRegister.value = exception

    }

    fun doneProgressBar() {
        _progressBar.value = null
    }

    fun doneNavigation() {
        _navigateToMainScreen.value = null
    }

    fun createAccount(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            repository.createAccount(email, password) {
                when(it) {
                    is UiState.Success -> {
                        saveExceptRegister(it.data)
                        _progressBar.value = true
                        _navigateToMainScreen.value = true
                    }
                    is UiState.Failure -> {
                        _progressBar.value = false
                        saveExceptRegister(it.error.toString())
                    }
                    UiState.Loading -> UiState.Loading
                }
            }
        }

    }
}

