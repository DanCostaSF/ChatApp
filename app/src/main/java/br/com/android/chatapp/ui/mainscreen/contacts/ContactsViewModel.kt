package br.com.android.chatapp.ui.mainscreen.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.repository.FirebaseContactsRepository
import br.com.android.chatapp.data.util.UiState
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: FirebaseContactsRepository) : ViewModel() {

    private val _searchInfo = MutableLiveData<UiState<List<UserModel>>>()
    val searchInfo: LiveData<UiState<List<UserModel>>>
        get() = _searchInfo

    private val _users = MutableLiveData<UiState<List<UserModel>>>()
    val users: LiveData<UiState<List<UserModel>>>
        get() = _users

    fun getUsers() {
        viewModelScope.launch {
            repository.getUsersData {
                _users.postValue(it)
            }
        }
    }

    fun searchUsers(queryTerm: String) {
        viewModelScope.launch {
            repository.searchUsers(queryTerm) {
                _searchInfo.postValue(it)
            }
        }
    }
}