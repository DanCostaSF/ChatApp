package br.com.android.chatapp.ui.authscreen.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.android.chatapp.data.models.UserLoginModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginViewModel : ViewModel() {

    private val fire by lazy { FirebaseAuth.getInstance() }

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

        fire.signInWithEmailAndPassword(
            userLogin.email,
            userLogin.password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _navigateToMainScreen.value = true
                saveExceptLogin("Usuário logado com sucesso!")
            } else {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    saveExceptLogin("Usuário não está cadastrado.")
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    saveExceptLogin("A senha está incorreta!")
                } catch (e: Exception) {
                    saveExceptLogin("Erro ao logar o usuário" + e.message)
                    e.printStackTrace()
                }
            }
        }
    }
}