package br.com.android.chatapp.ui.mainscreen.configuration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.repository.FirebaseStorageRepository
import br.com.android.chatapp.data.util.UiState

import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: FirebaseStorageRepository) : ViewModel() {

    private val _userConf = MutableLiveData<List<UserModel>?>()
    val userConf: LiveData<List<UserModel>?>
        get() = _userConf

    fun setDataUser() {
        viewModelScope.launch {
            repository.setDataUser {
                when (it) {
                    is UiState.Success -> {
                        _userConf.postValue(it.data)
                    }
                    is UiState.Failure -> UiState.Loading
                    UiState.Loading -> UiState.Loading
                }
            }
        }
    }

    fun buttonSave(name: String, email: String, status: String) {
        viewModelScope.launch {
            repository.buttonSave(name, email, status)
        }
    }

}