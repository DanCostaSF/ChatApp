package br.com.android.chatapp.data.repository

import android.util.Log
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object FirebaseContactsRepositoryImp : FirebaseContactsRepository {

    private val fire by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance()}

    override suspend fun getUsersData(
        result: (UiState<ArrayList<UserModel>>) -> Unit,
    ) {
        withContext(Dispatchers.Main) {
            val user = arrayListOf<UserModel>()

            fire.collection("users")
                .orderBy("userName")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        result.invoke(
                            UiState.Loading
                        )
                    } else {
                        if (!snapshot?.isEmpty!!) {
                            val userList = snapshot.documents
                            user.clear()
                            for (doc in userList) {
                                if (auth.currentUser!!.uid != doc.id
                                ) {
                                    val obj = UserModel(
                                        doc.id,
                                        doc.getString("userName").toString(),
                                        doc.getString("userEmail").toString(),
                                        doc.getString("userStatus").toString(),
                                        doc.getString("userProfilePhoto").toString()
                                    )
                                    user.add(obj)
                                    result.invoke(
                                        UiState.Success(user)
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }

    override suspend fun getFriendsData(
        result: (UiState<ArrayList<UserModel>>) -> Unit,
    ) {
        withContext(Dispatchers.Main) {
            val users = arrayListOf<UserModel>()
            val userid = auth.currentUser!!.uid
            fire.collection("users")
                .document(userid)
                .collection("friends")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        users.clear()
                        val listContact = it.documents
                        for (doc in listContact) {
                            if (doc.id != userid) {

                                val friendsID = doc.id

                                fire.collection("users")
                                    .document(friendsID)
                                    .addSnapshotListener { value, error ->
                                        if (error != null) {
                                            result.invoke(
                                                UiState.Loading
                                            )
                                        } else {

                                            val obj = UserModel(
                                                friendsID,
                                                value!!.getString("userName").toString(),
                                                value.getString("userEmail").toString(),
                                                value.getString("userStatus").toString(),
                                                value.getString("userProfilePhoto").toString()
                                            )
                                            users.add(obj)
                                            result.invoke(
                                                UiState.Success(users)
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
        }
    }

    override suspend fun searchUsers(
        queryTerm: String,
        result: (UiState<ArrayList<UserModel>>) -> Unit,
    ) {
        withContext(Dispatchers.IO) {
            val search = arrayListOf<UserModel>()
            if (queryTerm.isEmpty()) {
                result.invoke(
                    UiState.Loading
                )
            } else {
                fire.collection("users")
                    .orderBy("userName")
                    .startAt(queryTerm)
                    .limit(5)
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            result.invoke(
                                UiState.Loading
                            )
                        } else {
                            if (!snapshot?.isEmpty!!) {
                                val searchList = snapshot.documents
                                search.clear()
                                for (doc in searchList) {
                                    if (auth.currentUser!!.uid != doc.id) {
                                        val obj = UserModel(
                                            doc.id,
                                            doc.getString("userName").toString(),
                                            doc.getString("userEmail").toString(),
                                            doc.getString("userStatus").toString(),
                                            doc.getString("userProfilePhoto").toString()
                                        )
                                        search.add(obj)
                                        result.invoke(
                                            UiState.Success(search)
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }


}