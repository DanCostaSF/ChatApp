package br.com.android.chatapp.ui.authscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val _navigateToMainScreen = MutableLiveData<Boolean?>()
    val navigateToMainScreen: LiveData<Boolean?>
        get() = _navigateToMainScreen

    fun doneNavigation() {
        _navigateToMainScreen.value = null
    }

    fun userIsOn() {
        if(FirebaseAuth.getInstance().currentUser!= null) {
            _navigateToMainScreen.value = true
        }
    }
}