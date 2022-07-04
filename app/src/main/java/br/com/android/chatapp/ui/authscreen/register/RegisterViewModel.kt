package br.com.android.chatapp.ui.authscreen.register


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class RegisterViewModel : ViewModel() {
    private lateinit var fauth: FirebaseAuth
    private lateinit var fstore: FirebaseFirestore
    private lateinit var db: DocumentReference

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


    fun initialization() {
        fauth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
    }

    fun createAccount(email: String, password: String) {
        fauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userInfo = fauth.currentUser?.uid
                db = fstore.collection("users").document(userInfo.toString())
                val obj = mutableMapOf<String, String>()
                obj["userEmail"] = email
                obj["userPassword"] = password
                obj["userStatus"] = "Status"
                obj["userName"] = "Nome"

                db.set(obj).addOnSuccessListener {
                    _progressBar.value = true
                }
                _navigateToMainScreen.value = true

            } else {
                _progressBar.value = false
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthWeakPasswordException) {
                    saveExceptRegister("Digite uma senha mais forte!")
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    saveExceptRegister("Por favor digite um e-mail válido!")
                } catch (e: FirebaseAuthUserCollisionException) {
                    saveExceptRegister("Este e-mail já está cadastrado!")
                } catch (e: Exception) {
                    saveExceptRegister("Erro ao cadastrar usuário" + e.message)
                    e.printStackTrace()
                }
            }
        }
    }
}

