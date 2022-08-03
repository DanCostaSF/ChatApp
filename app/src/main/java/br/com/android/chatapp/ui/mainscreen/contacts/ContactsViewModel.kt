package br.com.android.chatapp.ui.mainscreen.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.android.chatapp.data.models.ContactModel
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.repository.FirebaseContactsRepository
import br.com.android.chatapp.data.util.UiIntent
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: FirebaseContactsRepository) : ViewModel() {

    private val _searchInfo = MutableLiveData<UiIntent<List<ContactModel>>>()
    val searchInfo: LiveData<UiIntent<List<ContactModel>>>
        get() = _searchInfo

    private val _users = MutableLiveData<UiIntent<List<ContactModel>>>()
    val users: LiveData<UiIntent<List<ContactModel>>>
        get() = _users

    fun getUsers() {
        viewModelScope.launch {
            repository.getUsersData {
                _users.postValue(it)
            }
        }
    }

    fun addFriend(friendID: String) {
        viewModelScope.launch {
            repository.addFriend(friendID)
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