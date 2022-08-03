package br.com.android.chatapp.data.repository

import android.graphics.Bitmap
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.util.UiIntent

interface FirebaseStorageRepository {
    suspend fun setDataUser(result: (UiIntent<ArrayList<UserModel>>) -> Unit)
    suspend fun buttonSave(name: String, email: String, status: String)
    suspend fun uploadImage(bitmap: Bitmap?)
}