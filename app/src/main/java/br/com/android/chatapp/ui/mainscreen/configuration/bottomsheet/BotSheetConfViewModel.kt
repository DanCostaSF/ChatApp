package br.com.android.chatapp.ui.mainscreen.configuration.bottomsheet

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.lang.IllegalArgumentException

class BotSheetConfViewModel : ViewModel() {

    private lateinit var auth: FirebaseAuth
    private lateinit var fstore: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var db: DocumentReference
    private lateinit var image: ByteArray
    private lateinit var storageReference: StorageReference

    fun initialization() {
        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        userId = auth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance().reference
            .child("$userId/profilePhoto")
        db = fstore.collection("users").document(userId)
    }

    fun uploadImage(it: Bitmap?) {
        val baos = ByteArrayOutputStream()
        it?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        image = baos.toByteArray()


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

}


class BottomSheetConfigViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BotSheetConfViewModel::class.java)) {
            return BotSheetConfViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}