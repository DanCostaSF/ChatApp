package br.com.android.chatapp.data.repository

import br.com.android.chatapp.data.models.UserLoginModel
import br.com.android.chatapp.data.util.UiState
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FirebaseAuthRepositoryImp : FirebaseAuthRepository {

    private val fire by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var db: DocumentReference


    override suspend fun loginUser(
        user: UserLoginModel,
        result: (UiState<String>) -> Unit,
    ) {
        withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(
                user.email,
                user.password
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke(
                        UiState.Success("Usuário logado com sucesso!")
                    )
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidUserException) {
                        result.invoke(
                            UiState.Failure("Usuário não está cadastrado.")
                        )
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(
                            UiState.Failure("A senha está incorreta!")
                        )
                    } catch (e: Exception) {
                        result.invoke(
                            UiState.Failure("Erro ao logar o usuário" + e.message)
                        )
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override suspend fun createAccount(
        email: String,
        password: String,
        result: (UiState<String>) -> Unit,
    ) {
        withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(
                email,
                password
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userInfo = auth.currentUser?.uid
                    db = fire
                        .collection("users")
                        .document(userInfo.toString())
                    val obj = mutableMapOf<String, String>()
                    obj["userEmail"] = email
                    obj["userPassword"] = password
                    obj["userStatus"] = "Status"
                    obj["userName"] = "Nome"

                    db.set(obj)

                    result.invoke(
                        UiState.Success("Usuário registrado com sucesso!")
                    )

                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(
                            UiState.Failure("Digite uma senha mais forte!")
                        )
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(
                            UiState.Failure("Digite um e-mail válido!")
                        )
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(
                            UiState.Failure("Este e-mail já está cadastrado!")
                        )
                    } catch (e: java.lang.Exception) {
                        result.invoke(
                            UiState.Failure("Erro ao cadastrar usuário" + e.message)
                        )
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}