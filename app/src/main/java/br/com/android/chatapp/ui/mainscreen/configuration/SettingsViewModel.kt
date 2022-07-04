package br.com.android.chatapp.ui.mainscreen.configuration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.android.chatapp.data.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SettingsViewModel : ViewModel() {

    private lateinit var fstore: FirebaseFirestore
    private lateinit var db: DocumentReference
    private lateinit var userId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private var confInfo = arrayListOf<UserModel>()

    private val _userConf= MutableLiveData<List<UserModel>?>()
    val userConf: LiveData<List<UserModel>?>
        get() = _userConf

    fun initialization() {
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance().reference
            .child("$userId/profilePhoto")

        fstore = FirebaseFirestore.getInstance()
        db = fstore.collection("users").document(userId)

        setDataUser()
    }

    private fun setDataUser() {
        db.addSnapshotListener { value, error ->
            confInfo.clear()
            if (error != null) {
                Log.d("Error", "Unable to fetch data")
            } else {
                val obj = UserModel("",
                    value?.getString("userName").toString(),
                    value?.getString("userEmail").toString(),
                    value?.getString("userStatus").toString(),
                    value?.getString("userProfilePhoto").toString()
                )

                confInfo.add(obj)
                _userConf.postValue(confInfo)
            }
        }
    }

    fun buttonSave(name: String, email: String, status: String) {
        val obj = mutableMapOf<String, String>()
        obj["userName"] = name
        obj["userEmail"] = email
        obj["userStatus"] = status
        db.update(obj as Map<String, Any>).addOnSuccessListener {
            Log.d("Sucess", "Data Sucessfully Updated")

        }
    }

}