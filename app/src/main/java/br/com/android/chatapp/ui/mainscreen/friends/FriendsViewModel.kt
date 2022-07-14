package br.com.android.chatapp.ui.mainscreen.friends

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.android.chatapp.data.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FriendsViewModel : ViewModel(){

        private lateinit var fstore: FirebaseFirestore
        private lateinit var auth: FirebaseAuth
        private lateinit var userid: String

        fun initialization() {
            auth = FirebaseAuth.getInstance()
            fstore = FirebaseFirestore.getInstance()
            userid = auth.currentUser!!.uid
        }

        private val _users = MutableLiveData<List<UserModel>?>()
        val usersList: MutableLiveData<List<UserModel>?>
            get() = _users

        fun getFriends() {
            val users = arrayListOf<UserModel>()
            fstore.collection("users").document(userid).collection("friends").get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        users.clear()
                        val listContact = it.documents
                        for (doc in listContact) {
                            if (doc.id != auth.currentUser?.uid) {

                                val friendsID = doc.id

                                fstore.collection("users").document(friendsID)
                                    .addSnapshotListener { value, error ->
                                        if (error != null) {
                                            Log.d("", "")
                                        } else {

                                            val obj = UserModel(
                                                friendsID,
                                                value!!.getString("userName").toString(),
                                                value.getString("userEmail").toString(),
                                                value.getString("userStatus").toString(),
                                                value.getString("userProfilePhoto").toString()
                                            )

                                            users.add(obj)
                                            usersList.postValue(users)
                                        }
                                    }
                            }
                        }
                    }
                }
        }
}