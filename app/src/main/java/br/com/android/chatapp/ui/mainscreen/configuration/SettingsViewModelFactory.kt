package br.com.android.chatapp.ui.mainscreen.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.data.repository.FirebaseStorageRepositoryImp
import java.lang.IllegalArgumentException

class SettingsViewModelFactory : ViewModelProvider.Factory  {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(FirebaseStorageRepositoryImp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}