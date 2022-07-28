package br.com.android.chatapp.ui.mainscreen.configuration.bottomsheet

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.android.chatapp.data.repository.FirebaseStorageRepository
import br.com.android.chatapp.data.repository.FirebaseStorageRepositoryImp
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class BotSheetConfViewModel(private val repository: FirebaseStorageRepository) : ViewModel() {

    fun uploadImage(it: Bitmap?) {
        viewModelScope.launch {
            repository.uploadImage(it)
        }
    }
}

class BottomSheetConfigViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BotSheetConfViewModel::class.java)) {
            return BotSheetConfViewModel(FirebaseStorageRepositoryImp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}