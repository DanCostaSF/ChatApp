package br.com.android.chatapp.data.repository

import android.graphics.Bitmap
import android.util.Log
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object FirebaseStorageRepositoryImp : FirebaseStorageRepository {

    private val fire by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private lateinit var db: DocumentReference
    private var confInfo = arrayListOf<UserModel>()
    private lateinit var storageReference: StorageReference
    private lateinit var image: ByteArray


    override suspend fun setDataUser(result: (UiState<ArrayList<UserModel>>) -> Unit) {
        withContext(Dispatchers.IO) {
            val userId = auth.currentUser!!.uid
            db = fire.collection("users").document(userId)
            db.addSnapshotListener { value, error ->
                confInfo.clear()
                if (error != null) {
                    result.invoke(
                        UiState.Loading
                    )
                } else {
                    val obj = UserModel("",
                        value?.getString("userName").toString(),
                        value?.getString("userEmail").toString(),
                        value?.getString("userStatus").toString(),
                        value?.getString("userProfilePhoto").toString()
                    )

                    confInfo.add(obj)
                    result.invoke(
                        UiState.Success(confInfo)
                    )
                }
            }
        }
    }

    override suspend fun uploadImage(bitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        val userId = auth.currentUser!!.uid
        db = fire.collection("users").document(userId)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        image = baos.toByteArray()
        storageReference = storage.reference
            .child("$userId/profilePhoto")

        storageReference.putBytes(image).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                val obj = mutableMapOf<String, String>()
                obj["userProfilePhoto"] = it.toString()
                db.update(obj as Map<String, Any>).addOnSuccessListener {
                    Log.d("onSucess", "Profile Picture Uploaded")
                }
            }
        }
    }


    override suspend fun buttonSave(name: String, email: String, status: String) {
        withContext(Dispatchers.Default) {
            val obj = mutableMapOf<String, String>()
            obj["userName"] = name
            obj["userEmail"] = email
            obj["userStatus"] = status
            db.update(obj as Map<String, Any>).addOnSuccessListener {
                Log.d("Sucess", "Data Sucessfully Updated")
            }
        }
    }
}