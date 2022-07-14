package br.com.android.chatapp.ui.mainscreen.contacts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.android.chatapp.data.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ContactsViewModel : ViewModel() {

    private lateinit var fstore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userid: String

    private val _searchInfo = MutableLiveData<List<UserModel>?>()
    val searchInfo: LiveData<List<UserModel>?>
        get() = _searchInfo

    private val _users = MutableLiveData<List<UserModel>?>()
    val users: LiveData<List<UserModel>?>
        get() = _users


    fun initialization() {
        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        userid = auth.currentUser!!.uid
    }

    fun getUsers() {
        val user = arrayListOf<UserModel>()
        fstore.collection("users")
            .orderBy("userName").addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("onError", "ErrorSearchContact")
                } else {
                    if (!snapshot?.isEmpty!!) {
                        val userList = snapshot.documents
                        user.clear()
                        for (doc in userList) {
                            if (auth.currentUser!!.uid != doc.id) {
                                val obj = UserModel(
                                    doc.id,
                                    doc.getString("userName").toString(),
                                    doc.getString("userEmail").toString(),
                                    doc.getString("userStatus").toString(),
                                    doc.getString("userProfilePhoto").toString()
                                )
                                user.add(obj)
                                _users.postValue(user)
                            }
                        }
                    }
                }
            }
    }

    fun searchUsers(queryTerm: String) {
        val search = arrayListOf<UserModel>()
        fstore.collection("users")
            .orderBy("userName").startAt(queryTerm).limit(5)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("onError", "ErrorSearchContact")
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
                                _searchInfo.postValue(search)
                            }
                        }
                    }
                }
            }
    }
}