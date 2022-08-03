package br.com.android.chatapp.data.repository

import android.util.Log
import br.com.android.chatapp.data.models.ContactModel
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.util.UiIntent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


object FirebaseContactsRepositoryImp : FirebaseContactsRepository {

    private val fire by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override suspend fun getUsersData(
        result: (UiIntent<ArrayList<ContactModel>>) -> Unit,
    ) {
        withContext(Dispatchers.Main) {
            val user = arrayListOf<ContactModel>()
            fire.collection("users")
                .orderBy("userName")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        result.invoke(
                            UiIntent.Loading
                        )
                    } else {
                        if (!snapshot?.isEmpty!!) {
                            val userList = snapshot.documents
                            for (doc in userList) {
                                if (auth.currentUser!!.uid != doc.id
                                ) {
                                    isFriend(doc.id) {
                                        val obj = ContactModel(
                                            doc.id,
                                            doc.getString("userName").toString(),
                                            doc.getString("userEmail").toString(),
                                            doc.getString("userStatus").toString(),
                                            doc.getString("userProfilePhoto").toString(),
                                            it
                                        )
                                        user.add(obj)
                                        result.invoke(
                                            UiIntent.Success(user)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun isFriend(friendID: String, result: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("friends")
            .whereEqualTo(FieldPath.documentId(), friendID)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("onError", "ErrorSearchContact")
                } else {
                    if (!snapshot?.isEmpty!!) {
                        result.invoke(true)
                    } else {
                        result.invoke(false)
                    }
                }
            }
    }

    override suspend fun getFriendsData(
        result: (UiIntent<ArrayList<UserModel>>) -> Unit,
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
                                                UiIntent.Loading
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
                                                UiIntent.Success(users)
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
        }
    }

    override suspend fun addFriend(friend: String) {
        withContext(Dispatchers.IO) {
            val c = Calendar.getInstance(Locale.getDefault())
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timeNow = "$hour:$minute"

            val uid1 = auth.currentUser?.uid.toString()


            val obj = mutableMapOf<String, String>().also {
                it["time"] = timeNow
            }

            fire.collection("users")
                .document(uid1)
                .collection("friends")
                .document(friend)
                .set(obj)
        }
    }

    override suspend fun searchUsers(
        queryTerm: String,
        result: (UiIntent<ArrayList<ContactModel>>) -> Unit,
    ) {
        withContext(Dispatchers.IO) {
            val search = arrayListOf<ContactModel>()
            search.clear()
            if (queryTerm.isEmpty()) {
                result.invoke(
                    UiIntent.Loading
                )
            } else {
                fire.collection("users")
                    .orderBy("userName")
                    .startAt(queryTerm)
                    .limit(3)
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            result.invoke(
                                UiIntent.Loading
                            )
                        } else {
                            if (!snapshot?.isEmpty!!) {
                                val searchList = snapshot.documents
                                for (doc in searchList) {
                                    if (auth.currentUser!!.uid != doc.id) {
                                        isFriend(doc.id) {
                                            val obj = ContactModel(
                                                doc.id,
                                                doc.getString("userName").toString(),
                                                doc.getString("userEmail").toString(),
                                                doc.getString("userStatus").toString(),
                                                doc.getString("userProfilePhoto").toString(),
                                                it
                                            )
                                            search.add(obj)
                                            result.invoke(
                                                UiIntent.Success(search)
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
}