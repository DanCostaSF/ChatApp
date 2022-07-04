package com.example.portifliozap.ui.mainscreen

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainScreenViewModel : ViewModel() {

    private val fire by lazy { FirebaseAuth.getInstance() }

    fun logoutUser() {
        try {
            fire.signOut()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
